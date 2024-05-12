package jpa.datajpa.repository;

import jpa.datajpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 스프링 데이터 JPA 사용
 * */
public interface MemberRepository extends JpaRepository<Member, Long> {
    /**
    * 쿼리 메서드 기능
    * - 메서드 이름을 분석해서 JPQL을 생성하고 실행
    * */
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**
     * NamedQuery (사용 안함)
     * - @Query 생략가능
     * */
//    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    /** ** 중요 **
     * @Query 리포지토리 메소드에 쿼리 정의
     * - 실행할 메서드에 정적 쿼리를 직접 작성 -> NamedQuery라고 할 수 있음
     * - NamedQuery처럼 애플리케이션 실행 시점에 문법 오류를 발견할 수 있음 -> 장점
     * - 메소드 이름으로 쿼리를 생성하면 파라미터 많아질수록 메서드 이름이 매우 지저분해짐 -> 해당 기능을 자주 사용
     * */
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);
}
