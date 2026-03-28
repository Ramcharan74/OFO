package com.ram.foodapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.foodapp.config.AppConfig;
import com.ram.foodapp.dto.request.*;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.dto.response.FoodOrderResponse;
import com.ram.foodapp.enums.OrderStatus;
import com.ram.foodapp.mapper.FoodOrderMapper;
import com.ram.foodapp.model.foodorder.FoodOrder;
import com.ram.foodapp.service.FoodOrderService;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "FoodOrderServlet", urlPatterns = {"/orders", "/orders/*"})
public class FoodOrderServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(FoodOrderServlet.class);

    private FoodOrderService foodOrderService;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(ServletConfig config) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        foodOrderService = context.getBean(FoodOrderService.class);
    }

    // ================= GET =================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

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
            handleGetByUser(req, resp);
            return;
        }

        if (path.equals("/status")) {
            handleGetByStatus(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid GET endpoint");
    }

    // ================= POST =================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        CreateOrderRequest request =
                mapper.readValue(req.getInputStream(), CreateOrderRequest.class);

        logger.info("Creating order for userId={}", request.userId());

        FoodOrder saved = foodOrderService.save(
                FoodOrderMapper.toEntity(request)
        );

        sendJson(resp, HttpServletResponse.SC_CREATED,
                ApiResponse.success("Order created",
                        FoodOrderMapper.toResponse(saved)));
    }

    // ================= PUT =================
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String path = req.getPathInfo();

        if (path.equals("/status")) {
            UpdateOrderStatusRequest request =
                    mapper.readValue(req.getInputStream(), UpdateOrderStatusRequest.class);

            foodOrderService.updateStatus(
                    request.orderId(),
                    OrderStatus.valueOf(request.status().toUpperCase())
            );

            sendJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.success("Order status updated", null));
            return;
        }

        if (path.equals("/payment-status")) {
            UpdatePaymentStatusRequest request =
                    mapper.readValue(req.getInputStream(), UpdatePaymentStatusRequest.class);

            foodOrderService.updatePaymentStatus(
                    request.orderId(),
                    request.isPaid()
            );

            sendJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.success("Payment status updated", null));
            return;
        }

        if (path.equals("/payment-mode")) {
            UpdatePaymentModeRequest request =
                    mapper.readValue(req.getInputStream(), UpdatePaymentModeRequest.class);

            foodOrderService.updatePaymentMode(
                    request.orderId(),
                    request.paymentMode()
            );

            sendJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.success("Payment mode updated", null));
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid PUT endpoint");
    }

    // ================= HANDLERS =================

    private void handleGetAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);

        List<FoodOrderResponse> list = foodOrderService
                .findAll(new PageRequest(page, size))
                .stream()
                .map(FoodOrderMapper::toResponse)
                .toList();

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Orders fetched", list));
    }

    private void handleGetById(int id, HttpServletResponse resp) throws IOException {
        FoodOrder order = foodOrderService.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Order fetched",
                        FoodOrderMapper.toResponse(order)));
    }

    private void handleGetByUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = parseInt(req.getParameter("userId"), -1);

        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);

        List<FoodOrderResponse> list = foodOrderService
                .findByUserId(userId, new PageRequest(page, size))
                .stream()
                .map(FoodOrderMapper::toResponse)
                .toList();

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("User orders fetched", list));
    }

    private void handleGetByStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String status = req.getParameter("status");

        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);

        List<FoodOrderResponse> list = foodOrderService
                .findByStatus(status, new PageRequest(page, size))
                .stream()
                .map(FoodOrderMapper::toResponse)
                .toList();

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Orders by status fetched", list));
    }

    // ================= COMMON =================

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