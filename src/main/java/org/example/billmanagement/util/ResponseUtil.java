package org.example.billmanagement.util;

import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class ResponseUtil {
    public static <T> ResponseEntity<T> wrapOrNotFound(Optional<T> optional) {
        return optional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
