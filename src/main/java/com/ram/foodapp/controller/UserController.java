package com.ram.foodapp.controller;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.dto.request.RegisterUserRequest;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.dto.response.UserResponse;
import com.ram.foodapp.exception.ResourceNotFoundException;
import com.ram.foodapp.mapper.UserMapper;
import com.ram.foodapp.model.user.User;
import com.ram.foodapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        PageRequest pageRequest = new PageRequest(page,size);
        List<UserResponse> userResponseList = userService.findAll(pageRequest).stream().map(UserMapper::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully",userResponseList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getByUserId(@PathVariable(value = "id") int id){
        UserResponse userResponse = UserMapper.toResponse(userService.findById(id));
        return ResponseEntity.ok(ApiResponse.success("User fetched successfully",userResponse));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserResponse>> getByEmail(@RequestParam(value = "email") String email){
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        UserResponse response = UserMapper.toResponse(user);
        return ResponseEntity.ok(
                ApiResponse.success("User fetched by email", response)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Validated @RequestBody RegisterUserRequest request){
        User savedUser = userService.save(UserMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User Created successfully",UserMapper.toResponse(savedUser)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> deleteById(@PathVariable(value = "id") int id){
        User user = userService.findById(id);
         userService.deactivateUser(id);
         return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("User deleted successfully",UserMapper.toResponse(user)));
    }
}
