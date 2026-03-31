package com.ram.foodapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.foodapp.config.AppConfig;
import com.ram.foodapp.dto.request.BulkOrderItemRequest;
import com.ram.foodapp.dto.request.CreateOrderItemRequest;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.dto.response.OrderItemResponse;
import com.ram.foodapp.mapper.OrderItemMapper;
import com.ram.foodapp.model.orderitem.OrderItem;
import com.ram.foodapp.service.OrderItemService;
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

@WebServlet(name = "OrderItemServlet", urlPatterns = {"/order-items", "/order-items/*"})
public class OrderItemServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemServlet.class);

    private OrderItemService orderItemService;
    private static final ObjectMapper mapper = JsonUtil.DEFAULT_MAPPER;

    @Override
    public void init(ServletConfig config) {
        ApplicationContext context = (ApplicationContext) config.getServletContext().getAttribute("springContext");
        orderItemService = context.getBean(OrderItemService.class);
    }

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
        if (path.equals("/order")) {
            handleGetByOrderId(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid GET endpoint");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path != null && path.equals("/bulk")) {
            BulkOrderItemRequest request =
                    mapper.readValue(req.getInputStream(), BulkOrderItemRequest.class);
            List<OrderItem> items = request.items()
                    .stream()
                    .map(OrderItemMapper::toEntity)
                    .toList();
            List<OrderItem> saved = orderItemService.saveAll(items);
            sendJson(resp, HttpServletResponse.SC_CREATED,
                    ApiResponse.success("Order items created",
                            saved.stream().map(OrderItemMapper::toResponse).toList()));
            return;
        }
        CreateOrderItemRequest request =
                mapper.readValue(req.getInputStream(), CreateOrderItemRequest.class);
        OrderItem saved = orderItemService.save(
                OrderItemMapper.toEntity(request));
        sendJson(resp, HttpServletResponse.SC_CREATED,
                ApiResponse.success("Order item created",
                        OrderItemMapper.toResponse(saved)));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path.equals("/order")) {
            int orderId = parseInt(req.getParameter("orderId"), -1);
            orderItemService.deleteByOrderId(orderId);
            sendJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.success("Order items deleted", null));
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid DELETE endpoint");
    }


    private void handleGetAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);
        List<OrderItemResponse> list = orderItemService
                .findAll(new PageRequest(page, size))
                .stream()
                .map(OrderItemMapper::toResponse)
                .toList();
        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Order items fetched", list));
    }

    private void handleGetById(int id, HttpServletResponse resp) throws IOException {
        OrderItem item = orderItemService.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found"));
        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Order item fetched",
                        OrderItemMapper.toResponse(item)));
    }

    private void handleGetByOrderId(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int orderId = parseInt(req.getParameter("orderId"), -1);
        List<OrderItemResponse> list = orderItemService
                .findByOrderId(orderId)
                .stream()
                .map(OrderItemMapper::toResponse)
                .toList();
        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Order items fetched", list));
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