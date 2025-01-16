package org.example.billmanagement.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public class ResponseUtil {
    public static <T> ResponseEntity<T> wrapOrNotFound(Optional<T> result) {
        return (ResponseEntity) result.map((response) -> ResponseEntity.ok().body(result)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
