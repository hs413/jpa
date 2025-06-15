package jpa.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Setter
@DiscriminatorValue("M")
@Getter
@Entity
public class Movie extends Item{
    private String director;
    private String actor;
}
