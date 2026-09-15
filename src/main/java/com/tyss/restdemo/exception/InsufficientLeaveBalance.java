package com.tyss.restdemo.exception;

public class InsufficientLeaveBalance extends RuntimeException {
    public InsufficientLeaveBalance(String message) {
        super(message);
    }
}
