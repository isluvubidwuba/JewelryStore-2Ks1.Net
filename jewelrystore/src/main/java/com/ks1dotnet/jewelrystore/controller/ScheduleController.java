package com.ks1dotnet.jewelrystore.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.AssignCountersForStaffDTO;
import com.ks1dotnet.jewelrystore.dto.AssignShiftForStaffDTO;
import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.entity.AssignCountersForStaff;
import com.ks1dotnet.jewelrystore.entity.AssignShiftForStaff;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IAssignShiftForStaffRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IAssignShiftForStaffService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;
import com.ks1dotnet.jewelrystore.utils.JwtUtilsHelper;

@RestController
@RequestMapping("${apiURL}/schedule")
@CrossOrigin(origins = "${domain}", allowCredentials = "true")
public class ScheduleController {
    @Value("${fileUpload.userPath}")
    private String filePath;

    @Value("${firebase.img-url}")
    private String url;
    @Autowired
    private IAssignShiftForStaffService assignShiftForStaffService;
    @Autowired
    private IAssignShiftForStaffRepository assignShiftForStaffRepository;
    @Autowired
    private IEmployeeService employService;

    @GetMapping("/events")
    public ResponseEntity<?> getEvents(@RequestParam String startDateStr, @RequestParam String endDateStr)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = formatter.parse(startDateStr);
        Date endDate = formatter.parse(endDateStr);

        List<AssignCountersForStaff> assignments = assignShiftForStaffService.getShiftsForWeek(startDate, endDate);

        Map<String, List<AssignCountersForStaffDTO>> eventsData = new HashMap<>();
        for (AssignCountersForStaff assignment : assignments) {
            AssignCountersForStaffDTO assignmentDTO = assignment.getDTO();
            AssignShiftForStaffDTO shiftDTO = assignmentDTO.getAssignShiftForStaffDTO();
            EmployeeDTO employeeDTO = shiftDTO.getEmployeeDTO();
            employeeDTO.setImage(url.trim() + filePath.trim() + employeeDTO.getImage());
            shiftDTO.setEmployeeDTO(employeeDTO);
            String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(shiftDTO.getDate());
            List<AssignCountersForStaffDTO> dayEvents = eventsData.getOrDefault(dayOfWeek, new ArrayList<>());
            assignmentDTO.setAssignShiftForStaffDTO(shiftDTO);
            dayEvents.add(assignmentDTO);
            eventsData.put(dayOfWeek, dayEvents);
        }

        return new ResponseEntity<>(new ResponseData(HttpStatus.OK, "Find events successfully", eventsData),
                HttpStatus.OK);
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assignShift(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam("counterId") Integer counterId,
            @RequestParam("employeeId") String employeeId) {
        try {
            assignShiftForStaffService.assignShift(date, counterId, employeeId);
            return new ResponseEntity<>(new ResponseData(HttpStatus.OK, "Assign shift successfully", null),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteSchedule(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam("counterId") Integer counterId,
            @RequestParam("employeeId") String employeeId) {
        try {
            assignShiftForStaffService.deleteAssignShift(date, counterId, employeeId);
            return new ResponseEntity<>(
                    new ResponseData(HttpStatus.OK, "Schedule deleted successfully", null),
                    HttpStatus.OK);
        } catch (ApplicationException e) {
            return new ResponseEntity<>(
                    new ResponseData(e.getStatus(), e.getErrorString(), null),
                    e.getStatus());
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "deleteAssignShift condition: " + e.getMessage(),
                            null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/checkin-status")
    public ResponseEntity<?> checkInStatus() {
        try {
            String id = JwtUtilsHelper.getAuthorizationByTokenType("at").getSubject();
            Employee employee = employService.findById(id);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date today = cal.getTime();

            Optional<AssignShiftForStaff> shiftOpt = assignShiftForStaffRepository.findByDateAndEmployee(today,
                    employee);

            if (shiftOpt.isPresent()) {
                AssignShiftForStaff shift = shiftOpt.get();
                if (shift.getCheckIn() == null) {
                    return new ResponseEntity<>(
                            new ResponseData(HttpStatus.OK, "Check-in status retrieved", false),
                            HttpStatus.OK);
                } else {
                    if (shift.getCheckOut() != null) {
                        return new ResponseEntity<>(
                                new ResponseData(HttpStatus.OK, "You check out already", "BAD_REQUEST"),
                                HttpStatus.OK);
                    }
                    return new ResponseEntity<>(
                            new ResponseData(HttpStatus.OK, "Check-in status retrieved", true),
                            HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(
                        new ResponseData(HttpStatus.OK, "No shift assigned for today", "NOT_FOUND"),
                        HttpStatus.OK);
            }
        } catch (ApplicationException e) {
            return new ResponseEntity<>(new ResponseData(e.getStatus(), e.getMessage(), null), e.getStatus());
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/checkin-checkout")
    public ResponseEntity<ResponseData> checkInOrCheckOut() {
        try {
            String id = JwtUtilsHelper.getAuthorizationByTokenType("at").getSubject();
            Employee employee = employService.findById(id);

            LocalDateTime nowLocalDateTime = LocalDateTime.now();
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date today = cal.getTime();
            LocalDateTime allowedCheckOutTime = nowLocalDateTime.withHour(8).withMinute(0).withSecond(0)
                    .withNano(0);
            Optional<AssignShiftForStaff> shiftOpt = assignShiftForStaffRepository.findByDateAndEmployee(today,
                    employee);

            if (shiftOpt.isPresent()) {
                AssignShiftForStaff shift = shiftOpt.get();

                if (shift.getCheckIn() == null) {
                    shift.setCheckIn(nowLocalDateTime);
                    if (nowLocalDateTime.isAfter(allowedCheckOutTime)) {
                        shift.setIsLate(true);
                    }
                    assignShiftForStaffService.saveShift(shift);

                    return new ResponseEntity<>(new ResponseData(HttpStatus.OK, "Check in successful", null),
                            HttpStatus.OK);

                } else {
                    shift.setCheckOut(nowLocalDateTime);
                    assignShiftForStaffService.saveShift(shift);

                    return new ResponseEntity<>(new ResponseData(HttpStatus.OK, "Check out successful", null),
                            HttpStatus.OK);

                    // if (nowLocalDateTime.isAfter(allowedCheckOutTime)) {

                    // } else {
                    // return new ResponseEntity<>(
                    // new ResponseData(HttpStatus.BAD_REQUEST, "Cannot check out before 5 PM",
                    // null),
                    // HttpStatus.BAD_REQUEST);
                    // }
                }
            } else {
                return new ResponseEntity<>(new ResponseData(HttpStatus.NOT_FOUND, "No shift assigned for today", null),
                        HttpStatus.NOT_FOUND);
            }
        } catch (ApplicationException e) {
            return new ResponseEntity<>(new ResponseData(e.getStatus(), e.getMessage(), null), e.getStatus());
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
