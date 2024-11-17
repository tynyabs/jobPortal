package com.jobapp.user_service.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordReset {

    String email;

    String actor;

    String password;
}
