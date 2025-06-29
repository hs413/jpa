package jpa.querydsl.basic;

import com.querydsl.core.Tuple;
import jpa.querydsl.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static jpa.querydsl.entity.QMember.member;
import static jpa.querydsl.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

public class JoinTest extends BasicTestInit{
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
}
