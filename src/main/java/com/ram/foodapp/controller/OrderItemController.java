package com.ram.foodapp.controller;

import com.ram.foodapp.dto.request.BulkOrderItemRequest;
import com.ram.foodapp.dto.request.CreateOrderItemRequest;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.dto.response.OrderItemResponse;
import com.ram.foodapp.exception.ResourceNotFoundException;
import com.ram.foodapp.mapper.OrderItemMapper;
import com.ram.foodapp.model.orderitem.OrderItem;
import com.ram.foodapp.service.OrderItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemController.class);

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderItemResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<OrderItemResponse> list = orderItemService.findAll(new PageRequest(page, size))
                .stream()
                .map(OrderItemMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Order items fetched", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderItemResponse>> getById(@PathVariable int id) {
        OrderItem item = orderItemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id: " + id));
        return ResponseEntity.ok(ApiResponse.success("Order item fetched", OrderItemMapper.toResponse(item)));
    }

    @GetMapping("/order")
    public ResponseEntity<ApiResponse<List<OrderItemResponse>>> getByOrderId(@RequestParam int orderId) {
        List<OrderItemResponse> list = orderItemService.findByOrderId(orderId)
                .stream()
                .map(OrderItemMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Order items fetched", list));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<OrderItemResponse>>> createBulk(@Validated @RequestBody BulkOrderItemRequest request) {
        List<OrderItem> items = request.items().stream().map(OrderItemMapper::toEntity).toList();
        List<OrderItem> saved = orderItemService.saveAll(items);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order items created", saved.stream().map(OrderItemMapper::toResponse).toList()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderItemResponse>> create(@Validated @RequestBody CreateOrderItemRequest request) {
        OrderItem saved = orderItemService.save(OrderItemMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order item created", OrderItemMapper.toResponse(saved)));
    }

    @DeleteMapping("/order")
    public ResponseEntity<ApiResponse<Void>> deleteByOrderId(@RequestParam int orderId) {
        orderItemService.deleteByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success("Order items deleted", null));
    }
}
