package jpa.querydsl.basic;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import jpa.querydsl.dto.MemberDto;
import jpa.querydsl.dto.QMemberDto;
import jpa.querydsl.dto.UserDto;
import jpa.querydsl.entity.QMember;
import org.junit.jupiter.api.Test;

import java.util.List;

import static jpa.querydsl.entity.QMember.member;
import static jpa.querydsl.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Projection 테스트
 * */
public class ProjectionTest extends BasicTestInit{
    @Test
    void projection() {
        // 프로젝션 대상이 하나인 경우 명확한 타입 지정 가능
        List<String> result1 = queryFactory
                .select(member.username)
                .from(member)
                .fetch();

        /**
         * 프로젝션 대상이 두 개 이상인 경우 Tuple 또는 DTO 사용
         * Tuple은 repository 계층 정도에서만 사용
         * 그 외에는 DTO 권장
         * */
        List<Tuple> result2 = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        /**
         * DTO 사용 - JPA
         * - new 명령어를 사용해야 함
         * - DTO의 package명을 다 적어줘야 함
         * - 생성자 방식만 지원
         * */
        List<MemberDto> result3 = em
                .createQuery(
                        "select new jpa.querydsl.dto.MemberDto(m.username, m.age)" +
                                "from Member m", MemberDto.class
                ).getResultList();

        /**
         * 프로퍼티 접근 - Setter
         * */
        List<MemberDto> result4 = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        /**
         * 필드 직접 접근
         * */
        List<MemberDto> result5 = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        /**
         * 필드 직접 접근 - 필드 이름이 다른 경우
         * - ExpressionUtils.as(source, alias): 필드, 서브 쿼리에 별칭 사용
         * - .as(): 필드에 별칭 적용
         * */
        QMember memberSub = new QMember("memberSub");

        List<UserDto> fetch = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(memberSub.age.max())
                                        .from(memberSub), "age"
                                )
                        )
                ).from(member)
                .fetch();

        // 생성자 사용
        List<MemberDto> result6 = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        // @QueryProjection 사용
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age))
                .from(member)
                .fetch();

    }

}
