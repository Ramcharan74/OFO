package com.ram.foodapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.foodapp.config.AppConfig;
import com.ram.foodapp.dto.request.*;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.dto.response.MenuItemResponse;
import com.ram.foodapp.mapper.MenuItemMapper;
import com.ram.foodapp.model.menuitem.MenuItem;
import com.ram.foodapp.service.MenuItemService;
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

@WebServlet(name = "MenuItemServlet", urlPatterns = {"/menuitem", "/menuitem/*"})
public class MenuItemServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(MenuItemServlet.class);
    private MenuItemService menuItemService;
    private static final ObjectMapper mapper = JsonUtil.DEFAULT_MAPPER;

    @Override
    public void init(ServletConfig config) {
        ApplicationContext context = (ApplicationContext) config.getServletContext().getAttribute("springContext");
        menuItemService = context.getBean(MenuItemService.class);
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
        if (path.equals("/restaurant")) {
            handleGetByRestaurant(req, resp);
            return;
        }
        if (path.equals("/category")) {
            handleGetByCategory(req, resp);
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
        CreateMenuItemRequest request =
                mapper.readValue(req.getInputStream(), CreateMenuItemRequest.class);
        logger.info("Creating menu item for restaurantId={}", request.restaurantId());
        MenuItem saved = menuItemService.save(MenuItemMapper.toEntity(request));
        sendJson(resp, HttpServletResponse.SC_CREATED,
                ApiResponse.success("Menu item created",
                        MenuItemMapper.toResponse(saved)));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path.equals("/availability")) {
            UpdateAvailabilityRequest request =
                    mapper.readValue(req.getInputStream(), UpdateAvailabilityRequest.class);
            menuItemService.updateAvailability(request.menuItemId(), request.isAvailable());
            sendJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.success("Availability updated", null));
            return;
        }
        if (path.equals("/price")) {
            UpdatePriceRequest request =
                    mapper.readValue(req.getInputStream(), UpdatePriceRequest.class);
            menuItemService.updatePrice(request.menuItemId(), request.price());
            sendJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.success("Price updated", null));
            return;
        }
        if (path.equals("/quantity")) {
            UpdateQuantityRequest request =
                    mapper.readValue(req.getInputStream(), UpdateQuantityRequest.class);
            menuItemService.updateQuantity(request.menuItemId(), request.quantity());
            sendJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.success("Quantity updated", null));
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid PUT endpoint");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path.matches("/\\d+")) {
            menuItemService.deleteById(extractId(path));
            sendJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.success("Menu item deleted", null));
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid DELETE endpoint");
    }

    private void handleGetAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);
        List<MenuItemResponse> list = menuItemService.findAll(new PageRequest(page, size))
                .stream()
                .map(MenuItemMapper::toResponse)
                .toList();
        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Menu items fetched", list));
    }

    private void handleGetById(int id, HttpServletResponse resp) throws IOException {
        MenuItem item = menuItemService.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Menu item fetched",
                        MenuItemMapper.toResponse(item)));
    }

    private void handleGetByRestaurant(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int restaurantId = parseInt(req.getParameter("restaurantId"), -1);
        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);
        List<MenuItemResponse> list = menuItemService
                .findByRestaurantId(restaurantId, new PageRequest(page, size))
                .stream()
                .map(MenuItemMapper::toResponse)
                .toList();
        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Menu items by restaurant fetched", list));
    }

    private void handleGetByCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int restaurantId = parseInt(req.getParameter("restaurantId"), -1);
        String category = req.getParameter("category");
        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);
        List<MenuItemResponse> list = menuItemService
                .findByCategorynRestaurant(
                        restaurantId,
                        com.ram.foodapp.enums.FoodType.valueOf(category.toUpperCase()),
                        new PageRequest(page, size)
                )
                .stream()
                .map(MenuItemMapper::toResponse)
                .toList();
        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Menu items by category fetched", list));
    }

    private void handleSearch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int restaurantId = parseInt(req.getParameter("restaurantId"), -1);
        String name = req.getParameter("name");
        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);
        List<MenuItemResponse> list = menuItemService
                .searchByName(restaurantId, name, new PageRequest(page, size))
                .stream()
                .map(MenuItemMapper::toResponse)
                .toList();
        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Search results", list));
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