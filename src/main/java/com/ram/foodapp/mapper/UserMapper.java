package com.ram.foodapp.mapper;

import com.ram.foodapp.dto.request.RegisterUserRequest;
import com.ram.foodapp.dto.response.UserResponse;
import com.ram.foodapp.enums.Role;
import com.ram.foodapp.model.address.PhoneNumber;
import com.ram.foodapp.model.user.ContactInfo;
import com.ram.foodapp.model.user.User;
import com.ram.foodapp.model.user.UserCredentials;

public class UserMapper {
    public static User toDomain(RegisterUserRequest registerUserRequest){
        return new User.Builder()
                .name(registerUserRequest.name())
                .contactInfo(new ContactInfo(registerUserRequest.email(),new PhoneNumber(registerUserRequest.phoneNo())))
                .credentials(new UserCredentials(registerUserRequest.password()))
                .role(Role.valueOf(registerUserRequest.role()))
                .build();
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getContactInfo().getEmail(),
                user.getContactInfo().getPhoneNo().getValue(),
                user.getRole().name(),
                user.getIsActive(),
                user.getAuditInfo().getCreatedAt()
        );
    }
}
