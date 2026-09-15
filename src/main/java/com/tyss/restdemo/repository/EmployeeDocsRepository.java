package com.tyss.restdemo.repository;

import com.tyss.restdemo.entity.EmployeeDocs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDocsRepository extends JpaRepository<EmployeeDocs,Integer> {
}
