package com.ram.foodapp.dto.request;

import java.util.List;

public record BulkOrderItemRequest(
        List<CreateOrderItemRequest> items
) {}