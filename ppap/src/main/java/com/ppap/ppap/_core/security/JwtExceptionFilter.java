package com.ppap.ppap._core.security;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppap.ppap._core.exception.Exception401;
import com.ppap.ppap._core.exception.Exception403;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper om = new ObjectMapper();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        }catch (JWTCreationException | JWTVerificationException e){
            setJwtExceptionResponse(request, response, e);
        }catch (Exception403 e) {
            setForbiddenExceptionResponse(request, response, e);
        }
    }

    private void setJwtExceptionResponse(HttpServletRequest request, HttpServletResponse response, Throwable exception) throws IOException {
        Exception401 e = new Exception401(exception.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(e.status().value());
        response.getOutputStream().write(om.writeValueAsBytes(e.body()));
    }

    private void setForbiddenExceptionResponse(HttpServletRequest request, HttpServletResponse response, Exception403 e) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(e.status().value());
        response.getOutputStream().write(om.writeValueAsBytes(e.body()));
    }
}
