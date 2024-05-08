package jpa.v5.baseentity;

import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

/**
 * 다른 entity에서 공통 으로 사용할 필드들을 추상화한 entity
 * */
@MappedSuperclass
public abstract class BaseEntity {
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
}
