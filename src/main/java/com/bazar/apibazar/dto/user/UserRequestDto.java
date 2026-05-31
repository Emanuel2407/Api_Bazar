package com.bazar.apibazar.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Set;

public record UserRequestDto(@NotBlank String username,
                             @NotBlank String password,
                             @NotEmpty List<@NotBlank String> rolesNames) {}
