package org.example.billmanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@Table(name = "roles")
public class Role {

    @NotNull
    @Size(max = 50)
    @Id
    @Column(length = 50)
    private String name;
}
