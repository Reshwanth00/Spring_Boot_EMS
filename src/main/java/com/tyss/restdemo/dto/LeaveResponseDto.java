package com.tyss.restdemo.dto;

import com.tyss.restdemo.entity.enums.LeaveType;
import com.tyss.restdemo.entity.enums.Status;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveResponseDto {
    private String empName;
    private Integer leaveId;
    private LocalDate fromDate;
    private Status status;
    private LocalDate toDate;
    private String reason;
    private LeaveType leaveType;
    private Integer empId;
}
