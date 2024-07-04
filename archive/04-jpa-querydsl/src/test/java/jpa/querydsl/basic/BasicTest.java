package jpa.querydsl.basic;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpa.querydsl.entity.Member;
import jpa.querydsl.entity.QMember;
import org.junit.jupiter.api.Test;

import java.util.List;

import static jpa.querydsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 기본 테스트
 *
 * */
public class BasicTest extends BasicTestInit {
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
