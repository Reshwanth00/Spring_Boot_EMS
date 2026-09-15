package com.tyss.restdemo.service;

import com.tyss.restdemo.dto.LeaveRequestDto;
import com.tyss.restdemo.dto.LeaveResponseDto;
import com.tyss.restdemo.entity.Employee;
import com.tyss.restdemo.entity.Leave;
import com.tyss.restdemo.entity.LeaveBalance;
import com.tyss.restdemo.entity.enums.LeaveType;
import com.tyss.restdemo.entity.enums.Status;
import com.tyss.restdemo.exception.InsufficientLeaveBalance;
import com.tyss.restdemo.repository.EmployeeRepository;
import com.tyss.restdemo.repository.LeaveBalanceRepository;
import com.tyss.restdemo.repository.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    private static final Integer MAX_LEAVE_BALANCE = 10;

    // CREATE LEAVE
    @Override
    public LeaveResponseDto createLeave(LeaveRequestDto leaveRequestDto) {
        // Find employee
        Employee employee = employeeRepository.findById(leaveRequestDto.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + leaveRequestDto.getEmpId()));

        // Create Leave object
        Leave leave = new Leave();
        leave.setFromDate(leaveRequestDto.getFromDate());
        leave.setToDate(leaveRequestDto.getToDate());
        leave.setLeaveType(leaveRequestDto.getLeaveType());
        leave.setReason(leaveRequestDto.getReason());
        leave.setStatus(Status.PENDING); // New leave starts as PENDING

        // Calculate number of days (inclusive)
        int count = (int) ChronoUnit.DAYS.between(
                leaveRequestDto.getFromDate(),
                leaveRequestDto.getToDate()
        ) + 1;

        // Get employee's leave balance & check sufficiency
//        LeaveBalance leaveBalance = employee.getLeaveBalance();
//        Integer availableBalance = leaveBalance.getCount(leaveRequestDto.getLeaveType());

        if(leave.getLeaveType().equals(LeaveType.SICK)) {
            Integer availableBalance = employee.getSickLeave();
            if (count > availableBalance) {
                throw new InsufficientLeaveBalance("Insufficient " + leaveRequestDto.getLeaveType() + " leave balance");
            }
            employee.setSickLeave(availableBalance - count);
        }
        if(leave.getLeaveType().equals(LeaveType.CASUAL)) {
            Integer availableBalance = employee.getCasualLeave();
            if (count > availableBalance) {
                throw new InsufficientLeaveBalance("Insufficient " + leaveRequestDto.getLeaveType() + " leave balance");
            }
            employee.setCasualLeave(availableBalance - count);
        }



//        Integer availableBalance = leave.getLeaveType().equals(LeaveType.SICK) ? employee.getSickLeave() :
//                leave.getLeaveType().equals(LeaveType.CASUAL) ? employee.getCasualLeave() : employee.getSickLeave();


//        leaveBalance.setCount(leaveRequestDto.getLeaveType(), count);



        leave.setEmployee(employee);
        employee.getLeaveList().add(leave);

        // Save Leave
        Leave savedLeave = leaveRepository.save(leave);
        leaveRepository.save(leave);
        employeeRepository.save(employee);

        return convertToResponse(savedLeave);
    }


    // GET ALL + FILTER + PAGINATION + SORTING
    @Override
    public Page<LeaveResponseDto> getAll(
            String status,
            Integer page,
            Integer size,
            String sort,
            String direction
    ) {
        Sort sortBy = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortBy);
        Page<Leave> leaves;

        if (status == null || status.isBlank()) {
            leaves = leaveRepository.findAll(pageable);
        } else {
            Status leaveStatus = Status.valueOf(status.toUpperCase());
            leaves = leaveRepository.findByStatus(leaveStatus, pageable);
        }

        return leaves.map(this::convertToResponse);
    }

    // APPROVE / REJECT
    @Override
    public LeaveResponseDto leaveAction(Integer id, String action) {
        Leave leave = leaveRepository.findById(id).get();

        if (action.equalsIgnoreCase("APPROVE")) {
            leave.setStatus(Status.APPROVED);
        } else if (action.equalsIgnoreCase("REJECT")) {
            leave.setStatus(Status.REJECTED);
        }

        Leave updatedLeave = leaveRepository.save(leave);
        return convertToResponse(updatedLeave);
    }

    @Override
    public LeaveResponseDto deleteLeave(Integer empId, Integer leaveId) {
        Optional<Employee> employee = employeeRepository.findById(empId);
        if(employee.isEmpty()) {
            throw new RuntimeException("no employee with this id");
        }
        Optional<Leave> leave = leaveRepository.findById(leaveId);

        if(leave.isEmpty()) {
            throw new RuntimeException("no leave witch this leave");
        }
        if(!leave.get().getEmployee().getId().equals(employee.get().getId())){
            throw new RuntimeException("this is not your leave");
        }
        if(!leave.get().getStatus().equals(Status.PENDING)){
            throw new RuntimeException(("this leave has already taken action"));
        }
        leaveRepository.delete(leave.get());
        LeaveResponseDto leaveRequestDto = new LeaveResponseDto();

        return LeaveResponseDto.builder().empId(empId).fromDate(leave.get().getFromDate()).toDate(leave.get().getToDate()).reason(leave.get().getReason()).build();
    }

    @Override
    public List<LeaveResponseDto> getById(Integer empId) {
        return List.of();
    }

    // ENTITY -> DTO
    private LeaveResponseDto convertToResponse(Leave leave) {
        LeaveResponseDto response = new LeaveResponseDto();
        response.setLeaveId(leave.getId());
        response.setFromDate(leave.getFromDate());
        response.setToDate(leave.getToDate()); // or setToDate depending on your DTO generator
        response.setLeaveType(leave.getLeaveType());
        response.setReason(leave.getReason());
        response.setStatus(leave.getStatus());

        if (leave.getEmployee() != null) {
            response.setEmpId(leave.getEmployee().getId());
            response.setEmpName(leave.getEmployee().getName());
        }

        return response;
    }
}