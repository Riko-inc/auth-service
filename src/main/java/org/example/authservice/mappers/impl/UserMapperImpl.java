package org.example.authservice.mappers.impl;

import lombok.RequiredArgsConstructor;
import org.example.authservice.domain.dto.UserDto;
import org.example.authservice.domain.entities.UserEntity;
import org.example.authservice.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements Mapper<UserEntity, UserDto> {

    private final ModelMapper modelMapper;

    @Override
    public UserDto mapToDto(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserEntity mapFromDto(UserDto userDto) {
        return modelMapper.map(userDto, UserEntity.class);
    }
}
