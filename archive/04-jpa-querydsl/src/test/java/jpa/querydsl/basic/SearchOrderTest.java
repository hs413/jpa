package jpa.querydsl.basic;

import jpa.querydsl.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static jpa.querydsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 검색 및 정렬 테스트
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
public class SearchOrderTest extends BasicTestInit{
    @Test
    @DisplayName("기본 검색")
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
}
