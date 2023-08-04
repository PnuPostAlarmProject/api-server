package com.ppap.ppap._core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppap.ppap._core.exception.Exception401;
import com.ppap.ppap._core.exception.Exception403;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class FilterResponseUtils {

    private final ObjectMapper om;

    public void unAuthorizationRepsonse(HttpServletResponse response, Exception401 e) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(e.status().value());
        response.getWriter().println(om.writeValueAsString(e.body()));
    }

    public void forbiddenResponse(HttpServletResponse response, Exception403 e) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(e.status().value());
        response.getWriter().println(om.writeValueAsString(e.body()));
    }
}
