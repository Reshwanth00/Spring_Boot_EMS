package com.tyss.restdemo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestResponseFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // BEFORE REQUEST

        System.out.println("========== BEFORE REQUEST ==========");
        System.out.println("HTTP Method : " + request.getMethod());
        System.out.println("Request URI : " + request.getRequestURI());

        // Continue request
        filterChain.doFilter(request, response);

        // AFTER RESPONSE

        System.out.println("========== AFTER RESPONSE ==========");
        System.out.println("Status Code : " + response.getStatus());
    }
}