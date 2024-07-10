# 연관관계 매핑2

## 다대일 [N:1]
- 외래 키(연관관계 주인)는 항상 N에 있다
- 가장 많이 사용됨

### 다대일 단방향
```java
@Entity
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;
    
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;
}

@Entity
public class Team {
    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;
}

```
### 다대일 양방향
```java
@Entity
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;
    
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    public void setTeam(Team team) {
        this.team = team;
        
        if (!team.getMembers().contains(this)) {
            team.getMembers().add(this);
        }
    }
}

@Entity
public class Team {
    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<Member>();
  
    public void addMember(Member member) {
        this.members.add(member);
        
        if (member.getTeam() != this) {
            member.setTeam(this);
        }
    }
}

```
** 양방향 **
- 외래 키가 있는 쪽이 연관관계 주인
- 항상 서로를 참조해야 함

## 일대다 [1:N]
- 엔티티를 하나 이상 참조할 수 있어 컬렉션(List, Set, Map) 중 하나 사용함

### 일대다 단방향
- 보통 엔티티 객체는 매핑한 테이블의 외래 키를 관리하지만 일대다 단방향은 반대 테이블의 외래 키를 관리한다
  - 외래 키는 항상 N에 해당하는 테이블에 있으나 해당 매핑은 N에 해당하는 객체에 참조 필드가 없음
  - 1에 해당하는 객체에서 N을 참조하는 컬렉션을 사용해 외래 키를 관리

```java
@Entity
public class Team {
    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;
  
    @OneToMany
    @JoinColumn(name = "TEAM_ID") // MEMBER 테이블의 TEAM_ID (FK)
    private List<Member> members = new ArrayList<Member>();
}
@Entity
public class Member {
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;
}
```
- 일대다 단방향 관계 매핑 시 @JoinColumn 명시 필수
  - 명시하지 않으면 조인 테이블 전략을 기본으로 사용(7장에서 설명)
- 매핑한 객체가 관리하는 외래 키가 다른 테이블에 있음
  - 연관관계 처리를 위한 UPDATE SQL 추가 실행 필요
- 일대다 단방향보다 다대일 양방향 사용 권장


### 일대다 양방향
- 존재하지 않음, 대신 다대일 양방향 매핑 사용

## 일대일 [1:1]
- 양쪽이 서로 하나의 관계만을 가짐
- 주 테이블이나 대상 테이블 중에서 외래 키 선택 가능

### 주 테이블 외래 키
- 주 테이블에 외래 키를 두고 대상 테이블을 참조
- JPA 매핑 편리
- 주 테이블만 조회해도 대상 테이블에 데이터가 있는지 확인 가능
- 값이 없으면 외래 키에 null 허용

**단방향**
```java
@Entity
public class Member {
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;
    
    @OneTo0ne
    @JoinColumn(name = "LOCKER_ID")
    private Locker locker;
}
@Entity
public class Locker {
    @Id @GeneratedValue
    @Column(name = "LOCKER_ID")
    private Long id;
    private String name;
}
```
**양방향**
```java
@Entity
public class Member {
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;
    
    @OneTo0ne
    @JoinColumn(name = "LOCKER_ID")
    private Locker locker;
}
@Entity
public class Locker {
    @Id @GeneratedValue
    @Column(name = "LOCKER_ID")
    private Long id;
    private String name;

    @OneToOne(mappedBy = "locker")
    private Member member;
}
```

### 대상 테이블 외래 키
- 일대일에서 일대다 관계로 변경 시 테이블 구조를 유지할 수 있음
- 프록시 기능의 한계로 지연로딩으로 설정해도 항상 즉시 로딩됨
  - where문을 이용해 쿼리를 날려야 하므로
- 단방향은 지원하지 않음

**양방향**
```java
@Entity
public class Member {
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;
    
    @OneToOne(mappedBy = "locker")
    private Locker locker;
}
@Entity
public class Locker {
    @Id @GeneratedValue
    @Column(name = "LOCKER_ID")
    private Long id;
    private String name;

    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}
```

## 다대다 [N:M]
- 정규화된 테이블 2개로 다대다 표현 불가능
  - 일대다, 다대일 관계로 풀어서 사용
- 객체는 다대다 관계를 만들 수 있음

### 다대다 단방향 
```java
@Entity
public class Member {
    @Id
    @Column(name = "MEMBER_ID")
    private String id;
    private String username;
    @ManyToMany
    @JoinTable(name = "MEMBER_PRODUCT",
            joinColumns = @JoinColumn(name = "MEMBER_ID"),
            inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID"))
    private List<Product> products = new ArrayList<Product>();
}

@Entity
public class Product {
    @Id
    @Column(name = "PRODUCT_ID")
    private String id;
    private String name;
}
```
- @ManyToMany와 @JoinTable을 사용해 연결 테이블 매핑
  - @JoinTable.name: 연결 테이블 지정
  - @JoinTable.joinColumn: 현재 객체와 매핑할 컬럼 정보(현재 객체 ID)
  - @JoinTable.inverseJoinColumn: 반대 객체와 매핑할 조인 컬럼 정보(반대 객체 ID)

**저장**
```java
public void save() {
    Product productA = new Product();
    productA.setId("productA");
    productA.setName("상품A");
    em.persist(productA);
    
    Member member1 = new Member();
    member1.setId("member1");
    member1.setUsername("회원1");
    member1.getProducts().add(productA); //연관관계 설정
    em.persist(member1);
}
```
**탐색**
```java
public void find () {
    Member member = em.find(Member.class, "member1");
    List<Product> products = member.getProducts(); //객체 그래프 탐색
    for (Product product : products) {
        System.out.printin("product.name = " + product.getName());
    }
}
```

### 다대다 양방향 
```java
@Entity
public class Product {
    @Id
    private String id;

    // 역방향추가
    @ManyToMany(mappedBy = "products") 
    private List<Member> members;
}
```

### 다대다 한계
- 연결 테이블이 단순 연결만 하는 경우는 없음
  - 추가적인 컬럼이 필요할 수 있음

### 다대다 개선
- 연결 엔티티를 추가하여 테이블처럼 일대다, 다대일 관계로 풀어서 매핑

```java
@Entity
public class Member {
    @Id
    @Column(name = "MEMBER_ID")
    private String id;
    //역방향
    @OneToMany(mappedBy = "member")
    private List<MemberProduct> memberProducts;
}

@Entity
public class Product {
    @Id
    @Column(name = "PRODUCT_ID")
    private String id;
    private String name;
}

@Entity
@IdClass (MemberProductId. class)
public class MemberProduct {
    @Id
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
  
    @Id
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;
    private int orderAmount;
}

// 회원상품 식별자 클래스
public class MemberProductId implements Serializable {
    private String member;
    private String product;

    @Override
    public boolean equals (Object o) {...}
  
    @Override
    public int hashCode () {...}
}
```
**복합 키 조건**
- 별도의 식별자 클래스로 만들어야 함한다
- Serializable을 구현해야 한다
- equals와 hashCode 메소드를 구현해야 한다
- 기본생성자가 있어야한다
- 식별자 클래스는 public이어야 한다
- @IdClass를 사용하는 방법 외에 @EmbeddedId를 사용하는 방법도 있다

```java

public void find() {
    // 기본 키 값생성이 필요
    MemberProductId memberProductId = new MemberProductId();
    memberProductId.setMember("member1");
    memberProductId.setProduct("productA");
    
    MemberProduct memberProduct = em.find(MemberProduct.class, memberProductld);
}
```

### 다대다 개선 - 새 기본 키 사용
```java
// MemberProduct -> Order
@Entity
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long Id;
    
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;
    
    private int orderAmount;
}

public void find() {
  Long orderId = 1L;
  Order order = em.find(Order.class, orderId);
}
```
