package com.ram.foodapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.foodapp.config.AppConfig;
import com.ram.foodapp.dto.request.*;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.dto.response.CartItemResponse;
import com.ram.foodapp.mapper.CartItemMapper;
import com.ram.foodapp.model.cartitem.CartItem;
import com.ram.foodapp.service.CartItemService;

import com.ram.foodapp.util.JsonUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CartItemServlet", urlPatterns = {"/cart", "/cart/*"})
public class CartItemServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CartItemServlet.class);

    private CartItemService cartItemService;
    private static final ObjectMapper mapper = JsonUtil.DEFAULT_MAPPER;

    @Override
    public void init(ServletConfig config) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        cartItemService = context.getBean(CartItemService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            handleGetByUser(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid GET endpoint");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AddToCartRequest request =
                mapper.readValue(req.getInputStream(), AddToCartRequest.class);
        logger.info("Adding to cart userId={}, menuItemId={}",
                request.userId(), request.menuItemId());
        if (cartItemService.existsByUserIdAndMenuItemId(
                request.userId(), request.menuItemId())) {
            cartItemService.incrementQuantity(
                    request.userId(),
                    request.menuItemId(),
                    request.quantity()
            );
            sendJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.success("Cart quantity updated", null));
            return;
        }
        CartItem saved = cartItemService.save(
                CartItemMapper.toEntity(request));
        sendJson(resp, HttpServletResponse.SC_CREATED,
                ApiResponse.success("Item added to cart",
                        CartItemMapper.toResponse(saved)));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path.equals("/quantity")) {
            UpdateCartQuantityRequest request =
                    mapper.readValue(req.getInputStream(), UpdateCartQuantityRequest.class);
            cartItemService.updateQuantity(
                    request.userId(),
                    request.menuItemId(),
                    request.quantity()
            );
            sendJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.success("Quantity updated", null));
            return;
        }
        if (path.equals("/increment")) {
            IncrementCartRequest request =
                    mapper.readValue(req.getInputStream(), IncrementCartRequest.class);
            cartItemService.incrementQuantity(
                    request.userId(),
                    request.menuItemId(),
                    request.delta()
            );
            sendJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.success("Quantity incremented", null));
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid PUT endpoint");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path.equals("/clear")) {
            int userId = parseInt(req.getParameter("userId"), -1);
            cartItemService.clearCart(userId);
            sendJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.success("Cart cleared", null));
            return;
        }
        if (path.matches("/\\d+/\\d+")) {
            String[] parts = path.split("/");
            int userId = Integer.parseInt(parts[1]);
            int menuItemId = Integer.parseInt(parts[2]);
            cartItemService.deleteByUserIdAndMenuItemId(userId, menuItemId);
            sendJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.success("Item removed", null));
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid DELETE endpoint");
    }

    private void handleGetByUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = parseInt(req.getParameter("userId"), -1);
        List<CartItemResponse> list = cartItemService.findByUserId(userId)
                .stream()
                .map(CartItemMapper::toResponse)
                .toList();
        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Cart fetched", list));
    }

    private void sendJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getOutputStream(), body);
    }

    private int parseInt(String value, int defaultVal) {
        try {
            return value == null ? defaultVal : Integer.parseInt(value);
        } catch (Exception e) {
            return defaultVal;
        }
    }
}