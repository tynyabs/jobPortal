package com.jobapp.user_service.DTO;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserMapper implements Function<com.jobapp.user_service.Model.User, UserDTO> {

    @Override
    public UserDTO apply(com.jobapp.user_service.Model.User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUsername(), null);
    }
}
