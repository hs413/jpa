## JPQL
- 엔티티 객체를 조회하는 객체지향 쿼리
- SQL과 비슷하고 ANSI 표준 SQL이 제공하는 기능을 유사하게 지원
- SQL을 추상화해서 특정 데이터베이스에 의존하지 않는다

### 기본 문법
```sql
SELECT m FROM Member AS m where m.username = ''
```
- 엔티티와 속성은 대소문자 구분
  - `JPQL` 키워드는 대소문자 구분하지 않음 (`SELECT`, `FROM`, `where`)
- 테이블 이름이 아닌 엔티티 이름을 사용
- 별칭은 필수, `as`는 생략 가능

**TypeQuery, Query**
- `TypeQuery`: 반환 타입을 명확하게 지정할 수 있으면 사용
- `Query`: 반환 타입을 명확하게 지정할 수 없으면 사용
- createQuery() 두 번째 파라미터에 반환할 타입을 지정하면 TypeQuery를, 지정하지 않으면 Query를 반환
```java
// TypeQuery 
// 반환 타입이 명확함
TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
TypedQuery<String> query1 = em.createQuery("select m.username from Member m", String.class);

// Query
// 반환 타입이 명확하지 않음
Query query2 = em.createQuery("select m.username, m.age from Member m");
```
**결과 조회**
- `query.getResultList()` : 결과가 하나 이상일 때
  - 리스트 반환, 결과가 없으면 빈 리스트 반환
- `query.getSingleResult()` : 결과가 하나 일 때
  - 단일 객체 반환 (값이 보장)
  - 결과가 없으면 : `javax.persistence.NoResultException`
  - 둘 이상이면 : `javax.persistence.NonUniqueResultException`

### 파라미터 바인딩

- JPQL은 이름 기준 파라미터 바인딩을 지원
  - 파라미터를 이름으로 구분하는 방법

**이름 기준 바인딩**
```sql
SELECT m FROM Member m where m.username=:username
query.setParameter("username", usernameParam);
```

**위치 기준 바인딩**

```sql
SELECT m FROM Member m where m.username=?1
query.setParameter(1, usernameParam);
```
- 위치 기준은 사용하지 않는 것을 권장
  - 중간에 파라미터가 추가되면 오류 발생

### 프로젝션
- select 절에 조회할 대상을 지정하는 것

**프로젝션 대상**
- 엔티티
  - SELECT **m** FROM Member m
  - SELECT **m.team** FROM Member m
- 임베디드 타입
  - 임베디드 타입은 조회의 시작점이 될 수 없음
    ```sql
    -- 잘못된 쿼리
    SELECT a FROM Address a;
      
    -- 올바른 쿼리 - 시작점은 상위 엔티티
    SELECT m.address FROM Member m 
    ```    
- 스칼라 타입
  - 숫자. 문자, 날짜와 같은 기본 데이터 타입들을 스칼라 타입이라고 한다
    ```sql
    SELECT m.username, m.age FROM Member m
    ```
  - `DISTINCT`를 사용하여 중복을 제거할 수 있다

**여러 값 조회**

- Query 타입으로 조회
    ```java
        List resultList = em.createQuery("select m.username, m.age from Member m")
            .getResultList();
    									 
        Object o = resultList.get(0);
        Object[] result = (Object[]) o;
    ```
- Object[] 타입으로 조회
    ```java
        List<Object[]> resultList = em.createQuery("select m.username, m.age from Member m")
            .getResultList();
                
    ```
- new 명령어로 조회
    ```java
    public class MemberDTO {
    
        private String username;
        private int age;
    
        public MemberDTO(String username, int age) {
            this.username = username;
            this.age = age;
        }
    }
    // 조회
    {
        List<MemberDTO> resultList = em
            .createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
            .getResultList();
    }
    ```
- 단순 값을 DTO로 바로 조회
- 패키지 명을 포함한 전체 클래스명 입력
- 순서와 타입이 일치하는 생성자 필요

### 페이징
- `setFirstResult`(int startPosition) : 조회 시작 위치(0부터 시작)
- `setMaxResults`(int maxResult) : 조회할 데이터 수

```java
//페이징 쿼리
 String jpql = "select m from Member m order by m.name desc";
 List<Member> resultList = em.createQuery(jpql, Member.class)
 .setFirstResult(10) // 10부터
 .setMaxResults(20)  // 20개 데이터를 가져옴
 .getResultList();
```

### 조인
- 조인 시 연관 필드를 사용한다 
- ex)`FROM Member m JOIN m.team t`

**내부 조인**
```sql
SELECT m FROM Member m [INNER] JOIN m.team t
```

**외부 조인**

```sql
SELECT m FROM Member m LEFT [OUTER] JOIN m.team t
```

**세타 조인**
- 연관관계가 없는 엔티티를 조인할 수 있다
```sql
select count(m) from Member m, Team t where m.username = t.name
```

**ON 절**
- JPA 2.1 부터 지원
- 조인 대상 필터링
- JPQL은 ON 절에 식별자 비교를 생략
```sql
-- 팀 이름이 A인 회원만 조인
-- JPQL
SELECT m, t FROM
Member m LEFT JOIN m.team t on t.name = 'A'

-- SQL
SELECT m.*, t.* FROM
  Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and t.name='A'
```

### 페치 조인
- JPQL에서 성능 최적화를 위해 제공하는 기능
- 지연 로딩으로 설정한 엔티티를 한번에 조회하는 경우 사용
  - 지연 로딩으로 설정한 경우 연관 객체가 한번에 조회되지 않고 프록시 객체로 초기화 됨
  - 페치 조인 사용 시 연관 객체를 모두 조회하여 실제 엔티티로 초기화 함  
- `JOIN FETCH` 사용

**엔티티 페치 조인**
```sql
-- 회원을 조회하면서 연관된 팀도 함께 조회
-- JPQL
select m from Member m join fetch m.team;

-- SQL
SELECT 
    M.*, T.* 
FROM MEMBER M INNER JOIN TEAM T 
    ON M.TEAM_ID = T.ID
```
- 페치 조인은 별칭을 사용할 수 없다

**컬렉션 페치 조인**
```sql
-- JPQL
select t
from Team t join fetch t.members
where t.name = '팀A'

-- SQL
SELECT 
    T.*, M.*
FROM TEAM T INNER JOIN MEMBER M 
    ON T.ID=M.TEAM_ID
WHERE T.NAME = '팀A'
```
- 일대다 관계이기 때문에 결과가 증가할 수 있다

**DISTINCT**
- 중복된 결과를 제거
- SQL에 DISTINCT를 추가하고 애플리케이션에서 한 번 더 중복을 제거한다
- 컬렉션 페치 조인과 같이 결과가 늘어나는 경우 사용

**글로벌 로딩 전략**
- 엔티티에 직접 적용한다
- 애플리케이션 전체에 영향을 미친다
```java
// 글로벌 로딩 전략
@OneToMany(fetch = FetchType.LAZY)
```
- 즉시 로딩으로 설정할 경우 사용하지 않는 엔티티를 자주 로딩하여 오히려 성능 저하가 생길 수 있음
- 따라서 **글로벌 로딩 전략은 지연 로딩**을 권장
- **최적화가 필요할 때 페치 조인을 사용**
  - 글로벌 로딩 전략보다 페치 조인이 우선 적용됨

**페치 조인 특징**
- 별칭을 줄 수 없음
  - select, where, sub query에 사용할 수 없음
- 둘 이상의 컬렉션을 페치할 수 없음
- 컬렉션 페치 조인 시 페이징 API 사용 불가
  - 단일 값 연관 필드(일대일, 다대일)들은 가능

### 서브 쿼리
- where, having 절에만 사용 가능

```sql
-- 나이가 평균보다 많은 회원
select m from Member m
where m.age > (select avg(m2.age) from Member m2)

-- 한 건이라도 주문한 고객
select m from Member m
where (select count(o) from Order o where m = o.member) > 0
```
**서브 쿼리 지원 함수**
- `[NOT] EXISTS (subquery)` : 서브쿼리에 결과가 존재하면 참
  - `{ALL | ANY | SOME} (subquery)`
  - `ALL` : 모두 만족하면 참
  - `ANY`, `SOME` : 조건을 하나라도 만족하면 참
- `[NOT] IN (subquery)` : 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참

### 다형성 쿼리
 **Type**
- 조회 대상을 특정 자식으로 한정
```sql
-- Item 중에 Book, Movie를 조회
-- JPQL
select i from Item i
where type(i) IN (Book, Movie)

-- SQL
select i from i
where i.DTYPE in ('B', 'M')
```

**TREAT (JPA 2.1)**
- 자바의 타입 캐스팅과 유사
- 상속 구조에서 부모 타입을 특정 자식 타입으로 다룰 때 사용
- FROM, WHERE, SELECT(하이버네이트 지원) 사용

```sql
-- 부모인 Item과 자식 Book
-- JPQL
select i from Item i
where treat(i as Book).auther = 'kim'

-- SQL
select i.* from Item i
where i.DTYPE = 'B' and i.auther = 'kim'
```

### 엔티티 직접 사용
**기본 키**
- JPQL에서 엔티티 객체 사용 = 해당 엔티티의 기본 키 사용

```sql
-- 엔티티의 아이디 사용
select count(m.id) from Member m;

-- 엔티티 직접 사용
select count(m) from Member m;

-- 실행 결과는 같음, JPQL의 count(m)이 SQL에서 count(m.id)로 변환 됨
select count(m.id) as cnt from Member m
```

```sql
-- where절에 엔티티 사용
select m from Member m where m = :member
                     
-- 실행된 SQL 
select m.* from Member m
where m.id=?
```

**외래 키**

```sql
-- JPQL
select m from Member m where m.team = :team;
           
-- SQL
select m.* from Member m
where m.team_id=?
```

### Named 쿼리: 정적 쿼리
- 미리 이름을 정의해서 사용하는 JPQL
- **애플리케이션 로딩 시점에 문법을 체크하고 파싱함**
  - 파싱된 결과를 재사용하므로 성능상 이점
  - 빠른 오류 확인 가능

**어노테이션에 정의**
```java
@Entity
// @NamedQuery.name: 쿼리 이름
// @NamedQuery.query: 사용할 쿼리
@NamedQuery(
    name = "Member.findByUsername",
    query="select m from Member m where m.username = :username")
public class Member {}

// 사용
List<Member> resultList =
    em.createNamedQuery("Member.findByUsername", Member.class)
        .setParameter("username", "회원1")
        .getResultList();
```
**@NamedQueries**
- 2개 이상의 Named 쿼리를 정의
```java
@Entity
@NamedQueries({
    @NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username"),
    @NamedQuery(
        name = "Member.count",
        query = "select count(m) from Member m")
})
```
**`@NamedQuery` 어노테이션 상세**
```java
@Target({TYPE})
public @interface NamedQuery {
    String name();     // Named 쿼리 이름 (필수)
    String query();    // JPQL 정의 (필수)
    LockModeType lockMode() default NONE;
    QueryHint[] hints() default {};
```
- `lockMode` : 쿼리 실행 시 락을 건다
- `hints` : JPA 구현체에게 제공하는 힌트, 2차 캐시를 다룰 때 사용
- **어노테이션에 정의할 경우 오류를 금방 발견할 수 있음**

**XML에 정의**
- 멀티 라인을 쉽게 작성할 수 있음
- XML이 애너테이션 보다 우선
- 애플리케이션 운영 환경에 따라 다른 XML을 배포할 수 있다

```xml
<![CDATA[
    select m 
    from Member m 
    where m.username = :username
]]>
```

## QueryDSL
- JPQL 쿼리 빌더
- 문법 오류를 컴파일 단계에서 잡을 수 있다

```java
// JPQL의 별칭을 m으로 함
QMember qMember = new QMember("m");

List<Member> members = query.from(qMember)
        .where (qMember .name.eq("회원1"))
        .orderBy(qMember.name.desc())
        .list(qMember);

// 생성된 쿼리
select m from Member m
where m.name = ?1
order by m.name desc
```
### 검색 조건
```java
List<Item> list = query.from(item)
    .where(item.name.eq("상품").and(item.price.gt(20000)))
    .list(item); // 프로젝션 지정

// and 연산
.where(item.name.eq("상품"), item.price.gt(20000));
```

### 결과 조회
- uniqueResult()
  - 조회 결과가 하나일 때 사용
  - 결과가 없으면 null 반환
  - 하나 이상이면 예외 발생
- singleResult()
  - uniqueResult와 같음
  - 결과가 하나 이상이면 첫 데이터 반환
- list()
  - 결과가 하나 이상일 때 사용
  - 없으면 빈 컬렉션 반환

### 페이징, 정렬
```java
query.from(item)
    // 정렬
    .orderBy(item.price.desc()), item.quantity.asc())
    // 페이징
    .offset(10).limit(20)
    .list(item);

// 페이징, restrict() + QueryModifiers(limit, offset)
QueryModifiers queryModifiers = new QueryModifiers(20L, 10L);
query.from(item)
    .restrict(queryModifiers)
    .list(item);

// 전체 데이터 수, listResults()
SearchResults<Item> result = query.from(item)
        .where(item.price.gt(10000))
        .offset(10).limit(20)
        .listResult(item);

// 검색된 전체 데이터 수
long total = result.getTotal();
// 조회된 데이터
List<Item> result = result.getResults();
```
### 그룹
```java
query.from(item)
    .groupBy(item.price)
    .having(item.price.gt(1000))
    .list(item);
```

### 조인
`join(조인 대상, 별칭으로 사용할 쿼리 타입)`

```java
QOrder order = QOrder.order;
QMember member = QMember.member;
QOrderItem orderItem = QOrderltem.orderItem;

// 기본 조인
query.from(order)
    .join(order.member, member)
    .leftjoin(order.orderIterns, orderitem)
    .list(order);

// on 사용
query.from(order)
    .leftjoin(order.orderIterns, orderitem)
    .on(orderItem.count.gt(2))
    .list(order);

// fetch 조인
query.from(order)
    .innerJoin(order.member, member).fetch()
    .leftjoin(order.orderIterns, orderitem).fetch()
    .list(order);

// 세타 조인
uery. from(order, member)
    .where(order.member.eq(member))
    .list(order);
```

### 서브 쿼리
```java
QItem item = QItem.item;
QItem itemSub = new QItem("itemSub");

// 한 건
query.from(item)
    .where(item.price.eq(
        new JPASubQuery().from(itemSub)
            .unique(itemSub.price.max())
    ))
    .list(item);

// 여러 건
query.from(item)
    .where(item.in(
        new JPASubQuery().from(itemSub)
            .where(item.name.eq(itemSub.name))
            .list(itemSub)
    ))
    .list(item);
```
### 프로젝션
**튜플**
- 프로젝션 대상이 여러 필드인 경우 Tuple 타입을 사용
```java
QItem item = QItem.item;
List<Tuple> result = query.from(item).list(item.name, item.price);

// 사용
// tuple.get(item.name);
// tuple.get(item.price);
```

**빈 생성**
```java
QItem item = QItem.item;

// setter
List<ItemDTO> result = query.from(item).list(
        Projections.bean(ItemDTO.class, item.name.as("username"), item.price));

// 필드 직접 접근(private 필드도 가능)
List<ItemDTO> result = query.from(item).list(
        Projections.fields(ItemDTO.class, item.name.as("username"), item.price));

// 생성자 사용
List<ItemDTO> result = query.from(item).list(
        Projections.constructor(ItemDTO.class, item.name, item.price));

```
**DISTINCT**
```java
query.distinct().from(item)
```
### 수정, 삭제
```java
QItem item = QItem.item;
// 수정
JPAUpdateClause updateClause = new JPAUpdateClause(em, item);
long count = updateClause.where(item, name.eq("JPA"))
        .set(item.price, item.price.add(100))
        .execute();
    
// 삭제
JPADeleteClause deleteClause = new JPADeleteClause(em, item);
long count = deleteClause.where(item, name.eq("JPA"))
        .execute();

```

### 동적 쿼리 
- BooleanBuilder 사용
```java
QItem item = QItem.item;
String text = "텍스트";
Integer price = 10000;
BooleanBuilder builder = new BooleanBuilder();

if (StringUtils.hasText(text)) {
    builder.and(item.name.contains(text));
}
if (param.getPrice() != null) {
    builder.and(item.price.gt(price));
}

List<Item> result = query.from(item)
    .where(builder)
    .list(item);
```

### 메서드 위임
```java
// 검색 조건 정의
public class ItemExpression {
    @QueryDelegate(Item.class)
    public static BooleanExpression isExpensive(QItem item,
                                                Integer price) {
        return item.price.gt(price);
    }
}

// 사용
query.from(item).where(item.isExpensive(30000)).list(item);
```
