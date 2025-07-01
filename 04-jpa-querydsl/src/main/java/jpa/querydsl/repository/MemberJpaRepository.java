package jpa.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jpa.querydsl.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static jpa.querydsl.entity.QMember.*;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    /* queryFactory 초기화 방법 - 기본
    public MemberJpaRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    } */

    /* queryFactory 초기화 방법 - 빈으로 미리 등록 -> RequiredArgs 에너테이션 가능
    public MemberJpaRepository(EntityManager em, JPAQueryFactory queryFactory) {
        this.em = em;
        this.queryFactory = queryFactory;
    } */

    public void save(Member member) {
        em.persist(member);
    }

    public Optional<Member> findById(Long id) {
        Member findMember = em.find(Member.class, id);
        return Optional.ofNullable(findMember);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m ", Member.class)
                .getResultList();
    }

    public List<Member> findByUsername(String username) {
        return em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<Member> findAll_Querydsl() {
        return queryFactory
                .selectFrom(member).fetch();
    }

    public List<Member> findByUsername_Querydsl(String username) {
        return queryFactory
                .selectFrom(member)
                .where(member.username.eq(username))
                .fetch();
    }
}
