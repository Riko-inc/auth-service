package org.example.authservice.services;

import org.example.authservice.domain.dto.requests.UserUpdateRequest;
import org.example.authservice.domain.dto.responses.UserGetCurrentUserResponse;
import org.example.authservice.domain.dto.responses.UserUpdateResponse;
import org.example.authservice.domain.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    /**
     * Получение списка всех пользователей. Только для администратора. Соответвующий эндпоинт скрыт в документации
     *
     * @return список {@link UserEntity}, сохранённый в базе данных
     */
    List<UserEntity> getAll();


    /**
     * Получение пользователя по его id
     *
     * @param id id пользователя
     * @return {@link UserEntity}, сохранённый в базе данных
     * @see UserEntity
     */
    UserEntity getById(Long id);


    /**
     * Получение текущего пользователя. Извлекается из Principals
     *
     * @param user текущий пользователь
     * @return {@link UserGetCurrentUserResponse}
     * @see UserEntity
     */
    UserGetCurrentUserResponse getCurrentUser(UserEntity user);

    /**
     * Обновление существующего пользователя. Может быть выполнено только самим пользователем по отношению к своему
     * аккаунту
     *
     * @param userId            id пользователя
     * @param userUpdateRequest запрос с полями на обновление
     * @return {@link UserUpdateResponse}
     */
    UserUpdateResponse update(long userId, UserUpdateRequest userUpdateRequest);

    /**
     * Обновление существующего пользователя. Может быть выполнено только самим пользователем по отношению к своему
     * аккаунту
     *
     * @param email почта пользователя
     */
    void deleteByEmail(String email);

    /**
     * Создание нового пользователя и добавление его в базу данных
     *
     * @param userEntity сущность пользователя
     * @return {@link UserEntity}
     */
    UserEntity create(UserEntity userEntity);


    /**
     * Получение пользователя по его email
     *
     * @param email почта пользователя
     * @return {@link UserEntity}
     */
    UserEntity getByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsById(Long userId);
}
