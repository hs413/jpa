# JPA (Java Persistence API)

## JPA
- 자바 0RM 기술에 대한 API 표준 명세(인터페이스를 모아둔 것)
  - **자바 진영 ORM 기술 표준**
- 관계형 데이터베이스의 데이터를 관리하고 지속시킴
- ORM을 통해 자바 객체를 관계형 데이터베이스 테이블에 매핑
  - 데이터베이스 작업을 보다 간편하게 처리할 수 있게 해줌
- 객체 중심의 개발 -> 생산성과 유지보수, 테스트 작성이 좋음

- JPA를 사용하려면 JPA를 구현한 ORM 프레임워크를 선택해야 함
  - 하이버네이트가 대표적
- 특정 구현 기술에 대한 의존도를 줄일 수 있고 다른 구현 기술로 손쉽게 이동할 수 있음

### ORM (Object-Relational Mapping)
- 객체와 관계형 데이터베이스를 매핑한다는 뜻
- 객체 지향 패러다임 유지
  - 객체와 테이블을 매핑해서 패러다임의 불일치 문제를 해결
- 생산성 향상
  - SQL을 직접 작성하지 않고 데이터베이스 작업을 수행할 수 있어 개발 속도 향상
  - 코드의 가독성, 유지보수성 향상
- 데이터베이스 독립성
  - 다양한 데이터베이스를 지원, 데이터베이스 변경 시 코드 수정 최소화
- 자동화된 데이터베이스 관리
  - 스키마 생성 및 업데이트, 데이터베이스 연결 관리 등을 자동으로 처리
  - 개발자가 비즈니스 로직에 집중할 수 있게 도와줌
- 대표적인 ORM 프레임 워크로 하이버네이트가 있음

## SQL 사용의 문제점
- 반복되는 코드가 많아짐
- 강한 SQL 의존성
  - SQL과 엔티티가 강한 의존관계를 가짐 (필드 추가 시 DAO와 SQL의 대부분을 변경해야 함)
  - 계층 분할이 어려움

## 패러다임 불일치
객체와 관계형 DB가 지향하는 목적이 달라 기능과 표현 방법이 다름 -> 패러다임 불일치

### 상속
- 객체는 상속 기능이 있지만 테이블은 없음
- DB 모델링에서 슈퍼타입 서브타입 관계를 사용하여 상속과 유사한 형태로 테이블 설계는 가능함

```java
abstract class Item {
    Long id;
    String name;
    int price;
}
class Album extends Item {
    String artist;
}
class Movie extends Item {
    String director;
    String actor;
}
class Book extends Item {
    String author;
    String isbn;
}
```
위와 같은 객체가 있을 때 Album 객체를 저장하려는 경우
- SQL 
```mysql
-- SQL 문을 두 번 사용해야 함 
INSERT INTO ITEM ... ;
INSERT INTO ALBUM ... ;
```
  
- 객체
```java
// 다형성을 활용해 컬렉션에 저장
List<Item> list = new ArrayList<>();
list.add(album);
list.add(movie);
Album album = list.get(albumld);
```

- JPA
```java
// persistO 메서드를 사용해서 객체 저장
jpa.persist(album);
```

### 연관관계
- 객체는 참조를 사용해서 다른 객체와 연관관계를 가지고 참조에 접근
  ```java
  class Member {
    Team team;
    Team getTeam() {
        return team;
    }
  }
  class Team {}
  
  member.getTeam(); //member -> team 접근
  ```
  - 객체는 참조가 있는 방향으로만 조회 가능

- 테이블은 외래 키를 사용해서 조인을 사용해서 연관 테이블을 조회
  ```sql
  SELECT M.*, T.*
  FROM MEMBER M
  JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID
  ```
  - 테이블은 양방향으로 연관 테이블 조회 가능


- 객체를 테이블에 맞춘 모델링
  ```java
  class Member {
    String id;
    Long teamld;
    String username;
    }
  class Team {
    Long id;
    String name;
  }
  ```
  - 참조를 통해 연관 객체를 찾을 수 없음


- 객체지향 모델링
  ```java
  class Member {
    String id;
    Team team;
    String username;
    Team getTeam() {
      return team;
    }
  }
  class Team {
    Long id;
    String name;
  }
  ```
  - 객체는 필드로, 테이블은 외래 키로 연관관계를 맺기 때문에 저장이나 조회가 복잡

### 객체 그래프 탐색
- 객체
  ```java
  member.getOrder().getOrderItem()
  ```
  - 객체 그래프 탐색이 자유로움
- SQL
  ```sql
  SELECT M.*, T.*
  FROM MEMBER M
  JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID
  ```
  - JOIN된 객체만 탐색 가능

### 비교 
- DB는 기본 키 값으로 row를 구분

- 객체는 동일성, 동등성 비교 방법을 사용
  - 동일성: ==
  - 동등성: equals

```java
// 기존 객체 비교
String memberld = "100";
Member member1 = memberDAO.getMember(memberld);
Member member2 = memberDAO.getMember(memberld);

// 다름
member1 == member2; 

// 컬렉션
Member member1 = list.get(0);
Member member2 = list.get(0);

// 같음
member1 == member2;

// JPA
String memberId = "100”;
Member memberl = jpa.find(Member.class, memberld) ;
Member member2 = jpa.find (Member. class, memberld);

// 같음
memberl == member2;
```
- JPA는 같은 트랜잭션안의 객체가 같음을 보장

