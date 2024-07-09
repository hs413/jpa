### [회원 엔티티](src/main/java/jpabook/model/entity/Member.java)
### [주문 엔티티](src/main/java/jpabook/model/entity/Order.java)
- 회원과 주문은 일대다
- 주문과 회원은 다대일
- 연관관계의 주인은 외래 키가 있는 Order.member

### [주문상품 엔티티](src/main/java/jpabook/model/entity/OrderItem.java)
- 주문과 주문상품은 일대다
- 주문상품과 주문은 다대일
- 연관관계의 주인은 외래 키가 있는 OrderItem.order

### [상품 엔티티](src/main/java/jpabook/model/entity/Item.java)
- 상품에서 주문상품을 참조할 일이 거의 없음
  - OrderItem -> Item 다대일 단방향

### 변경 사항
- 외래 키 -> 객체 참조
- 객체 그래프 탐색 가능
- JPQL 사용 가능

