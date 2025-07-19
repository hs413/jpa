## 글로벌 페치 전략 설정

### [주문 엔티티](src/main/java/jpabook/model/entity/Order.java)
- member와 delivery를 지연 로딩으로 설정
- delivery, orderitems 필드에 cascade = CascadeType.ALL로 영속성 전이 설정


### [주문상품 엔티티](src/main/java/jpabook/model/entity/OrderItem.java)
- item과 order를 지연 로딩으로 설정

```java
// 영속성 전이 X
Delivery delivery = new Delivery();
em.persist(delivery); //persist

Orderitem orderItem1 = new OrderItem();
OrderItem orderItem2 = new OrderItem();
em.persist(orderItem1); //persist
em.persist(orderItem2); //persist

Order order = new Order();
order.setDelivery(delivery);
order.addOrderItem(orderItem1);
order.addOrderItem(orderItem2);

em.persist(order); //persist

// 영속성 전이 O
Delivery delivery = new Delivery();
Orderitem orderItem1 = new Orderitem();
Orderitem orderItem2 = new OrderItem();

Order order = new Order();
order.setDelivery(delivery);
order.addOrderItem(orderItem1);
order.addOrderItem(orderItem2);
em. persist(order);
```
