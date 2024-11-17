package com.jobapp.user_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserPasswordUpdate {

    private String oldPassword;

    private String newPassword;
    private String confirmPassword;
}

