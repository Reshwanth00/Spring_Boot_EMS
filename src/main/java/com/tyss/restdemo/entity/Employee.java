package com.tyss.restdemo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.List;

/**
 * Architectural Note on Lombok Usage:
 * Selective annotations (@Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @Builder)
 * are used instead of @Data. @Data automatically generates equals(), hashCode(), and toString()
 * methods which can cause issues with JPA entities (such as LazyInitializationException or infinite recursion).
 */
@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "mobile_no", nullable = false, unique = true, length = 15)
    private String mobileNo;

    @Column(name = "password")
    private String password;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Address address;

    @ManyToOne
    private Department department;

    @OneToMany(mappedBy = "employee")
    private List<Leave> leaveList;

//    @OneToOne
//    private LeaveBalance leaveBalance;

    @Column(name = "sick_leave", nullable = false,columnDefinition = "integer default 20")
    private Integer sickLeave;

    @Column(name = "casual_leave", nullable = false,columnDefinition = "integer default 20")
    private Integer casualLeave;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeDocs> employeeDocs;
}

