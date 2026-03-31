package com.ram.foodapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.foodapp.config.AppConfig;
import com.ram.foodapp.dto.request.CreateAddressRequest;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.dto.request.UpdateAddressRequest;
import com.ram.foodapp.dto.response.AddressResponse;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.mapper.AddressMapper;
import com.ram.foodapp.model.address.Address;
import com.ram.foodapp.service.AddressService;
import com.ram.foodapp.util.JsonUtil;
import jakarta.servlet.ServletConfig;
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

@WebServlet(name = "AddressServlet", urlPatterns = {"/address", "/address/*"})
public class AddressServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AddressServlet.class);

    private AddressService addressService;
    private static final ObjectMapper mapper = JsonUtil.DEFAULT_MAPPER;

    @Override
    public void init(ServletConfig config) {
        ApplicationContext context = (ApplicationContext) config.getServletContext().getAttribute("springContext");
        addressService = context.getBean(AddressService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.info("getting user get request :", req);
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            handleGetAll(req, resp);
            return;
        }
        if (path.matches("/\\d+")) {
            handleGetById(extractId(path), resp);
            return;
        }
        if (path.equals("/user")) {
            handleGetByUserId(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid GET endpoint");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            handleCreate(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid POST endpoint");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleUpdate(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path.matches("/\\d+")) {
            handleDeleteById(extractId(path), resp);
            return;
        }
        if (path.equals("/user")) {
            handleDeleteByUserId(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid DELETE endpoint");
    }

    private void handleGetAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);

        PageRequest pageRequest = new PageRequest(page, size);

        List<AddressResponse> addresses = addressService.findAll(pageRequest)
                .stream()
                .map(AddressMapper::toResponse)
                .toList();

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Addresses fetched", addresses));
    }

    private void handleGetById(int id, HttpServletResponse resp) throws IOException {
        Address address = addressService.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Address fetched",
                        AddressMapper.toResponse(address)));
    }

    private void handleGetByUserId(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = parseInt(req.getParameter("userId"), -1);

        List<AddressResponse> addresses = addressService.findByUserId(userId)
                .stream()
                .map(AddressMapper::toResponse)
                .toList();

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("User addresses fetched", addresses));
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CreateAddressRequest request =
                mapper.readValue(req.getInputStream(), CreateAddressRequest.class);

        logger.info("Creating address for userId={}", request.userId());

        Address saved = addressService.save(AddressMapper.toEntity(request));

        sendJson(resp, HttpServletResponse.SC_CREATED,
                ApiResponse.success("Address created",
                        AddressMapper.toResponse(saved)));
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UpdateAddressRequest request =
                mapper.readValue(req.getInputStream(), UpdateAddressRequest.class);

        addressService.updateAddress(AddressMapper.toEntity(request));

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Address updated", null));
    }

    private void handleDeleteById(int id, HttpServletResponse resp) throws IOException {
        addressService.deleteById(id);

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Address deleted", null));
    }

    private void handleDeleteByUserId(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = parseInt(req.getParameter("userId"), -1);

        addressService.deleteByUserId(userId);

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("User addresses deleted", null));
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