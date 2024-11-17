package com.jobapp.user_service.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Getter
@Setter
@Service
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginResponse{
    private String username;
    private String Role;
    private String token;
    private String expiry;
    private String issuedAt;
    private String message;
}
