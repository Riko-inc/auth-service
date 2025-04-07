package org.example.authservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.authservice.domain.dto.requests.UserUpdateRequest;
import org.example.authservice.domain.dto.responses.GetUserResponse;
import org.example.authservice.domain.dto.responses.UserGetCurrentUserResponse;
import org.example.authservice.domain.dto.responses.UserUpdateResponse;
import org.example.authservice.domain.entities.UserEntity;
import org.example.authservice.exceptions.AccessDeniedException;
import org.example.authservice.exceptions.EntityNotFoundException;
import org.example.authservice.exceptions.InvalidRequestParameterException;
import org.example.authservice.mappers.Mapper;
import org.example.authservice.repositories.UserRepository;
import org.example.authservice.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.authservice.exceptions.UserAlreadyExistException;
import java.util.List;
import java.util.Optional;

/**
 * Contains logic for operations with User Entity
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Mapper<UserEntity, GetUserResponse> getUserResponseMapper;

    @Override
    @Transactional
    public UserEntity create(UserEntity userEntity) {
        if (userRepository.existsByEmail(userEntity.getEmail())) {
            throw new UserAlreadyExistException("User with " + userEntity.getEmail() + " email already exists");
        }
        return userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public List<GetUserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(getUserResponseMapper::mapToDto).toList();
    }

    @Override
    @Transactional
    public UserEntity getById(Long id) {
        return userRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Пользователь " + id + " не найден"));
    }

    @Override
    @Transactional
    public UserEntity getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь c email" + email + " не найден"));
    }

    @Override
    @Transactional
    public UserUpdateResponse update(long userId, UserUpdateRequest userUpdateRequest) {
        return userRepository.findById(userId).map(existingUserEntity -> {
            Optional.ofNullable(userUpdateRequest.getEmail()).ifPresent(existingUserEntity::setEmail);
            Optional.ofNullable(userUpdateRequest.getPassword()).ifPresent(pwd -> existingUserEntity.setPassword(passwordEncoder.encode(pwd)));
            userRepository.save(existingUserEntity);
            return UserUpdateResponse.builder()
                    .id(existingUserEntity.getUserId())
                    .role(existingUserEntity.getRole())
                    .email(existingUserEntity.getEmail())
                    .registrationDateTime(existingUserEntity.getRegistrationDateTime())
                    .build();
        }).orElseThrow(() -> new EntityNotFoundException("Пользователь " + userId + " не найден"));
    }

    @Override
    @Transactional
    public void deleteByEmail(String email){
        if (email == null || email.isEmpty()) {
            throw new InvalidRequestParameterException("Email was not provided");
        }
        userRepository.deleteUserEntityByEmail(email);
    }

    @Override
    @Transactional
    public UserGetCurrentUserResponse getCurrentUser(UserEntity user) {
        if (user == null) {
            throw new AccessDeniedException("User was not logged in");
        }
        return UserGetCurrentUserResponse.builder()
                .id(user.getUserId())
                .role(user.getRole())
                .email(user.getEmail())
                .registrationDateTime(user.getRegistrationDateTime()).build();
    }

    @Override
    @Transactional
    public boolean existsByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new InvalidRequestParameterException("Email was not provided");
        }
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public boolean existsById(Long userId) {
        if (userId == null) {
            throw new InvalidRequestParameterException("User id was not provided");
        }
        return userRepository.existsById(userId);
    }
}
