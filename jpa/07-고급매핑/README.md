# 고급매핑

## 상속 관계 매핑
DB의 슈퍼타입 서브타입 관계를 매핑하는 것

### 조인 전략
- 엔티티를 모두 테이블로 만들고 조회할 때 조인 사용
- 자식 테이블이 부모 테이블의 기본 키를 받아 기본 키 + 외래 키로 사용
- 타입을 구분하는 컬럼 필요

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;
    private String name;
    private int price;
}

@Entity
@DiscriminatorValue("A")
public class Album extends Item {}

@Entity
@DiscriminatorValue("M")
public class Movie extends Item {}
```
- @Inheritance(strategy = InheritanceType.JOINED)
  - 상속 매핑은 부모 클래스에 @Inheritance를 사용
- @DiscriminatorColumn(name = "DTYPE")
  - 자식 테이블을 구분하기 위한 컬럼을 지정하는 속성
  - 기본값 DTYPE
    - @DiscriminatorColumn(name = "DTYPE") = @DiscriminatorColumn
- @DiscriminatorValue("M")
  - 구분 컬럼에 입력할 값
- @PrimaryKeyJoinColumn
  - 자식 테이블은 부모 테이블의 ID 컬럼명을 기본값으로 사용
  - ID 컬럼을 변경하고 싶을 때 사용
```java
@Entity
@DiscriminatorValue("B")
@PrimaryKeyJoinColumn(name = "BOOK_ID") // ID 재정의
public class Book extends Item {}
```

**장점**
- 정규화 된 테이블
- 외래 키 참조 무결성 제약조건 활용 가능
- 효율적인 저장공간 사용

**단점**
- 많은 조인 사용으로 성능 저하가 있을 수 있음
- 조회 쿼리가 복잡
- 데이터 등록 시 INSERT 두 번 실행

### 단일 테이블 전략
- 테이블을 하나만 사용해서 통합
- 조회할 때 조인을 사용하지 않음
- 자식 엔티티의 매핑 컬럼은 모두 null을 허용 해야함
```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;
}
```
**장점**
- 조인이 없어 조회가 빠름
- 조회 쿼리가 단순

**단점**
- 자식 엔티티의 매핑 컬럼에 null 허용
- 테이블이 커질 수 있고 상황에 따라 조회 성능이 느려질 수 있음

### 구현 클래스 테이블 전략
- 서브 타입마다 하나의 테이블을 만든다
- 권장하지 않음
- 구분 컬럼을 사용 안함
```java
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;
}
```
**장점**
- 서브 타입을 구분해서 처리할 때 효과적
- not null 제약조건 사용 가능

**단점**
- 자식 테이블을 함께 조회할 때 성능이 안 좋음
- 자식 테이블 통합 쿼리가 어려움

## @MappedSuperclass
- 부모 클래스는 테이블과 매핑하지 않고 자식 클래스에게 매핑 정보만 제공할 때 사용
- 직접 생성해서 사용할 일이 없으므로 **추상 클래스로 만드는 것을 권장**
- 엔티티가 공통으로 사용하는 매핑 정보를 모으는 역할(등록일, 수정일 등)

![mapped](images/img.png)

```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private String name;
}
@Entity
public class Member extends BaseEntity {
    // ID, NAME 상속
    private String email;
}
@Entity
public class Seller extends BaseEntity {
    // ID, NAME 상속
    private String shopName;
}
```
- @AttributeOverrides, @AttributeOverride
  - 매핑 정보를 재정의할 때 사용
```java
@Entity
// id 속성을 MEMBER_ID로 재정의
@AttributeOverride(name = "id", column = @Colunm (name = "MEMBER_ID"))
public class Member extends BaseEntity { ... }

@Entity
// 두 개 이상의 속성을 재정의
@AttributeOverrides({
        @AttributeOverride (name = "id", column = @Column (name = "MEMBER_ID")),
        @AttributeOverride (name = "name", column = @Column (name = "MEMBER_NAME"))
})
public class Member extends BaseEntity { ... }

```
- @AssociationOverrides, @AssociationOverride
  - 연관관계를 재정의할 때 사용

## 복합 키
- 식별자를 두 개 이상 사용하려면 별도의 식별자 클래스를 만들어야 함
- 식별자를 구분하기 위해 equals와 hashCode를 사용하므로 두 메서드를 overriding 해야 함
- 복합 키를 지원하기 위해 @IdClass, @EmbeddedId 2가지 방법을 제공

[//]: # (### 식별 관계)
[//]: # (- 부모 테이블의 기본 키를 자식 테이블에서 기본 키 + 외래 키로 사용)

[//]: # (### 비식별 관계)
[//]: # (- 부모 테이블의 기본 키를 자식 테이블에서 외래 키로만 사용)

### @IdClass
- 관계형 DB에 가까운 방법

```java
@Entity
@IdClass(ParentId.class)
public class Parent {
    @Id
    @Column(name = "PARENT_ID1")
    private String id1;
    
    @Id
    @Column(name = "PARENT_ID2")
    private String id2;
    
    private String name;
}
// 식별자 클래스
public class ParentId implements Serializable {
    private String id1;
    private String id2;
    
    public ParentId() {}
    
    public ParentId(String id1, String id2) {
      this.id1 = id1;
      this.id2 = id2;
    }
  
    @Override
    public boolean equals(Object o) {...}
    @Override
    public int hashCode() {...}
}
```
- 식별자 클래스의 속성명과 엔티티 식별자의 속성명이 같아야 한다.
- Serializable 인터페이스를 구현해야 한다
- equals, hashCode를 구현해야 한다.
- 기본 생성자가 있어야 한다.
- 식별자 클래스는 public이어야 한다.
 
**저장 및 조회**

```java
// 저장
Parent parent = new Parent();
parent.setId1("id1");
parent.setId2("id2");
parent.setName("parentName");
em.persist(parent);

// 조회
ParentId parentId = new ParentId("id1","id2");
Parent parent = em.find(Parent. class, parentId);
```
**자식 클래스**
```java
@Entity
public class Child {
    @Id
    private String id;
    
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID1", 
                    referencedColumnName = "PARENT_ID1"),
            // referencedColumnName값이 name값과 같으면 생략 가능 
            @JoinColumn(name = "PARENT_ID2")
    })
    private Parent parent;
}
```

### @EmbeddedId
- 객체지향에 가까운 방법

```java
@Entity
public class Parent {
    @EmbeddedId
    private ParentId id;
    private String name;
}
// 식별자 클래스
@Embeddable
public class ParentId implements Serializable {
    @Column(name = "PARENT_ID1")
    private String id1;
    
    @Column(name = "PARENT_ID2")
    private String id2;
    
    // equals, hashCode 구현
}
```
- 식별자 클래스에 기본 키를 직접 매핑
- Serializable 인터페이스를 구현해야 한다
- equals, hashCode를 구현해야 한다.
- 기본 생성자가 있어야 한다.
- 식별자 클래스는 public이어야 한다.
 
**저장 및 조회**
```java
// 저장
Parent parent = new Parent();
ParentId parentId = new ParentId("id1","id2");
parent.setId(parentId);
parent.setName("parentName");
em.persist(parent);

// 조회
ParentId parentId = new ParentId("id1","id2");
Parent parent = em.find(Parent.class, parentId);
```

### 식별 관계 매핑
**@IdClass**
```java
@Entity
public class Parent {
    @Id
    @Column(name = "PARENT_ID")
    private String id;
}

@Entity
@IdClass(ChildId.class)
public class Child {
    @Id
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    public Parent parent;
    
    @Id
    @Column(name = "CHILD_ID")
    private String childId;
}
// 자식 ID 클래스
public class ChildId implements Serializable {
  private String parent;
  private String childId;
  //equals, hashCode
}

//손자
@Entity
@IdClass(GrandChildId.class)
public class GrandChild {
  @Id
  @ManyToOne
  @JoinColumns({
          @JoinColumn(name = "PARENT_ID"),
          @JoinColumn(name = "CHILD_ID")
  })
  private Child child;
  
  @Id @Column(name = "GRANDCHILD_ID")
  private String id;
}

//손자 ID
public class GrandChildId implements Serializable {
    private ChildId child;
    private String id;
    //equals, hashCode
}
```
- 식별 관계는 기본 키와 외래 키를 같이 매핑해야 한다

**@EmbeddedId**
- @EmbeddedId로 식별 관계를 구성할 때는 @MapsId를 사용해야 한다
```java
@Entity
public class Parent {
    @Id
    @Column(name = "PARENT_ID")
    private String id;
}

@Entity
public class Child {
    @Embeddedld
    private ChildId id;
  
    @MapsId("parentId") // ChildId.parentId와 매핑
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    public Parent parent;
}

//자식 ID
@Embeddable
public class ChildId implements Serializable {
    private String parentId; // @MapsId("parentId")로 매핑
    @Column(name = "CHILD_ID")
    private String id;
    //equals, hashCode
}
@Entity
public class GrandChild {
    @EmbeddedId
    private GrandChildId id;
  
    @MapsId("childId") // GrandChildId.childId와 매핑
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID"),
            @JoinColumn(name = "CHILD_ID")
    })
    private Child child;
}
    
//손자 ID
@Embeddable
public class GrandChildId implements Serializable {
    private ChildId childId;
    
    @Column(name = "GRANDCHILD_ID")
    private String id;
    //equals, hashCode
}
```
### 일대일 식별 관계
- 자식 테이블은 부모 테이블의 기본 키 값만 사용
```java
//부모
@Entity
public class Board {
    @Id @GeneratedValue
    @Column (name = "BOARD_ID")
    private Long id;
    
    @OneToOne(mappedBy = "board”)
    private BoardDetail boardDetail;
}

//자식
@Entity
public class BoardDetail {
    @Id
    private Long boardId;

    // 식별자가 컬럼 하나면 @MapsId를 사용하고 속성 값은 비워두면 된다 
    @MapsId //BoardDetail.boardId
    @OneToOne
    @JoinColunm (name="BOARD_ID")
    private Board board;
}
```
- 객체지향 관점에서
  - 복합 키 보다 대리 키(자동생성) 사용이 편함
  - 비식별 관계가 편함
- 선택적 비식별 관계(Nullable) 필수적 비식별 관계(NOT NULL) 권장
