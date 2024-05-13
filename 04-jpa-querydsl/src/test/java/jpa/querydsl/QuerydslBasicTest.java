package jpa.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;
import jpa.querydsl.entity.Member;
import jpa.querydsl.entity.QMember;
import jpa.querydsl.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

import static jpa.querydsl.entity.QMember.*;
import static jpa.querydsl.entity.QTeam.*;

@SpringBootTest
@Transactional
class QuerydslBasicTest  {
    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    @PersistenceUnit
    EntityManagerFactory emf;

    @BeforeEach
    public void testEntity() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1" , 10, teamA);
        Member member2 = new Member("member2" , 20, teamA);
        Member member3 = new Member("member3" , 30, teamB);
        Member member4 = new Member("member4" , 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL() {
        Member findMember = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember m = new QMember("m");

        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * 기본 인스턴스 사용
     * - import static 사용
     * - ex) import static study.querydsl.entity.QMember.*;
     * */
    @Test
    public void startQuerydsl2() {
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * 조인이 필요한 경우 alias 를 적어줘야 함
     * */
    @Test
    public void startQuerydsl3() {
        QMember m1 = new QMember("m1");

        Member findMember = queryFactory
                .select(m1)
                .from(m1)
                .where(m1.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * 기본 검색
     *  member.username.eq("member1") // username = 'member1'
     *  member.username.ne("member1") //username != 'member1'
     *  member.username.eq("member1").not() // username != 'member1'
     * 	member.username.isNotNull() //이름이 is not null
     *  member.age.in(10, 20) // age in (10,20)
     *  member.age.notIn(10, 20) // age not in (10, 20)
     *  member.age.between(10,30) //between 10, 30
     *  member.age.goe(30) // age >= 30
     *  member.age.gt(30) // age > 30
     *  member.age.loe(30) // age <= 30
     *  member.age.lt(30) // age < 30
     * 	member.username.like("member%") //like 검색
     * 	member.username.contains("member") // like ‘%member%’
     * 	member.username.startsWith("member") //like ‘member%’
    * */
    @Test
    public void search() {
        Member findMember = queryFactory
                // select, from -> selectFrom으로 합칠 수 있다.
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * 검색 - AND 조건을 파라미터로 처리
     * - where()절에 조건을 추가하면 AND 조건이 자동으로 추가
     * - null 값은 무시 → 메서드 추출을 활용해서 동적 쿼리를 깔끔해서 만들 수 있다
     * */
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


    /**
     * 정렬
     * nullsLast(), nullsFirst() : null 데이터 순서 부여
     * */
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

    /**
     * 페이징 - 조회 수 제한
     * */
    @Test
    public void paging1() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();

        assertThat(result.size()).isEqualTo(2);

        // 전체 조회 수가 필요한 경우 count query를 별도로 작성 하는 것을 추천
        // fetchResults는 deprecated
    }

    /**
     * 집합 함수
     */
    @Test
    public void aggregation() {
        List<Tuple> result = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    /**
     * groupBy, having
     */
    @Test
    public void group() throws Exception {
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .having(member.age.avg().gt(20))
                .fetch();

//        Tuple teamA = result.get(0);
        Tuple teamB = result.get(0);

//        assertThat(teamA.get(team.name)).isEqualTo("TeamA");
//        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("TeamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    /**
     * 조인 - 기본 조인
     * */
    @Test
    public void join() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("TeamA"))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");
    }

    /**
     * 조인 - 세타 조인 (연관관계가 없는 필드로 조인)
     * */
    @Test
    public void theta_join() throws Exception {
        em.persist(new Member("TeamA"));
        em.persist(new Member("TeamB"));
        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();
        assertThat(result)
                .extracting("username")
                .containsExactly("TeamA", "TeamB");
    }

    /**
     * 조인 - on
     * - 내부조인이면 where 절로 해결, 외부조인이 필요한 경우에만 사용
     * */
    @Test
    @DisplayName("회원과 팀을 조인하면서, 팀 이름이 TeamA인 팀만 조인 하고 회원은 모두 조회한다.")
    public void join_on_filtering() throws Exception {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    @DisplayName("연관관계가 없는 엔터티 외부 조인으로 회원의 이름과 팀 이름이 같은 회원을 조인한다.")
    void joinOnNoRelation(){
        //given
        em.persist(new Member("TeamA"));
        em.persist(new Member("TeamB"));
        em.persist(new Member("TeamC"));
        //when
        List<Tuple> result = queryFactory
                .select(member,team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println(tuple);
        }
    }

    /**
     * 조인 - 페치 조인
     * */
    @Test
    @DisplayName("패치조인 미사용")
    void fetchJoinNo(){
        // given
        em.flush();
        em.clear();

        // when
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        // then
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 미적용").isFalse();
    }

    @Test
    @DisplayName("패치조인 사용")
    public void fetchJoinUse() {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 적용").isTrue();
    }


    /**
     * 서브 쿼리
     * - JPA JPQL, Qeurydsl은 from 절의 서브쿼리(인라인 뷰)를 지원 안함
     *    - 서브쿼리를 join으로 변경 → 불가능한 경우도 있음
     *    - 쿼리를 2번 분리해서 실행
     *    - nativeSQL을 사용한다
     * */
    @Test
    @DisplayName("eq : 나이가 가장 많은 회원 조회")
    public void subQuery() throws Exception {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(40);
    }

    @Test
    @DisplayName("eoq : 나이가 평균 나이 이상인 회원")
    public void subQueryGoe() throws Exception {

        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(30,40);
    }

    @Test
    @DisplayName("in : 나이가 10인 회원")
    public void subQueryIn() throws Exception {

        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        JPAExpressions
                                .select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(20, 30, 40);
    }

    /**
     * Case
     * - select, 조건절( where ), order by에서 사용
     * */
    @Test
    void caseTest(){
        List<String> baseCase = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        // CaseBuilder()
        List<String> complexCase = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타")
                )
                .from(member)
                .fetch();
    }

    /**
     * 상수, 문자 더하기
     * */
    @Test
    void addConstant(){
        // 상수 - Expressions.constanc
        Tuple constanc = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetchFirst();

        // 문자 더하기 concat
        // 문자가 아닌 타입은 stringValue()로 문자로 변환 가능
        String concat = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();
    }

}