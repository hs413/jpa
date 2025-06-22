package jpa.datajpa.repository;

import jpa.datajpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 스프링 데이터 JPA 사용
 * */
public interface MemberRepository extends JpaRepository<Member, Long> {
}
