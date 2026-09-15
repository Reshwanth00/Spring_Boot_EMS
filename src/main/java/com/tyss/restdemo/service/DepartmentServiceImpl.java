package com.tyss.restdemo.service;

import com.tyss.restdemo.dto.DepartmentResponseDto;
import com.tyss.restdemo.entity.Department;
import com.tyss.restdemo.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService{

    private final DepartmentRepository departmentRepository;

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Override
    public DepartmentResponseDto saveDeartment(String departmentName) {
        log.debug("this is the department{}",departmentName);
        Department department = new Department();
        department.setDeptName(departmentName);
        Department dbDepartEntity = departmentRepository.save(department);
        return DepartmentResponseDto.builder()
                .id(dbDepartEntity.getId())
                .deptName(dbDepartEntity.getDeptName())
                .build();
    }
}
