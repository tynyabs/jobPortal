package com.jobapp.user_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class UserLoginRequests {
    private String username;
    private String password;
}
