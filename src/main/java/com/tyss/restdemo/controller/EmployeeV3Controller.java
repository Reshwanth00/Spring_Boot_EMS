package com.tyss.restdemo.controller;

import com.tyss.restdemo.dto.EmployeeV2Request;
import com.tyss.restdemo.dto.ResponseDto;
import com.tyss.restdemo.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v3/employee")
@RequiredArgsConstructor
public class EmployeeV3Controller {

    private final EmployeeService employeeService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseDto saveEmployee(
            @RequestPart("employee") EmployeeV2Request employeeV2Request,
            @RequestPart("file") MultipartFile file) {


        return ResponseDto.builder().error(false).data(employeeService.saveEmployeeV3(employeeV2Request,file)).message("uploaded successfully").build();
    }


}
