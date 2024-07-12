package jpabook.model.entity;

import jakarta.persistence.*;
import jpabook.model.entity.item.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ORDER_ITEM")
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    @ManyToOne
    @Column(name = "ORDER_ID")
    private Order order;

    private int orderPrice;
    private int count;
}
