package com.tyss.restdemo.controller;

import com.tyss.restdemo.dto.LeaveRequestDto;
import com.tyss.restdemo.dto.ResponseDto;
import com.tyss.restdemo.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping
    public ResponseDto createLeave(@RequestBody LeaveRequestDto leaveRequestDto) {
        return ResponseDto.builder().error(false).message("leave created successfully").data(leaveService.createLeave(leaveRequestDto)).build();
    }

    @GetMapping
    public ResponseDto getAll(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {

        return ResponseDto.builder()
                .error(false)
                .message("Fetched all leave details")
                .data(leaveService.getAll(status, page, size, sort, direction))
                .build();
    }




    @PutMapping
    public ResponseDto leaveAction(
            @RequestParam Integer id,
            @RequestParam String action
    ) {

        return ResponseDto.builder()
                .error(false)
                .message("Leave action performed successfully")
                .data(leaveService.leaveAction(id, action))
                .build();
    }

    @DeleteMapping("/{empId}/{leaveId}")
    public ResponseDto deleteLeave(@PathVariable Integer empId,@PathVariable Integer leaveId){
        return ResponseDto.builder().error(false).data(leaveService.deleteLeave(empId,leaveId)).message("deleted the leave requested").build();
    }

    @GetMapping("/{empId}")
    public ResponseDto getById(@PathVariable Integer empId){
        return ResponseDto.builder().error(false).data(leaveService.getById(empId)).message("all leaves by employee").build();
    }

}
