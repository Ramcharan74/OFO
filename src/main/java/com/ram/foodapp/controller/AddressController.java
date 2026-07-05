package com.ram.foodapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.foodapp.dto.request.CreateAddressRequest;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.dto.request.UpdateAddressRequest;
import com.ram.foodapp.dto.response.AddressResponse;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.exception.DataAccessException;
import com.ram.foodapp.mapper.AddressMapper;
import com.ram.foodapp.model.address.Address;
import com.ram.foodapp.service.AddressService;
import com.ram.foodapp.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/address")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    private final AddressService addressService;
    private static final ObjectMapper mapper = JsonUtil.DEFAULT_MAPPER;

    public AddressController(AddressService addressService){
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAll(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size){
        PageRequest pageRequest = new PageRequest(page,size);
        List<AddressResponse> addressResponses = addressService.findAll(pageRequest)
                .stream()
                .map(AddressMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Address Fetched",addressResponses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(@PathVariable(value = "id") int id){
        Address address = addressService.findById(id).orElseThrow(() -> new DataAccessException("Address not found"));
        return ResponseEntity.ok(ApiResponse.success("Address fetched",AddressMapper.toResponse(address)));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddressByUserId(@RequestParam int userId){
        List<AddressResponse> addressResponses = addressService.findByUserId(userId).stream()
                .map(AddressMapper::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.success("User address fetched",addressResponses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> create(@Validated  @RequestBody CreateAddressRequest request){
        logger.info("Creating address for userId={}", request.userId());
        Address address = AddressMapper.toEntity(request);
        AddressResponse addressResponse = AddressMapper.toResponse(addressService.save(address));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Created Address Successfully",addressResponse));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<AddressResponse>> create(@Validated  @RequestBody UpdateAddressRequest request){
        addressService.updateAddress(AddressMapper.toEntity(request));
        return ResponseEntity.ok(
                ApiResponse.success("Address updated", null)
        );
    }

    @DeleteMapping("/user")
    public ResponseEntity<ApiResponse<Void>> deleteByUserId(
            @RequestParam int userId) {

        addressService.deleteByUserId(userId);

        return ResponseEntity.ok(
                ApiResponse.success("User addresses deleted", null)
        );
    }
}