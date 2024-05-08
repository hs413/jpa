package jpa.v5.joined;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("A")
public class Movie extends Item{
    private String director;
    private String actor;
}
