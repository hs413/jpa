package jpa.querydsl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * querydsl 빌드 테스트 엔티티
 * */
@Entity
@Data
public class Hello {
    @Id @GeneratedValue
    private Long id;

}
