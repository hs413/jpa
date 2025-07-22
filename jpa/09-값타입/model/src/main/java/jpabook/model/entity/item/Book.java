package jpabook.model.entity.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("B")
public class Book extends Item{
    private String author;
    private String isbn;
}
