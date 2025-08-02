## Spring Data JPA

인터페이스를 기반으로 자동으로 JPA 구현체를 만들어 줌

```java
// JPA
@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}

// Spring Data JPA
public interface MemberRepository extends JpaRepository<Member, Long> {
}
```

### 쿼리 메서드

**메서드 이름으로 쿼리 생성**

메서드 이름을 분석해서 JPQL을 생성하고 실행

```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
}
```

**메서드 이름으로 Named Query 호출**<br/>
(거의 안 씀)
```java
// NamedQuery 정의
@NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username = :username"
)
public class Member { }

// JPA
public List<Member> findByUsername(String username) {
    return em.createNamedQuery("Member.findByUsername", Member.class)
            .setParameter("username", username)
            .getResultList();
}

// Spring Data JPA
@Query(name = "Member.findByUsername") // 생략 가능
List<Member> findByUsername(@Param("username") String username);
```
스프링 데이터 JPA는 [Entity] + . + [메서드 명]으로 Named Query를 찾아서 실행<br/>
만약 해당하는 쿼리가 없으면 메서드 이름으로 쿼리 생성 전략을 사용<br/>

**@Query 애너테이션으로 리포지토리 인터페이스에 쿼리 정의**<br/>
(자주 사용)

```java
// 메서드에 JPQL 쿼리 작성
@Query("select m from Member m where m.username = :username")
List<Member> findUser(@Param("username") String username);
```
- 실행할 메서드에 정적 쿼리를 직접 작성하므로 이름 없는 Named 쿼리라고 할 수 있다
- JPA Named 쿼리처럼 어플리케이션 실행 시점에 문법 오류를 발견할 수 있다

```java
// DTO로 직접 조회
@Query("select new dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
List<MemberDto> findMemberDto();
```

**파라미터 바인딩**
```java
// select m from Member m where m.username = ?0 위치 기반 
// select m from Member m where m.username = :name 이름 기반
@Query("select m from Member m where m.username = :name")
Member findMembers(@Param("name") String username);

// 컬렉션 파라미터
@Query("select m from Member m where m.username in :names")
List<Member> findByNames(@Param("names") Collection<String> names);
```
- 이름 기반 사용 권장

**벌크성 수정 쿼리**
```java
@Modifying(clearAutomatically = true)
@Query("update Product p set p.price = p.price * 1.1 where p.stockAmount < :stockAmount")
int bulkPriceUpdate(@Param("stockAmount") String stockAmount);
```
- @Modifying 애너테이션 사용
- clearAutomatically 옵션
  - 쿼리 실행 후 영속성 컨텍스트를 초기화 할 때 사용
  - 기본값 false

**반환 타입**
```java
List<Member> findByUsername(String name); //컬렉션
Member findByUsername(String name); //단건
Optional<Member> findByUsername(String name); //단건 Optional
```

**페이징과 정렬**
- 페이징 정렬 파라미터
  - Sort: 정렬 기능
  - Pageable: 페이징 기능 (내부에 Sort 포함)
- 반환 타입
  - Page: 추가 count 쿼리 결과를 포함하는 페이징
  - Slice: 추가 count 쿼리 없이 다음 페이지만 확인 가능
    - 내부적으로 limit + 1 조회
  - List(자바 컬렉션): 추가 count 쿼리 없이 결과만 반환

```java
// 페이징 메서드 정의 예시
Page<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용
Slice<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용 X
List<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용 X
List<Member> findByUsername(String name, Sort sort);
```

```java
// page 정의 코드
Page<Member> findByUsername(String name, Pageable pageable);

// page 실행 코드
PageRequest pageRequest = 
        new PageRequest(0, 10, new Sort(Direction.DESC, "name"));

page<Member> result = 
        memberRepository.findByName("kim", pageRequest);

List<Member> members = result.getContent();
int totalPage = result.getTotalPages();
boolean hasNextPage = result.hasNextPage();
```

**fetch join**
```java
@Query("select m from Member m left join fetch m.team")
List<Member> findMemberFetchJoin();
```

JPA가 제공하는 엔티티 그래프 기능을 사용하여 JPQL 없이 페치 조인을 사용가능
```java
//공통 메서드 오버라이드
@Override
@EntityGraph(attributePaths = {"team"})
List<Member> findAll();

//JPQL + 엔티티 그래프
@EntityGraph(attributePaths = {"team"})
@Query("select m from Member m")
List<Member> findMemberEntityGraph();

//메서드 이름으로 쿼리에서 특히 편리하다.
@EntityGraph(attributePaths = {"team"})
List<Member> findEntityGraphByUsername(@Param("username") String username);
```

### 사용자 정의 리포지토리
리포지토리 인터페이스 이름 + Impl
```java
// 사용자 정의 인터페이스
public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}

// 구현 클래스
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

  private final EntityManager em;

  @Override
  public List<Member> findMemberCustom() {
    return em.createQuery("select m from Member m")
            .getResultList();
  }
}

// 사용자 정의 인터페이스 상속
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom { }
```
TODO: 지원 방식 추가

### Auditing
**설정**
- @EnableJpaAuditing → 스프링 부트 클래스에 적용
- @EntityListeners(AuditingEntityListener.class) → 엔티티에 적용

**적용**
```java
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    
    @LastModifiedBy
    private String lastModifiedBy;
}
```

