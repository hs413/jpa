package jpa.datajpa.repository;

import jpa.datajpa.dto.MemberDto;
import jpa.datajpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
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


    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new jpa.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    /**
     * 컬렉션 파라미터 바인딩
     * - 많이 사용하는 기능
     * */
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

//    Page<Member> findByAge(int age, Pageable pageable);
    /**
     * Slice 사용 paging
     * - 전체 데이터, 페이지 번호 확인 X
     * - 무한 스크롤, 자동으로 다음 게시물을 보여주는 방식 등에서 사용
     * - count 쿼리 발생 X
     * - limit + 1을 조회
     * */
//    Slice<Member> findByAge(int age, Pageable pageable);

    /**
     * count 쿼리 분리
     * - 실무에서 많이 사용
     * */
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);


    /**
     * 벌크성 수정
     * - 벌크 연산은 영속성 컨텍스트를 무시하고 db에 바로 반영
     * - 벌크 연산 이후에는 영속성 컨텍스트를 초기화 해야 한다.
     * - clearAutomatically = true 옵션 추가 시 flush + clear 작업을 한다
     * ** 권장사항
     * - 영속성 컨텍스트에 엔티티가 없는 상태에서 벌크 연산 실행을 권장
     * */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
}
