package com.tyss.restdemo.service;

import com.tyss.restdemo.dto.LeaveRequestDto;
import com.tyss.restdemo.dto.LeaveResponseDto;
import com.tyss.restdemo.entity.Leave;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LeaveService {
    LeaveResponseDto createLeave(LeaveRequestDto leaveRequestDto);

    Page<LeaveResponseDto> getAll(String status, Integer page, Integer size, String sort, String direction);

    LeaveResponseDto leaveAction(Integer id, String action);

    LeaveResponseDto deleteLeave(Integer empId, Integer leaveId);

    List<LeaveResponseDto> getById(Integer empId);
}
