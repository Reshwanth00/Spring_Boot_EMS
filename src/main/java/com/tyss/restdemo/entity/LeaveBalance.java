package com.tyss.restdemo.entity;

import com.tyss.restdemo.entity.enums.LeaveType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer casualLeave;

    private Integer sickLeave;

    private Integer annualLeave;

    private Integer maternityLeave;

    private Integer paternityLeave;

    @OneToOne
    private Employee employee;


    // GET BALANCE
    public Integer getCount(LeaveType leaveType) {

        if (LeaveType.CASUAL.equals(leaveType)) {
            return casualLeave;
        }

        if (LeaveType.SICK.equals(leaveType)) {
            return sickLeave;
        }

        if (LeaveType.ANNUAL.equals(leaveType)) {
            return annualLeave;
        }

        if (LeaveType.MATERNITY.equals(leaveType)) {
            return maternityLeave;
        }

        if (LeaveType.PATERNITY.equals(leaveType)) {
            return paternityLeave;
        }

        return 0;
    }


    // DEDUCT BALANCE
    public void setCount(LeaveType leaveType, Integer count) {

        if (LeaveType.CASUAL.equals(leaveType)) {

            this.casualLeave -= count;

        } else if (LeaveType.SICK.equals(leaveType)) {

            this.sickLeave -= count;

        } else if (LeaveType.ANNUAL.equals(leaveType)) {

            this.annualLeave -= count;

        } else if (LeaveType.MATERNITY.equals(leaveType)) {

            this.maternityLeave -= count;

        } else if (LeaveType.PATERNITY.equals(leaveType)) {

            this.paternityLeave -= count;
        }
    }

}
