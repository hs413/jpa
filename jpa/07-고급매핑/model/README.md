## 요구사항 추가
- 상품의 종류는 음반, 도서, 영화가 있고 이후 더 확장될 수 있다.
- 모든 데이터는 등록일과 수정일이 있어야 한다

### 상속관계 매핑
### [상품 엔티티](src/main/java/jpabook/model/entity/item/Item.java)
- item 패키지로 이동
- 상품과 자식 클래스를 모아둠
- 단일 테이블 전략 사용
- 직접 생성하지 않으므로 추상 클래스로 만듦


### [기본 엔티티](src/main/java/jpabook/model/entity/BaseEntity.java)
- 모든 엔티티에 등록일과 수정일을 추가하기 위한 기본 엔티티
- 모든 엔티티는 해당 엔티티를 상속하면 됨
