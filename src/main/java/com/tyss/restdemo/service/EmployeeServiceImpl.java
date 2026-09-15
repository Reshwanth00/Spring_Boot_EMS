package com.tyss.restdemo.service;

import com.tyss.restdemo.dto.*;
import com.tyss.restdemo.entity.Department;
import com.tyss.restdemo.entity.Employee;
import com.tyss.restdemo.entity.EmployeeDocs;
import com.tyss.restdemo.exception.DuplicateResourceException;
import com.tyss.restdemo.exception.EmailNotFoundException;
import com.tyss.restdemo.exception.EmployeeNotFoundException;
import com.tyss.restdemo.repository.DepartmentRepository;
import com.tyss.restdemo.repository.EmployeeRepository;
import com.tyss.restdemo.repository.EmployeeDocsRepository;

import com.tyss.restdemo.repository.LeaveBalanceRepository;
import com.tyss.restdemo.util.EmployeeMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for managing Employee business logic.
 * <p>
 * Architectural & Optimization Notes:
 * 1. Resource Identity vs Business Identity: 'id' (Long) is the surrogate PK and REST resource identifier.
 * 'email' and 'mobileNo' are business attributes enforced via unique DB constraints.
 * 2. Optimized Create: Uses a single combined findByEmailOrMobileNo() query to validate duplicates
 * in 1 SELECT round-trip instead of 2 separate existence checks.
 * 3. Optimized Update: Evaluates if email or mobileNo have changed before running duplicate queries,
 * eliminating 2 DB round-trips during standard updates where unique attributes remain unchanged.
 * 4. Deferred Transactions: @Transactional is intentionally omitted in S1 and deferred to S3.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final LeaveBalanceRepository leaveBalanceRepository;

    private final DepartmentRepository departmentRepository;

    private final EmployeeDocsRepository employeeDocsRepository;

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        log.info("Fetching all employees");
        return employeeRepository.findAllBy().stream()
                .map(EmployeeMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse getEmployeeById(Integer id) {
        log.info("Fetching employee details for ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));
        return EmployeeMapper.entityToDto(employee);
    }

    @Override
    public EmployeeResponse getEmployeeDetails(String email) {
        log.info("Fetching employee details for email: {}", email);
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Employee not found with email: " + email));
        return EmployeeMapper.entityToDto(employee);
    }

    @Override
    public EmployeeResponse saveEmployee(EmployeeRequest employeeRequest) {
        log.info("Saving new employee with email: {}", employeeRequest.getEmail());

        // Single DB round-trip check for existing email or mobile number
        Optional<Employee> existingEmployee = employeeRepository.findByEmailOrMobileNo(
                employeeRequest.getEmail(), employeeRequest.getMobileNo());

        if (existingEmployee.isPresent()) {
            Employee matched = existingEmployee.get();
            if (matched.getEmail().equalsIgnoreCase(employeeRequest.getEmail())) {
                throw new DuplicateResourceException(
                        "Employee with email '" + employeeRequest.getEmail() + "' already exists");
            } else {
                throw new DuplicateResourceException(
                        "Employee with mobile number '" + employeeRequest.getMobileNo() + "' already exists");
            }
        }

        Employee employee = EmployeeMapper.dtoToEntity(employeeRequest);

        Employee savedEmployee = employeeRepository.save(employee);


        log.info("Successfully created employee with ID: {}", savedEmployee.getId());
        return EmployeeMapper.entityToDto(savedEmployee);
    }

    @Override
    public EmployeeResponse updateEmployee(Integer id, EmployeeRequest employeeRequest) {
        log.info("Updating employee with ID: {}", id);
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Cannot update. Employee not found with ID: " + id));

        // Only query DB for email duplicate if the email actually changed
        if (!existingEmployee.getEmail().equalsIgnoreCase(employeeRequest.getEmail())) {
            if (employeeRepository.existsByEmailAndIdNot(employeeRequest.getEmail(), id)) {
                throw new DuplicateResourceException(
                        "Another employee is already registered with email '" + employeeRequest.getEmail() + "'");
            }
        }

        // Only query DB for mobile duplicate if the mobile number actually changed
        if (!existingEmployee.getMobileNo().equalsIgnoreCase(employeeRequest.getMobileNo())) {
            if (employeeRepository.existsByMobileNoAndIdNot(employeeRequest.getMobileNo(), id)) {
                throw new DuplicateResourceException(
                        "Another employee is already registered with mobile number '" + employeeRequest.getMobileNo() + "'");
            }
        }

        EmployeeMapper.updateEntityFromDto(existingEmployee, employeeRequest);
        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        log.info("Successfully updated employee with ID: {}", updatedEmployee.getId());
        return EmployeeMapper.entityToDto(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Integer id) {
        log.info("Deleting employee with ID: {}", id);
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Cannot delete. Employee not found with ID: " + id));

        employeeRepository.delete(existingEmployee);
        log.info("Successfully deleted employee with ID: {}", id);
    }

    @Override
    @Transactional
    public EmployeeV2Response saveEmployeeV2(EmployeeV2Request employeeV2Request) {
        log.info("Saving new employee with email: {}", employeeV2Request.getEmail());

        // Single DB round-trip check for existing email or mobile number
        Optional<Employee> existingEmployee = employeeRepository.findByEmailOrMobileNo(
                employeeV2Request.getEmail(), employeeV2Request.getMobileNo());

        if (existingEmployee.isPresent()) {
            Employee matched = existingEmployee.get();
            if (matched.getEmail().equalsIgnoreCase(employeeV2Request.getEmail())) {
                throw new DuplicateResourceException("Employee with email '" + employeeV2Request.getEmail() + "' already exists");
            } else {
                throw new DuplicateResourceException("Employee with mobile number '" + employeeV2Request.getMobileNo() + "' already exists");
            }
        }

        Department department = departmentRepository.findById(employeeV2Request.getDeptId()).orElseThrow(() -> new RuntimeException("Department not found with ID: " + employeeV2Request.getDeptId()));

        Employee employee = EmployeeMapper.dtoToEntity(employeeV2Request);
        employee.setSickLeave(20);
        employee.setCasualLeave(20);

        employee.setDepartment(department);//assign dept to new Employee before saving

        Employee savedEmployee = employeeRepository.save(employee);

//        LeaveBalance leaveBalance = new LeaveBalance();
//        leaveBalance.setCasualLeave(12);
//        leaveBalance.setSickLeave(10);
//        leaveBalance.setAnnualLeave(15);
//        leaveBalance.setMaternityLeave(90);
//        leaveBalance.setPaternityLeave(15);
//
//        leaveBalance.setEmployee(employee);
//
//        employee.setLeaveBalance(leaveBalance);
//        LeaveBalance savedBalance = leaveBalanceRepository.save(leaveBalance);
//
//        savedEmployee.setLeaveBalance(savedBalance);
//
//        employeeRepository.save(savedEmployee);
//
//        log.info("Successfully created employee with ID: {}", savedEmployee.getId());
        return EmployeeMapper.entityToDtoV2(savedEmployee);
    }

    @Override
    public EmployeePageResponse getEmployeesV2(String search, Pageable pageable) {
        Page<Employee> employeePage;

        if (search == null || search.isBlank()) {
            employeePage = employeeRepository.findAll(pageable);
        } else {
            employeePage =
                    employeeRepository
                            .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                                    search,
                                    search,
                                    pageable);
        }

        EmployeePageResponse pageResponse = new EmployeePageResponse();

        List<EmployeeResponse> employeesResponseList = new ArrayList<>();

        List<Employee> employeeEntityList = employeePage.getContent();//get the employee entity list from page object
        //convert employee entity to dto one by one
        for (Employee employee : employeeEntityList) {

            EmployeeResponse employeeResponse = new EmployeeResponse();//dto

            employeeResponse.setId(employee.getId());
            employeeResponse.setName(employee.getName());
            employeeResponse.setEmail(employee.getEmail());

            employeesResponseList.add(employeeResponse);
        }

        pageResponse.setEmployeeReponseList(employeesResponseList);
        pageResponse.setPage(employeePage.getNumber());
        pageResponse.setSize(employeePage.getSize());
        pageResponse.setTotalElements(employeePage.getTotalElements());
        pageResponse.setTotalPages(employeePage.getTotalPages());
        return pageResponse;
    }

    @Override
    @Transactional(rollbackFor = IOException.class,timeout = 10)
    public EmployeeDocs saveEmployeeV3(EmployeeV2Request employeeV2Request, MultipartFile file) {

        // 1. Save employee
        saveEmployeeV2(employeeV2Request);

        // 2. Fetch employee
        Employee employee = employeeRepository.findByEmail(employeeV2Request.getEmail())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // 3. Initialize and save EmployeeDocs to generate an ID
        EmployeeDocs employeeDocs = EmployeeDocs.builder()
                .fileName(file.getOriginalFilename())
                .location("TEMP")
                .employee(employee)
                .build();

        employeeDocs = employeeDocsRepository.save(employeeDocs);

        // 4. Resolve target directory path
        Path directory = Paths.get(
                "src/main/resources/files",
                String.valueOf(employee.getId()),
                String.valueOf(employeeDocs.getId())
        );

        try {
            // 5. Create directories and file path
            Files.createDirectories(directory);
            Path filePath = directory.resolve(file.getOriginalFilename());

            // 6. Save physical file to disk
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 7. Update and persist final location
            employeeDocs.setLocation(filePath.toString());
            return employeeDocsRepository.save(employeeDocs);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    @Override
    public EmployeeResponse saveEmployeeV4(EmployeeRequest employeeRequest) {

        log.info("Saving new employee with email: {}", employeeRequest.getEmail());

        // Check whether email or mobile number already exists
        Optional<Employee> existingEmployee =
                employeeRepository.findByEmailOrMobileNo(
                        employeeRequest.getEmail(),
                        employeeRequest.getMobileNo());

        if (existingEmployee.isPresent()) {

            Employee matched = existingEmployee.get();

            if (matched.getEmail().equalsIgnoreCase(employeeRequest.getEmail())) {
                throw new DuplicateResourceException(
                        "Employee with email '" +
                                employeeRequest.getEmail() +
                                "' already exists");
            } else {
                throw new DuplicateResourceException(
                        "Employee with mobile number '" +
                                employeeRequest.getMobileNo() +
                                "' already exists");
            }
        }

        // Convert DTO → Entity
        Employee employee = EmployeeMapper.dtoToEntity(employeeRequest);

        // Save employee
        Employee savedEmployee = employeeRepository.save(employee);

        log.info("Successfully created employee with ID: {}",
                savedEmployee.getId());

        // Entity → Response DTO
        return EmployeeMapper.entityToDto(savedEmployee);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "employees", key = "#id")
    public EmployeeResponse getEmployeeByIdV4(Integer id) {

        log.info("Fetching employee from database for ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found with ID: " + id));

        return EmployeeMapper.entityToDto(employee);
    }


    @Override
    @Cacheable(value = "employeesByEmail", key = "#email")
    public EmployeeResponse getEmployeeByEmailV4(String email) {

        log.info("Fetching employee from database for email: {}", email);

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EmailNotFoundException(
                                "Employee not found with email: " + email));

        return EmployeeMapper.entityToDto(employee);
    }



    /*


    Even though this exception normally causes a rollback, I want this transaction to COMMIT.


     */

    @Override
    @CachePut(value = "employees", key = "#id")
    @Transactional(noRollbackFor = DuplicateResourceException.class)
    public EmployeeResponse updateEmployeeV4(
            Integer id,
            EmployeeRequest employeeRequest) {

        log.info("Updating employee with ID: {}", id);

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Cannot update. Employee not found with ID: " + id));

        // Check email only if it has changed
        if (!existingEmployee.getEmail()
                .equalsIgnoreCase(employeeRequest.getEmail())) {

            if (employeeRepository.existsByEmailAndIdNot(
                    employeeRequest.getEmail(), id)) {

                throw new DuplicateResourceException(
                        "Another employee is already registered with email '" +
                                employeeRequest.getEmail() + "'");
            }
        }

        // Check mobile only if it has changed
        if (!existingEmployee.getMobileNo()
                .equalsIgnoreCase(employeeRequest.getMobileNo())) {

            if (employeeRepository.existsByMobileNoAndIdNot(
                    employeeRequest.getMobileNo(), id)) {

                throw new DuplicateResourceException(
                        "Another employee is already registered with mobile number '" +
                                employeeRequest.getMobileNo() + "'");
            }
        }

        // Update entity
        EmployeeMapper.updateEntityFromDto(
                existingEmployee,
                employeeRequest);

        // Save updated employee
        Employee updatedEmployee =
                employeeRepository.save(existingEmployee);

        log.info("Successfully updated employee with ID: {}",
                updatedEmployee.getId());

        return EmployeeMapper.entityToDto(updatedEmployee);
    }


    @Override
    @CacheEvict(value = "employees", key = "#id")
    public void deleteEmployeeV4(Integer id) {

        log.info("Deleting employee with ID: {}", id);

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Cannot delete. Employee not found with ID: " + id));

        employeeRepository.delete(existingEmployee);

        log.info("Successfully deleted employee with ID: {}", id);
    }
}


