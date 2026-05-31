package com.bazar.apibazar.dto.user;

import com.bazar.apibazar.dto.role.RoleResponseDto;

import java.util.List;

public record UserResponseDto(Long id,
                              String username,
                              List<RoleResponseDto> roles,
                              Long idClient, boolean enable) {}
