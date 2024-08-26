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