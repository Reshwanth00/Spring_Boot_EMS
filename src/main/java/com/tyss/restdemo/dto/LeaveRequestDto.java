package com.tyss.restdemo.dto;

import com.tyss.restdemo.entity.enums.LeaveType;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequestDto {
    private Integer empId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
    private LeaveType leaveType;
}
