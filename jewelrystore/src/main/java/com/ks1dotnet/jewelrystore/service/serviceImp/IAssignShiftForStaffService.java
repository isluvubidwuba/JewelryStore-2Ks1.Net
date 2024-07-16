package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.Date;
import java.util.List;

import com.ks1dotnet.jewelrystore.entity.AssignCountersForStaff;
import com.ks1dotnet.jewelrystore.entity.AssignShiftForStaff;

public interface IAssignShiftForStaffService {

    List<AssignCountersForStaff> getShiftsForWeek(Date startDate, Date endDate);

    void assignShift(Date date, Integer counterId, String employeeId);

    void deleteAssignShift(Date date, Integer counterId, String employeeId);

    AssignShiftForStaff getShiftByDateAndEmployeeId(Date date, String employeeId);

    void saveShift(AssignShiftForStaff shift);

    public List<AssignCountersForStaff> getShiftsByUserId(Date startDate, Date endDate, String userID);

    List<String> getlistUserIdHaveSchedule(Date startDate, Date endDate);
}
