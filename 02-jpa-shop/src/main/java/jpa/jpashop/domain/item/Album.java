package jpa.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Setter
@DiscriminatorValue("A")
@Getter
@Entity
public class Album extends Item{
    private String artist;
    private String etc;
}
