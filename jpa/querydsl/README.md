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