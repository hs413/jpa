package jpa.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Setter
@DiscriminatorValue("B")
@Getter
@Entity
public class Book extends Item{
    private String author;
    private String isbn;
}
