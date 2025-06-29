package jpa.querydsl.basic;

import com.querydsl.jpa.JPAExpressions;
import jpa.querydsl.entity.Member;
import jpa.querydsl.entity.QMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static jpa.querydsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

public class SubQueryTest extends BasicTestInit{
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
}
