package jpa.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jpa.querydsl.entity.Hello;
import jpa.querydsl.entity.QHello;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class ApplicationTest {
    @Autowired
    EntityManager em;

    @Test
    void contextLoad() {
        Hello hello = new Hello();
        em.persist(hello);

        JPAQueryFactory query = new JPAQueryFactory(em);
        QHello qHello = QHello.hello;

        Hello result = query.selectFrom(qHello).fetchOne();

        assertThat(result).isEqualTo(hello);
    }
}