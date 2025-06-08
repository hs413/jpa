package jpa.v6.collection;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class AddressEntity {
    @Id @GeneratedValue
    private Long id;


}
