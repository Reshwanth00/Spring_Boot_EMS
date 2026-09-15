package com.tyss.restdemo.service;

import com.tyss.restdemo.dto.*;
import com.tyss.restdemo.entity.EmployeeDocs;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {

    List<EmployeeResponse> getAllEmployees();

    EmployeeResponse getEmployeeById(Integer id);

    EmployeeResponse getEmployeeDetails(String email);

    EmployeeResponse saveEmployee(EmployeeRequest employeeRequest);

    EmployeeResponse updateEmployee(Integer id, EmployeeRequest employeeRequest);

    void deleteEmployee(Integer id);

    EmployeeV2Response saveEmployeeV2(EmployeeV2Request employeeV2Request);

    EmployeePageResponse getEmployeesV2(String search,
                                        Pageable pageable);

    EmployeeDocs saveEmployeeV3(EmployeeV2Request employeeV2Request, MultipartFile file);

    EmployeeResponse saveEmployeeV4(EmployeeRequest employeeRequest);

    EmployeeResponse getEmployeeByIdV4(Integer id);

    EmployeeResponse getEmployeeByEmailV4(String email);

    EmployeeResponse updateEmployeeV4( Integer id, EmployeeRequest employeeRequest);

    void deleteEmployeeV4(Integer id);
}

