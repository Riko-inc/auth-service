# Auth Service

Это модуль аутентификации для платформы управления задачами. Проект реализован на Spring Boot и включает основные функции аутентификации, авторизации и работы с JWT-токенами.

---

## Структура проекта

src/  
├── main/  
│   ├── java/org/example/authservice/  
│   │   ├── config/                   # Конфигурационные классы Spring  
│   │   │   ├── ApplicationConfig.java  
│   │   │   ├── MapperConfig.java  
│   │   │   ├── OpenApiConfig.java  
│   │   │   └── WebSecurityConfig.java  
│   │   ├── controllers/              # REST-контроллеры  
│   │   │   └── AuthenticationController.java  
│   │   ├── domain/                   # Сущности и DTO  
│   │   │   ├── dto/  
│   │   │   │   ├── requests/         # DTO для входящих запросов  
│   │   │   │   │   ├── UserAccessTokenRequest.java  
│   │   │   │   │   ├── UserRefreshTokenRequest.java  
│   │   │   │   │   ├── UserSignInRequest.java  
│   │   │   │   │   ├── UserSignUpRequest.java  
│   │   │   │   │   └── UserUpdateRequest.java  
│   │   │   │   ├── responses/        # DTO для исходящих ответов  
│   │   │   │   │   ├── ErrorResponse.java  
│   │   │   │   │   ├── UserGetCurrentUserResponse.java  
│   │   │   │   │   ├── UserJwtAuthenticationResponse.java  
│   │   │   │   │   └── UserUpdateResponse.java  
│   │   │   │   └── UserDto.java      # Универсальный DTO  
│   │   │   └── entities/             # JPA и Redis сущности  
│   │   │       ├── TokenRedisEntity.java  
│   │   │       └── UserEntity.java  
│   │   ├── exceptions/               # Кастомные исключения  
│   │   │   ├── AccessDeniedException.java  
│   │   │   ├── EntityNotFoundException.java  
│   │   │   ├── ExceptionResponseHandler.java  
│   │   │   └── TaskManagementServiceException.java  
│   │   ├── mappers/                  # Мапперы для преобразования объектов  
│   │   │   ├── Mapper.java           # Интерфейс маппера  
│   │   │   └── impl/                 # Реализации мапперов  
│   │   │       └── UserMapperImpl.java  
│   │   ├── repositories/             # Интерфейсы Spring Data  
│   │   │   ├── TokenRepository.java  # (Redis)  
│   │   │   └── UserRepository.java   # (JPA)  
│   │   ├── security/                 # JWT, фильтры и безопасность  
│   │   │   ├── AspectTaskController.java  # (закомментирован)  
│   │   │   ├── JwtAuthenticationFilter.java  
│   │   │   ├── JwtService.java  
│   │   │   └── LogoutService.java  
│   │   ├── services/                 # Сервисный слой  
│   │   │   ├── AuthenticationService.java  
│   │   │   ├── UserService.java      # Интерфейс  
│   │   │   └── impl/                 # Реализации сервисов  
│   │   │       └── UserServiceImpl.java  
│   │   └── AuthServiceApplication.java  # Точка входа  
│   └── resources/  
│       └── application.properties     # Конфигурация приложения  
└── test/                             # Тесты  

![alt text](image-1.png)

---

# Эндпоинты API (AuthenticationController)

| Метод | Путь             | Описание                                      | Запрос                    | Ответ                          |
|-------|------------------|-----------------------------------------------|---------------------------|--------------------------------|
| POST  | `/register`      | Регистрация нового пользователя              | `UserSignUpRequest`       | `UserJwtAuthenticationResponse` |
| POST  | `/authenticate`  | Аутентификация пользователя                   | `UserSignInRequest`       | `UserJwtAuthenticationResponse` |
| POST  | `/refresh-token` | Обновление пары токенов                      | `UserRefreshTokenRequest` | `UserJwtAuthenticationResponse` |
| GET   | `/check-token`   | Проверка валидности access токена            | `Header: Authorization`   | `Boolean`                      |
| GET   | `/check-email`   | Проверка существования email                 | `Header: Email`           | `Boolean`                      |
| GET   | `/extract-email` | Извлечение email из токена                    | `Header: Authorization`   | `String`                       |
| POST  | `/role`          | Получение роли пользователя из access токена | `UserAccessTokenRequest`  | `String`                       |

---

## 1. Файл `application.properties`

**Основные настройки Spring Boot и зависимостей:**

- **Настройки приложения:**
  - Имя приложения: `spring.application.name=auth-service`
  - Порт сервера: `server.port=8083`
  
- **Логирование:**
  - Отключение вывода стектрейсов в ошибках: `server.error.include-stacktrace=never`

- **База данных (PostgreSQL):**
  - URL подключения: `jdbc:postgresql://postgres/mydatabase`
  - Учетные данные:
    - `spring.datasource.username=myuser`
    - `spring.datasource.password=secret`
  - Схема по умолчанию: `auth_service`

- **Настройки Hibernate:**
  - Автоматическое создание/удаление таблиц (для разработки): `create-drop`
  - Показ SQL-запросов: `spring.jpa.show-sql=true`

- **Redis:**
  - Хост: `redis`
  - Порт: `6379` (используется для хранения токенов или сессий)

- **JWT (JSON Web Tokens):**
  - Секретный ключ: `token.signing.key`
  - Время жизни токенов:
    - **Access Token:** 3 часа (10800 сек)
    - **Refresh Token:** 7 дней (604800 сек)

- **Spring Actuator:**
  - Все эндпоинты активатора открыты: `management.endpoints.web.exposure.include=*`

---

## 2. Класс `AuthServiceApplication.java`

Точка входа приложения. Запускает Spring Boot приложение.

```java
@SpringBootApplication
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
```

---

## 3. Конфигурационные классы (папка `config`)

### 3.1. `ApplicationConfig.java`

Конфигурация аутентификации и безопасности.

- **UserDetailsService:** Загружает пользователя из БД по email. Если пользователь не найден, выбрасывает `EntityNotFoundException`.
- **AuthenticationProvider:** Использует `DaoAuthenticationProvider` для проверки учетных данных.
- **PasswordEncoder:** Использует `BCryptPasswordEncoder` для хеширования паролей.
- **AuthenticationManager:** Предоставляет менеджер аутентификации Spring.

---

### 3.2. `MapperConfig.java`

Настройка `ModelMapper` для преобразования DTO <-> сущности.

```java
@Bean
public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    return mapper;
}
```

### 📌 **Конфигурация Swagger и безопасности**  

![alt text](image.png)

#### 🛠 **3.3. OpenApiConfig.java**  
Конфигурация **Swagger/OpenAPI** для документирования API.  

| **Параметр**  | **Значение** |
|--------------|-------------|
| **Заголовок** | `"Authentication module of task management platform"` |
| **Версия** | `1.0.0` |
| **Контакты** | `Отец (Telegram: @Handlest)` |
| **Схема безопасности** | `JWT (Bearer Token)` |

---

#### 🔒 **3.4. WebSecurityConfig.java**  
Настройка безопасности **Spring Security**.  

##### ✅ **Белый список URL**  
Некоторые эндпоинты доступны **без авторизации**:
- **Эндпоинты аутентификации**
- **Swagger**
- **Actuator**  

##### ✅ **Настройка CORS**  
- Разрешены **все источники, методы и заголовки**.  

##### ✅ **Фильтры безопасности**  
- `JwtAuthenticationFilter` проверяет токен в заголовке запроса.  
- **CSRF-защита отключена** (`csrf().disable()`).  
- **Сессии отключены** (`STATELESS`).  

##### ✅ **Выход из системы**  
| **Эндпоинт**  | **Описание** |
|--------------|-------------|
| `/api/v1/auth/logout` | Выход из системы и очистка контекста безопасности |

##### 📝 **Код конфигурации безопасности**:
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors().configurationSource(apiConfigurationSource())
        .and()
        .csrf().disable()
        .authorizeHttpRequests()
            .requestMatchers(WHITE_LIST_URL).permitAll()
            .anyRequest().authenticated()
        .and()
        .sessionManagement().sessionCreationPolicy(STATELESS)
        .and()
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .logout()
            .logoutUrl("/api/v1/auth/logout")
            .addLogoutHandler(logoutHandler)
            .logoutSuccessHandler((req, res, auth) -> 
                SecurityContextHolder.clearContext());
    return http.build();
}
```

### 📂 **4. Папка `domain`**  

#### 📌 **DTO (Data Transfer Objects)**  
Объекты для передачи данных между слоями приложения. Разделены на:  

- **Requests** – Входящие данные (например, запросы от клиента).  
- **Responses** – Исходящие данные (ответы сервера).  
- **UserDto** – Универсальный объект для внутреннего использования.  

---

### 📥 **4.1. Requests (Входящие данные)**  

| **Класс**                   | **Описание**                               | **Поля**          | **Валидация**                                |
|-----------------------------|-------------------------------------------|------------------|----------------------------------------------|
| `UserSignUpRequest`         | Запрос на регистрацию                     | `email, password` | `@Email, @Length(min=2), @NotBlank`        |
| `UserSignInRequest`         | Запрос на аутентификацию                   | `email, password` | `@Email, @Length(min=4, max=255), @NotBlank` |
| `UserRefreshTokenRequest`   | Запрос на обновление токенов               | `refreshToken`   | `@NotBlank`                                |
| `UserAccessTokenRequest`    | Запрос с `accessToken` (например, проверка роли) | `accessToken`    | `@NotBlank`                                |
| `UserUpdateRequest`         | Запрос на обновление данных пользователя  | `email, password` | `@Email, @Length(min=4, max=255)` (поля необязательные) |

---

### 📤 **4.2. Responses (Исходящие данные)**  

| **Класс**                        | **Описание**                           | **Поля**                            | **Пример** |
|----------------------------------|--------------------------------------|------------------------------------|------------------------------------------------|
| `UserJwtAuthenticationResponse` | Ответ с токенами доступа и обновления | `accessToken, refreshToken`        | `{ "accessToken": "eyJhbGciOiJ...", "refreshToken": "eyJhbGciOiJ..." }` |
| `UserGetCurrentUserResponse`    | Данные текущего пользователя         | `id, email, role, registrationDateTime` | `{ "id": 12, "email": "john@gmail.com", "role": "USER", "registrationDateTime": "14-05-2024 20:50" }` |
| `UserUpdateResponse`            | Ответ после обновления данных пользователя | `id, email, role, registrationDateTime` | Аналогично `UserGetCurrentUserResponse` |
| `ErrorResponse`                 | Ошибка выполнения запроса            | `id, message, params`               | `{ "id": "550e8400-e29b-41d4-a716-446655440000", "message": "Invalid token" }` |

---

### 5 Сервис `AuthenticationService`
Обрабатывает регистрацию, аутентификацию, управление `JWT`-токенами и проверку их валидности.

### 🔑 Ключевые методы

| Метод            | Описание                                        | Параметры                     | Возвращаемое значение         | Исключения                                  |
|-----------------|------------------------------------------------|------------------------------|------------------------------|--------------------------------------------|
| `register()`    | Регистрирует нового пользователя и генерирует токены | `UserSignUpRequest`          | `UserJwtAuthenticationResponse` | `ValidationException` (некорректные данные) |
| `authenticate()` | Проверяет учетные данные и возвращает токены   | `UserSignInRequest`          | `UserJwtAuthenticationResponse` | `AuthenticationException` (ошибка аутентификации) |
| `refreshToken()` | Обновляет пару токенов по `refresh-токену`      | `UserRefreshTokenRequest`    | `UserJwtAuthenticationResponse` | `AccessDeniedException` (невалидный токен) |
| `checkToken()`  | Проверяет валидность `access-токена`            | `token: String`              | `Boolean`                    | – |
| `getRoleByToken()` | Извлекает роль пользователя из токена        | `token: String`              | `String`                     | – |
| `extractEmail()` | Извлекает `email` из токена                    | `token: String`              | `String`                     | – |
| `checkEmailExists()` | Проверяет существование пользователя с указанным `email` | `email: String` | `Boolean` | – |

### ⚙️ Особенности

#### 🔹 Генерация токенов:
- **Access Token**: срок действия — `3 часа` (настраивается в `application.properties`).
- **Refresh Token**: срок действия — `7 дней`.
- Токены сохраняются в **Redis** через `TokenRepository`.

#### 🔹 Временный метод `adminCreator()`:
Создает тестового администратора при запуске приложения.

```java
@EventListener(ApplicationReadyEvent.class)
public void adminCreator() {
    // Создание администратора
    UserEntity user = userService.create(...);
    // Вывод токенов в консоль
}
```

## 6 Сервис `UserService` (интерфейс)
Определяет операции для работы с пользователями.

### 🔑 Методы

| Метод           | Описание                                           | Параметры                         | Возвращаемое значение             | Исключения                                  |
|----------------|--------------------------------------------------|---------------------------------|--------------------------------|--------------------------------------------|
| `getAll()`      | Возвращает список всех пользователей (только для администратора) | `–`                             | `List<UserEntity>`              | – |
| `getById()`     | Ищет пользователя по `ID`                        | `id: Long`                      | `UserEntity`                    | `EntityNotFoundException` |
| `getCurrentUser()` | Возвращает данные текущего пользователя       | `user: UserEntity`              | `UserGetCurrentUserResponse`    | – |
| `update()`      | Обновляет данные пользователя                     | `userId: Long, UserUpdateRequest` | `UserUpdateResponse`            | `EntityNotFoundException` |
| `deleteByEmail()` | Удаляет пользователя по `email`                 | `email: String`                 | `–`                             | – |
| `create()`      | Создает нового пользователя                      | `userEntity: UserEntity`        | `UserEntity`                    | `ResponseStatusException` (дубликат `email`) |
| `getByEmail()`  | Ищет пользователя по `email`                      | `email: String`                 | `UserEntity`                    | `EntityNotFoundException` |

---

## 6.1 Сервис `UserServiceImpl` (реализация)
Реализует логику интерфейса `UserService` с использованием Spring Data JPA.

### 🔥 Особенности

- **Транзакции**: Все методы помечены аннотацией `@Transactional`.
- **Валидация**:
  - При создании пользователя проверяется уникальность `email`.
  - При обновлении пароля используется кодирование через `PasswordEncoder`.
- **Обработка ошибок**:
  - `EntityNotFoundException` — если пользователь не найден.
  - `ResponseStatusException` — при попытке создать дубликат `email`.

### 📌 Пример реализации `update()`:

```java
public UserUpdateResponse update(long userId, UserUpdateRequest request) {
    return userRepository.findById(userId).map(user -> {
        Optional.ofNullable(request.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(request.getPassword())
                .ifPresent(pwd -> user.setPassword(passwordEncoder.encode(pwd)));
        userRepository.save(user);
        return UserUpdateResponse.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }).orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
}
```

## 7  Сервис `JwtService`
Отвечает за генерацию и верификацию JWT-токенов.

### 🔑 Ключевые методы

| Метод                   | Описание                                      | Параметры         | Возвращаемое значение | Исключения |
|-------------------------|---------------------------------------------|------------------|--------------------|-----------|
| `generateAccessToken()`  | Создает `access-токен` (срок 3 часа) | `UserEntity`    | `String`          | – |
| `generateRefreshToken()` | Создает `refresh-токен` (срок 7 дней) | `UserEntity`    | `String`          | – |
| `extractEmail()`         | Извлекает `email` из токена                 | `token: String` | `String`          | – |
| `extractRole()`          | Извлекает `роль` пользователя из токена     | `token: String` | `String`          | – |

### 🔥 Особенности

- **Использует HMAC256** для подписи токенов:

```java
Algorithm.HMAC256(jwtSigningKey);
```