package jpa.querydsl.basic;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;
import jpa.querydsl.entity.Member;
import jpa.querydsl.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 테스트를 위한 기본 설정 (상속용)
 * */
@SpringBootTest
@Transactional
public class BasicTestInit {
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
}
