package com.tyss.restdemo.controller;

import com.tyss.restdemo.dto.EmployeeRequest;
import com.tyss.restdemo.dto.ResponseDto;
import com.tyss.restdemo.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v4/employee")
@RequiredArgsConstructor
public class EmployeeV4Controller {

    private final EmployeeService employeeService;
    private final JobOperator jobOperator;
    private final Job employeeBatchJob;


    // CREATE
    @PostMapping
    public ResponseDto saveEmployee(@Valid @RequestBody EmployeeRequest employeeRequest) {
        return ResponseDto.builder()
                .error(false)
                .data(employeeService.saveEmployeeV4(employeeRequest))
                .message("Employee created successfully")
                .build();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseDto getEmployeeById(@PathVariable Integer id) {
        return ResponseDto.builder()
                .error(false)
                .data(employeeService.getEmployeeByIdV4(id))
                .message("Employee fetched successfully")
                .build();
    }

    // READ BY EMAIL
    @GetMapping("/email/{email}")
    public ResponseDto getEmployeeByEmail(@PathVariable String email) {
        return ResponseDto.builder()
                .error(false)
                .data(employeeService.getEmployeeByEmailV4(email))
                .message("Employee fetched successfully")
                .build();
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseDto updateEmployee(@PathVariable Integer id, @Valid @RequestBody EmployeeRequest employeeRequest) {
        return ResponseDto.builder()
                .error(false)
                .data(employeeService.updateEmployeeV4(id, employeeRequest))
                .message("Employee updated successfully")
                .build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseDto deleteEmployee(@PathVariable Integer id) {

        employeeService.deleteEmployeeV4(id);

        return ResponseDto.builder()
                .error(false)
                .data(null)
                .message("Employee deleted successfully")
                .build();
    }

    @PostMapping("/batch")
    public String processEmployeeBatch() throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobOperator.start(employeeBatchJob, jobParameters);
        return "Job started with execution ID: " + execution.getId();
    }
}