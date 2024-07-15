package com.ks1dotnet.jewelrystore.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.entity.AssignCountersForStaff;
import com.ks1dotnet.jewelrystore.entity.AssignShiftForStaff;
import com.ks1dotnet.jewelrystore.entity.Counter;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.repository.IAssignCountersForStaffRepository;
import com.ks1dotnet.jewelrystore.repository.IAssignShiftForStaffRepository;
import com.ks1dotnet.jewelrystore.repository.ICounterRepository;
import com.ks1dotnet.jewelrystore.repository.IEmployeeRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IAssignShiftForStaffService;
import com.ks1dotnet.jewelrystore.utils.JwtUtilsHelper;

import jakarta.transaction.Transactional;

@Service
public class AssignShiftForStaffService implements IAssignShiftForStaffService {

    @Autowired
    private IAssignShiftForStaffRepository assignShiftForStaffRepository;
    @Autowired
    private IAssignCountersForStaffRepository assignCountersForStaffRepository;

    @Autowired
    private IEmployeeRepository iEmployeeRepository;
    @Autowired
    private ICounterRepository counterRepository;

    @Override
    public List<AssignCountersForStaff> getShiftsForWeek(Date startDate, Date endDate) {
        try {
            String id = JwtUtilsHelper.getAuthorizationByTokenType("at").getSubject();
            Employee employee = iEmployeeRepository.findById(id).orElseThrow(
                    () -> new ApplicationException("User not exist!", HttpStatus.NOT_FOUND));
            EmployeeDTO emp = employee.getDTO();
            if (emp.getRole().getId() == 3) {
                return assignShiftForStaffRepository.findAllByDateBetweenAndEmployeeId(startDate, endDate, emp.getId());
            } else {
                return assignShiftForStaffRepository.findAllByDateBetween(startDate, endDate);
            }

        } catch (ApplicationException e) {
            throw new ApplicationException("Find schedule error:  " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        }
    }

    @Transactional
    @Override
    public void assignShift(Date date, Integer counterId, String employeeId) {
        try {
            // Set giờ, phút, giây và mili giây của ngày hiện tại về 0
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date today = cal.getTime();

            // Kiểm tra nếu ngày được gán nằm trong quá khứ
            if (date.before(today)) {
                throw new ApplicationException("Cannot assign shift for a past date", HttpStatus.BAD_REQUEST);
            }

            Employee employee = iEmployeeRepository.findById(employeeId)
                    .orElseThrow(
                            () -> new ApplicationException("Can Not Found ID :" + employeeId, HttpStatus.NOT_FOUND));
            Counter counter = counterRepository.findById(counterId)
                    .orElseThrow(() -> new ApplicationException("Counter not found" + counterId, HttpStatus.NOT_FOUND));

            // Kiểm tra xem AssignShiftForStaff đã tồn tại cho ngày này chưa
            Optional<AssignShiftForStaff> existingShiftOpt = assignShiftForStaffRepository.findByDateAndEmployee(date,
                    employee);

            AssignShiftForStaff shift;
            boolean counterAlreadyAssigned = false;
            if (existingShiftOpt.isPresent()) {
                shift = existingShiftOpt.get();
                // Kiểm tra xem counter đã được gán cho shift này chưa
                counterAlreadyAssigned = shift.getListAssignCountersForStaff().stream()
                        .anyMatch(assignCounter -> assignCounter.getCounter().getId() == counterId);

            } else {
                shift = new AssignShiftForStaff();
                shift.setDate(date);
                shift.setCheckIn(null);
                shift.setCheckOut(null);
                shift.setIsLate(null);
                shift.setEmployee(employee);
                shift = assignShiftForStaffRepository.save(shift);
            }

            if (!counterAlreadyAssigned) {
                AssignCountersForStaff assignCounter = new AssignCountersForStaff();
                assignCounter.setAssignShiftForStaff(shift);
                assignCounter.setCounter(counter);
                assignCountersForStaffRepository.save(assignCounter);
            }
        } catch (ApplicationException e) {
            throw new ApplicationException("assignShift failed: " + e.getMessage(), e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException("assignShift condition : " + e.getMessage(), "assignShift failed!");
        }
    }

    @Transactional
    @Override
    public void deleteAssignShift(Date date, Integer counterId, String employeeId) {
        try {
            // Find the existing shift for the given date and employee
            AssignShiftForStaff shift = assignShiftForStaffRepository.findByDateAndEmployee(date, iEmployeeRepository
                    .findById(employeeId)
                    .orElseThrow(
                            () -> new ApplicationException("Employee not found: " + employeeId, HttpStatus.NOT_FOUND)))
                    .orElseThrow(
                            () -> new ApplicationException("Shift not found for date: " + date, HttpStatus.NOT_FOUND));

            // Find the counter assignment within the shift
            AssignCountersForStaff assignCounter = shift.getListAssignCountersForStaff().stream()
                    .filter(ac -> ac.getCounter().getId() == counterId)
                    .findFirst()
                    .orElseThrow(
                            () -> new ApplicationException("Counter assignment not found: " + counterId,
                                    HttpStatus.NOT_FOUND));

            // Remove the counter assignment
            assignCountersForStaffRepository.delete(assignCounter);
            // Refresh the shift entity to reflect changes in the database
            shift.getListAssignCountersForStaff().remove(assignCounter);
            // If the shift has no more counter assignments, delete the shift
            if (shift.getListAssignCountersForStaff().isEmpty()) {
                assignShiftForStaffRepository.delete(shift);
            }

        } catch (ApplicationException e) {
            throw new ApplicationException("deleteAssignShift failed: " + e.getMessage(), e.getErrorString(),
                    e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException("deleteAssignShift condition: " + e.getMessage(),
                    "deleteAssignShift failed!");
        }
    }

    @Override
    public AssignShiftForStaff getShiftByDateAndEmployeeId(Date date, String employeeId) {
        try {
            Employee employee = iEmployeeRepository.findById(employeeId)
                    .orElseThrow(
                            () -> new ApplicationException("Can Not Found ID :" + employeeId, HttpStatus.NOT_FOUND));
            return assignShiftForStaffRepository.findByDateAndEmployee(date, employee).orElseThrow(
                    () -> new ApplicationException("Can Not Found ID :" + employeeId, HttpStatus.NOT_FOUND));
        } catch (ApplicationException e) {
            throw new ApplicationException("Find schedule error:  " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        }
    }

    @Override
    public void saveShift(AssignShiftForStaff shift) {
        assignShiftForStaffRepository.save(shift);
    }

}
