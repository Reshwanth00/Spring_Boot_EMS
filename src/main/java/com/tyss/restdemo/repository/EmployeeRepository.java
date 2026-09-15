package com.tyss.restdemo.repository;

import com.tyss.restdemo.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByMobileNo(String mobileNo);

    Optional<Employee> findByEmailOrMobileNo(String email, String mobileNo);

    boolean existsByEmailAndIdNot(String email, Integer id);

    boolean existsByMobileNoAndIdNot(String mobileNo, Integer id);

    Page<Employee> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name,
            String email,
            Pageable pageable);

//    @Query("""
//    SELECT DISTINCT e FROM Employee e
//    LEFT JOIN FETCH e.address
//    LEFT JOIN FETCH e.department
//    LEFT JOIN FETCH e.leaveList
//    LEFT JOIN FETCH e.leaveBalance
//    """)
//    List<Employee> findByJoinFetch();

    @EntityGraph(attributePaths = {"address","department","leaveList","leaveBalance"})
//    @EntityGraph(attributePaths = "address")
    List<Employee> findAllBy();
}


