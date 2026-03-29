package com.ram.foodapp.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.enums.ErrorCode;
import com.ram.foodapp.exception.DataAccessException;
import com.ram.foodapp.exception.UserAlreadyExistsException;
import com.ram.foodapp.util.JsonUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebFilter("/*")
public class GlobalExceptionFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionFilter.class);
    private final ObjectMapper mapper = JsonUtil.DEFAULT_MAPPER;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        try {
            chain.doFilter(request, response);
        } catch (UserAlreadyExistsException e) {
            handle(resp, HttpServletResponse.SC_CONFLICT,
                    e.getMessage(), ErrorCode.USER_ALREADY_EXISTS, e);

        } catch (DataAccessException e) {
            handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), ErrorCode.valueOf("INVALID_RESPONSE"), e);
        } catch (IllegalArgumentException e) {
            handle(resp, HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage(), ErrorCode.VALIDATION_FAILED, e);

        } catch (Exception e) {
            handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Something went wrong", ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    private void handle(HttpServletResponse resp,
                        int status,
                        String message,
                        ErrorCode code,
                        Exception e) throws IOException {
        if (resp.isCommitted()) return;
        logger.error("Exception caught: {}", message, e);
        resp.reset();
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getOutputStream(),
                ApiResponse.error(message, code));
    }
}