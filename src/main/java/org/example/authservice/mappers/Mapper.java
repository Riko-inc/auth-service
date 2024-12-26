package org.example.authservice.mappers;

public interface Mapper<A,B> {
    B mapToDto(A a);
    A mapFromDto(B a);
}
