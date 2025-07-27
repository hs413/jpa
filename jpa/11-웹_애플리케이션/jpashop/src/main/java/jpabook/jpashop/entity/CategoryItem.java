package jpabook.jpashop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

public class CategoryItem {
    @Id
    @GeneratedValue
    @Column(name = "")
    private Long id;
}
