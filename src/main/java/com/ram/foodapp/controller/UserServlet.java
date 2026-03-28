package com.ram.foodapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.foodapp.config.AppConfig;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.dto.request.RegisterUserRequest;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.dto.response.UserResponse;
import com.ram.foodapp.mapper.UserMapper;
import com.ram.foodapp.model.user.User;
import com.ram.foodapp.service.UserService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "UserServlet", urlPatterns = {"/user", "/user/*"})
public class UserServlet extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(UserServlet.class);
    UserService userService;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(ServletConfig config) throws ServletException {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        userService = context.getBean(UserService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            handleGetUsers(req, resp);
            return;
        }
        if (path.matches("/\\d+")) {
            handleGetUserById(extractId(path), resp);
            return;
        }
        if (path.equals("/search")) {
            handleSearch(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid GET endpoint");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            handleCreateUser(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid POST endpoint");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path.matches("/\\d+")) {
            int id = extractId(path);
            userService.deactivateUser(id);
            sendJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.success("User deactivated", null));
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid DELETE endpoint");
    }

    private void handleGetUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);
        PageRequest pageRequest = new PageRequest(page,size);
        List<UserResponse> users = userService.findAll(pageRequest)
                .stream()
                .map(UserMapper::toResponse)
                .toList();
        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Users fetched", users));
    }

    private void handleGetUserById(int id, HttpServletResponse resp) throws IOException {
        User user = userService.findById(id);
        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("User fetched",
                        UserMapper.toResponse(user)));
    }

    private void handleCreateUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RegisterUserRequest request =
                mapper.readValue(req.getInputStream(), RegisterUserRequest.class);
        logger.info("Creating user with email={}", request.email());
        User savedUser = userService.save(UserMapper.toDomain(request));
        sendJson(resp, HttpServletResponse.SC_CREATED,
                ApiResponse.success("User created",
                        UserMapper.toResponse(savedUser)));
    }

    private void handleSearch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        Optional<User> user = userService.findByEmail(email);
        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Search result",
                        user.map(UserMapper::toResponse)));
    }

    private void sendJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getOutputStream(), body);
    }

    private int extractId(String path) {
        return Integer.parseInt(path.split("/")[1]);
    }

    private int parseInt(String value, int defaultVal) {
        try {
            return value == null ? defaultVal : Integer.parseInt(value);
        } catch (Exception e) {
            return defaultVal;
        }
    }
}
