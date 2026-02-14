package com.sea.desafio_backend.model.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Classe base para todas as entidades
 * Contém campos comuns de auditoria (createdAt, updatedAt)
 * 
 * @MappedSuperclass: Indica que esta classe não será uma tabela no banco,
 * mas seus campos serão herdados pelas entidades filhas
 */
@Data
@MappedSuperclass
@lombok.EqualsAndHashCode
public abstract class BaseEntity {

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
