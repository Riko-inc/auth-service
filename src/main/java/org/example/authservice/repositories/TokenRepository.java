package org.example.authservice.repositories;

import org.example.authservice.domain.entities.TokenRedisEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TokenRepository extends CrudRepository<TokenRedisEntity, String> {

    Optional<TokenRedisEntity> getByToken(String token);
    void deleteAllByUserId(String userId);
    void deleteByToken(String token);
}