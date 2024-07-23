## JPQL
- 엔티티 객체를 조회하는 객체지향 쿼리
- SQL과 비슷하고 ANSI 표준 SQL이 제공하는 기능을 유사하게 지원
- SQL을 추상화해서 특정 데이터베이스에 의존하지 않는다

### 기본 문법
```sql
SELECT m FROM Member AS m where m.username = ''
```
- 엔티티와 속성은 대소문자 구분
  - `JPQL` 키워드는 대소문자 구분하지 않음 (`SELECT`, `FROM`, `where`)
- 테이블 이름이 아닌 엔티티 이름을 사용
- 별칭은 필수, `as`는 생략 가능

**TypeQuery, Query**
- `TypeQuery`: 반환 타입을 명확하게 지정할 수 있으면 사용
- `Query`: 반환 타입을 명확하게 지정할 수 없으면 사용
- createQuery() 두 번째 파라미터에 반환할 타입을 지정하면 TypeQuery를, 지정하지 않으면 Query를 반환
```java
// TypeQuery 
// 반환 타입이 명확함
TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
TypedQuery<String> query1 = em.createQuery("select m.username from Member m", String.class);

// Query
// 반환 타입이 명확하지 않음
Query query2 = em.createQuery("select m.username, m.age from Member m");
```
**결과 조회**
- `query.getResultList()` : 결과가 하나 이상일 때
  - 리스트 반환, 결과가 없으면 빈 리스트 반환
- `query.getSingleResult()` : 결과가 하나 일 때
  - 단일 객체 반환 (값이 보장)
  - 결과가 없으면 : `javax.persistence.NoResultException`
  - 둘 이상이면 : `javax.persistence.NonUniqueResultException`

### 파라미터 바인딩

- JPQL은 이름 기준 파라미터 바인딩을 지원
  - 파라미터를 이름으로 구분하는 방법

**이름 기준 바인딩**
```sql
SELECT m FROM Member m where m.username=:username
query.setParameter("username", usernameParam);
```

**위치 기준 바인딩**

```sql
SELECT m FROM Member m where m.username=?1
query.setParameter(1, usernameParam);
```
- 위치 기준은 사용하지 않는 것을 권장
  - 중간에 파라미터가 추가되면 오류 발생

### 프로젝션
- select 절에 조회할 대상을 지정하는 것

**프로젝션 대상**
- 엔티티
  - SELECT **m** FROM Member m
  - SELECT **m.team** FROM Member m
- 임베디드 타입
  - 임베디드 타입은 조회의 시작점이 될 수 없음
    ```sql
    -- 잘못된 쿼리
    SELECT a FROM Address a;
      
    -- 올바른 쿼리 - 시작점은 상위 엔티티
    SELECT m.address FROM Member m 
    ```    
- 스칼라 타입
  - 숫자. 문자, 날짜와 같은 기본 데이터 타입들을 스칼라 타입이라고 한다
    ```sql
    SELECT m.username, m.age FROM Member m
    ```
  - `DISTINCT`를 사용하여 중복을 제거할 수 있다

**여러 값 조회**

- Query 타입으로 조회
    ```java
        List resultList = em.createQuery("select m.username, m.age from Member m")
            .getResultList();
    									 
        Object o = resultList.get(0);
        Object[] result = (Object[]) o;
    ```
- Object[] 타입으로 조회
    ```java
        List<Object[]> resultList = em.createQuery("select m.username, m.age from Member m")
            .getResultList();
                
    ```
- new 명령어로 조회
    ```java
    public class MemberDTO {
    
        private String username;
        private int age;
    
        public MemberDTO(String username, int age) {
            this.username = username;
            this.age = age;
        }
    }
    // 조회
    {
        List<MemberDTO> resultList = em
            .createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
            .getResultList();
    }
    ```
- 단순 값을 DTO로 바로 조회
- 패키지 명을 포함한 전체 클래스명 입력
- 순서와 타입이 일치하는 생성자 필요
