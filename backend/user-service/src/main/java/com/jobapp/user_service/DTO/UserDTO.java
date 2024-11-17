package com.jobapp.user_service.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDTO(
        Long id,
        String name,
        String email,
        String username, Object o)
{

}