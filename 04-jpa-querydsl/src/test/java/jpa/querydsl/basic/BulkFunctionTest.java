package jpa.querydsl.basic;


import com.querydsl.core.types.dsl.Expressions;
import jpa.querydsl.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static jpa.querydsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 벌크 쿼리 및 sql function 테스트
 * */
public class BulkFunctionTest extends BasicTestInit{
    @Test
    @DisplayName("수정 테스트")
    void bulkUpdate(){
        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

        assertThat(count).isEqualTo(2);

        // 벌크 연산 후 조회 하려면 영속성 컨텍스트를 초기화 해줘야 함
        em.flush();
        em.clear();

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.username.eq("비회원"))
                .fetch();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("1 더하기 테스트")
    void bulkAdd(){
        queryFactory
                .update(member)
                .set(member.age, member.age.add(1))
                .execute();

        em.flush();
        em.clear();

        Member result = queryFactory
                .selectFrom(member)
                .fetchFirst();

        assertThat(result.getAge()).isEqualTo(11);
    }

    @Test
    @DisplayName("삭제 테스트")
    void bulkDelete(){
        long count = queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();

        assertThat(count).isEqualTo(3);
    }

    /**
     * SQL Function 호출 테스트
     * - Dialect에 등록된 내용만 호출
     * */
    @Test
    @DisplayName("SQL Function 호출")
    public void testSQLFunctionCall() {
        String result = queryFactory
                .select(Expressions.stringTemplate("function('replace', {0}, {1}, {2})", member.username, "member", "M"))
                .from(member)
                .fetchFirst();

        System.out.println(result);
    }
}
