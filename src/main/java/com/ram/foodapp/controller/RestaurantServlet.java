package com.ram.foodapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.foodapp.config.AppConfig;
import com.ram.foodapp.dto.request.*;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.dto.response.RestaurantResponse;
import com.ram.foodapp.enums.ErrorCode;
import com.ram.foodapp.mapper.RestaurantMapper;
import com.ram.foodapp.model.restaurant.Restaurant;
import com.ram.foodapp.service.RestaurantService;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "RestaurantServlet", urlPatterns = {"/restaurant", "/restaurant/*"})
public class RestaurantServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantServlet.class);

    private RestaurantService restaurantService;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(ServletConfig config) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        restaurantService = context.getBean(RestaurantService.class);
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
            handleGetByUserId(req, resp);
            return;
        }

        if (path.equals("/address")) {
            handleGetByAddressId(req, resp);
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

        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            handleCreate(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid POST endpoint");
    }

    // ================= PUT =================
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String path = req.getPathInfo();

        if (path.equals("/status")) {
            handleUpdateStatus(req, resp);
            return;
        }

        if (path.equals("/rating")) {
            handleUpdateRating(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid PUT endpoint");
    }

    // ================= DELETE =================
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String path = req.getPathInfo();

        if (path.matches("/\\d+")) {
            restaurantService.deleteById(extractId(path));

            sendJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.success("Restaurant deleted", null));
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid DELETE endpoint");
    }

    // ================= HANDLERS =================

    private void handleGetAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);

        List<RestaurantResponse> restaurants = restaurantService
                .findAll(new PageRequest(page, size))
                .stream()
                .map(RestaurantMapper::toResponse)
                .toList();

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Restaurants fetched", restaurants));
    }

    private void handleGetById(int id, HttpServletResponse resp) throws IOException {
        Restaurant restaurant = restaurantService.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Restaurant fetched",
                        RestaurantMapper.toResponse(restaurant)));
    }

    private void handleGetByUserId(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = parseInt(req.getParameter("userId"), -1);

        List<RestaurantResponse> list = restaurantService.findByUserId(userId)
                .stream()
                .map(RestaurantMapper::toResponse)
                .toList();

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("User restaurants fetched", list));
    }

    private void handleGetByAddressId(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int addressId = parseInt(req.getParameter("addressId"), -1);

        List<RestaurantResponse> list = restaurantService.findByAddressId(addressId)
                .stream()
                .map(RestaurantMapper::toResponse)
                .toList();

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Restaurants by address fetched", list));
    }

    private void handleGetByStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String status = req.getParameter("status");

        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);

        List<RestaurantResponse> list = restaurantService
                .findByStatus(status, new PageRequest(page, size))
                .stream()
                .map(RestaurantMapper::toResponse)
                .toList();

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Restaurants by status fetched", list));
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CreateRestaurantRequest request =
                mapper.readValue(req.getInputStream(), CreateRestaurantRequest.class);

        logger.info("Creating restaurant for userId={}", request.userId());

        Restaurant saved = restaurantService.save(
                RestaurantMapper.toEntity(request));

        sendJson(resp, HttpServletResponse.SC_CREATED,
                ApiResponse.success("Restaurant created",
                        RestaurantMapper.toResponse(saved)));
    }

    private void handleUpdateStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UpdateRestaurantStatusRequest request =
                mapper.readValue(req.getInputStream(), UpdateRestaurantStatusRequest.class);

        restaurantService.updateStatus(request.restaurantId(), request.status());

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Status updated", null));
    }

    private void handleUpdateRating(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UpdateRestaurantRatingRequest request =
                mapper.readValue(req.getInputStream(), UpdateRestaurantRatingRequest.class);

        restaurantService.updateRating(
                request.restaurantId(),
                request.rating(),
                request.ratingCount()
        );

        sendJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.success("Rating updated", null));
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