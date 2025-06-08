package jpa.v5.joined;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;


/**
 * 일반적인 상속 관계
 * */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DIS_TYPE")
public class Item {
    @Id @GeneratedValue
    private Long id;

    private String name;
}
