package com.jobapp.user_service.DTO;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UserCreate {

    private String name;
    private String surname;

    private String email;

    private String msidn;

    private String idn;

    private String password;

    private String username;

    public String getUsername() {
        return email.toLowerCase();
    }

    public void setUsername(String username) {
        this.username = email;
    }
    }