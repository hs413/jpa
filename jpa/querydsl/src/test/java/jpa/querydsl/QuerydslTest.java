package jpa.querydsl;

import static org.assertj.core.api.Assertions.*;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import jpa.querydsl.entity.Member;
import jpa.querydsl.entity.QMember;
import jpa.querydsl.entity.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static jpa.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
class QuerydslTest {
    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("Team A");
        Team teamB = new Team("Team B");
        em.persist(teamA);
        em.persist(teamB);

        Member Member1 = new Member("member1", 10, teamA);
        Member Member2 = new Member("member2", 20, teamA);
        Member Member3 = new Member("member3", 30, teamA);
        Member Member4 = new Member("member4", 40, teamA);

        em.persist(Member1);
        em.persist(Member2);
        em.persist(Member3);
        em.persist(Member4);
    }

    @Test
    public void JPQL() {
        String qlString =
                "select m from Member m " +
                "where m.username = :username";

        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void querydsl() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember m = new QMember("m");

        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    // JPAQueryFactory 필드 사용
    @Test
    public void querydsl2() {
        QMember m = new QMember("m");

        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    // Q-Type 기본 인스턴스 사용
    @Test
    public void querydsl3() {
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    // 기본 검색
    @Test
    public void search() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10))
                .fetch();

        assertThat(result).hasSize(1);
    }


}