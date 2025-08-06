## Querydsl
```java
JPAQueryFactory queryFactory = new JPAQueryFactory(em);
QMember m = new QMember("m");

Member findMember = queryFactory
        .select(m)
        .from(m)
        .where(m.username.eq("member1"))
        .fetchOne();
```
- EntityManager로 JPAQueryFactory 생성
- Querydsl은 JPQL 빌더
- JPQL: 문자 (실행 시점 오류) vs Querydsl: 코드 (컴파일 시점 오류)
- JPQL: 파라미터 바인딩 직접 vs Querydsl: 파라미터 바인딩 자동 처리

### 기본 Q-Type
```java
QMember member = new QMember("m"); // 별칭 직접 지정
QMember member = QMember.member; // 기본 인스턴스 사용

// 기본 인스턴스 사용 코드
import static study.querydsl.entity.QMember.*;

@Test
public void startQuerydsl3() {
    Member findMember = queryFactory
            .select(member)
            .from(member)
            .where(member.username.eq("member1"))
            .fetchOne();

    assertThat(findMember.getUsername()).isEqualTo("member1");
}
```

### 검색
**기본**
```java
@Test
public void search() {
    Member findMember = queryFactory
        .selectFrom(member)
        .where(member.username.eq("member1")
        .and(member.age.eq(10)))
        .fetchOne();
    
    assertThat(findMember.getUsername()).isEqualTo("member1");
}
```
- .and(), .or()를 메서드 체인으로 연결할 수 있다

**검색 조건**
```java
.eq("member1") //  = 'member1'
.ne("member1") // != 'member1'
.eq("member1").not() //  != 'member1'

.isNotNull() // is not null

.in(10, 20) //  in (10,20)
.notIn(10, 20) //  not in (10, 20)
.between(10,30) // between 10, 30

.goe(30) //  >= 30
.gt(30) //  > 30
.loe(30) //  <= 30
.lt(30) //  < 30

.like("member%") // like 검색
.contains("member") // like ‘%member%’ 검색
.startsWith("member") //like ‘member%’ 검색
```
**AND 조건 파라미터**
```java
@Test
public void searchAndParam() {
    List<Member> result1 = queryFactory
        .selectFrom(member)
        .where(
                member.username.eq("member1"),
                member.age.eq(10))
        .fetch();
    
    assertThat(result1.size()).isEqualTo(1);
}
```
- where()에 파라미터로 검색 조건을 추가하면 AND 조건이 추가됨
- null 값은 무시

### 결과 조회
- fetch(): 리스트 조회, 데이터 없으면 빈 리스트 반환
- fetchOne(): 단건 조회
  - 결과가 없으면: null
  - 결과가 둘 이상이면: 에러
- fetchFirst(): limit(1).fetchOne()와 같다.
- fetchResults(): 페이징 정보 포함, total count 쿼리 추가 실행
- fetchCount(): count 쿼리로 변경해서 count 수 조회
```java
//List
List<Member> fetch = queryFactory
        .selectFrom(member)
        .fetch();
        
//단건
Member findMember1 = queryFactory
        .selectFrom(member)
        .fetchOne();
        
//처음 한 건 조회
Member findMember2 = queryFactory
        .selectFrom(member)
        .fetchFirst();
        
//페이징에서 사용
QueryResults<Member> results = queryFactory
        .selectFrom(member)
        .fetchResults();
        
//count 쿼리로 변경
long count = queryFactory
        .selectFrom(member)
        .fetchCount();
```
### 정렬
- desc(), asc(): 일반 정렬
- nullsLast(), nullsFirst(): null 데이터 순서 부여
```java
/**
 * 회원 정렬 순서
 * 1. 회원 나이 내림차순(desc)
 * 2. 회원 이름 올림차순(asc)
 * 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
 */
@Test
public void sort() {
    em.persist(new Member(null, 100));
    em.persist(new Member("member5", 100));
    em.persist(new Member("member6", 100));
    
    List<Member> result = queryFactory
            .selectFrom(member)
            .where(member.age.eq(100))
            .orderBy(member.age.desc(), member.username.asc().nullsLast())
            .fetch();
            
    Member member5 = result.get(0);
    Member member6 = result.get(1);
    Member memberNull = result.get(2);
    assertThat(member5.getUsername()).isEqualTo("member5");
    assertThat(member6.getUsername()).isEqualTo("member6");
    assertThat(memberNull.getUsername()).isNull();
}
```