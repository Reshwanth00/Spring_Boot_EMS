package com.tyss.restdemo.entity;


import com.tyss.restdemo.entity.enums.LeaveType;
import com.tyss.restdemo.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@Table(name = "employee_leaves")
@NoArgsConstructor
@AllArgsConstructor
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private LocalDate fromDate;

    private LocalDate toDate;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    private String reason;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    private Employee employee;

}
