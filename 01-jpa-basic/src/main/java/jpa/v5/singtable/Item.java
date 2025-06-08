package jpa.v5.singtable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jpa.v5.baseentity.BaseEntity;


/**
 * 하나의 테이블에 모든 정보를 저장
 * 자식을 구분하기 위해 type 컬럼이 추가 됨
 * */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Item extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    private String name;
    private int price;

    // 자식을 구분하기 위한 타입
    private String dType;

    private String director;
    private String actor;
    private String artist;
    private String author;
}
