package org.example.authservice.mappers.impl;

import lombok.RequiredArgsConstructor;
import org.example.authservice.domain.dto.UserDto;
import org.example.authservice.domain.dto.responses.GetUserResponse;
import org.example.authservice.domain.entities.UserEntity;
import org.example.authservice.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetUserResponseMapperImpl implements Mapper<UserEntity, GetUserResponse> {

    private final ModelMapper modelMapper;

    @Override
    public GetUserResponse mapToDto(UserEntity userEntity) {
        return modelMapper.map(userEntity, GetUserResponse.class);
    }

    @Override
    public UserEntity mapFromDto(GetUserResponse getUserResponse) {
        return modelMapper.map(getUserResponse, UserEntity.class);
    }
}
