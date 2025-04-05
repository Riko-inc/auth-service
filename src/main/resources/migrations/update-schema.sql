-- liquibase formatted sql
-- changeset Riko:add_users_table

CREATE SCHEMA IF NOT EXISTS auth_service;
CREATE SEQUENCE IF NOT EXISTS auth_service.users_sequence START WITH 1 INCREMENT BY 50;
CREATE TABLE IF NOT EXISTS auth_service.users
(
    user_id                BIGINT       NOT NULL PRIMARY KEY,
    password               VARCHAR(255) NOT NULL,
    email                  VARCHAR(255) NOT NULL UNIQUE,
    is_active              BOOLEAN      NOT NULL,
    role                   VARCHAR(255) NOT NULL,
    registration_date_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
);