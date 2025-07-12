# 연관관계 매핑1

## 단방향 연관관계
![단방향](images/img.png)

### 객체 연관관계
- 회원 객체는 team 필드로 팀 객체와 연관관계를 맺음
- 회원 객체와 팀 객체는 단방향 관계 
  - 회원 객체는 team 필드를 통해서 팀 객체를 알 수 있음
  - 팀 객체는 회원 객체로 접근하는 필드가 없음 -> 팀 객체에서 회원 객체를 알 수 없음

### 테이블 연관관계
- 회원 테이블은 TEAM_ID 외래 키로 팀 테이블과 연관관계를 맺음
- 회원 테이블과 팀 테이블은 양방향 관계
  - TEAM_ID를 통해 회원과 팀, 팀과 회원으로 조인 가능

### 객체와 테이블 차이
**객체**
- 참조(객체)를 통해 연관관계를 맺음 
- 연관관계는 항상 단방향
  - 양방향 관계를 원한다면 두 객체 모두 참조 필드를 추가해야 함
  - 단방향 관계를 2개 설정하는 것과 같음
  ```java
  // 단방향
  class A {
    B b;
  }
  class B {}
  
  // 양방향
  class A {
    B b;
  }
  class B {
    A a;
  }
  ```
예시)
```java
public class Member {
    private String id;
    private String username;
    
    private Team team;
    
    public void setTeam(Team team) {
        this.team = team;
    }
}

public class Team {
    private String id;
    private String name;
}

// 동작 코드
private void main() {
    Member member1 = new Member("member1", "회원1");
    Member member2 = new Member("member2", "회원2");

    Team team1 = new Team("team1", "팀1");

    member1.setTeam(team1);
    member2.setTeam(team1);
    
    // 객체 그래프탐색 - 참조를 사용해서 연관관계를 탐색하는 것
    Team findTeam = member1.getTeam();
}
```
**테이블**
- 외래 키 하나로 양방향 관계를 가지며 연관관계를 탐색

```sql
SELECT *
FROM MEMBER M
JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID

SELECT *
FROM TEAM T
JOIN MEMBER M ON T.TEAM_ID = M.TEAM_ID
```

### 객체 관계 매핑
![단방향](images/img_1.png)
```java
@Entity
public class Member {
    @Id @GeneratedValue
    @Column(name="MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;
}

@Entity
public class Team {
    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;
}
```
**@JoinColumn**
- name
  - 매핑할 외래 키 이름
  - 기본 값: 필드명 + _ + 참조 테이블의 기본 키 컬럼명
- referencedColumnName
  - 외래 키가 참조하는 대상 테이블의 컬럼명
  - 기본 값: 참조하는 테이블의 기본 키 컬럼명 
- foreignKey(DDL)
  - 외래 키 제약조건을 지정
  - 테이블 생성 시에만 사용

**@ManyToOne**
- optional
  - false로 설정 시 연관된 엔티티가 항상 있어야 함
  - 기본 값 true
- fetch
  - 글로벌 페치 전략 설정
  - 기본 값
    - ManyToOne = FetchType.EAGER
    - OneToMany = FetchType.LAZY
- cascade
  - 영속성 전이 기능을 사용
- targetEntity
  - 연관된 엔티티의 타입 정보 설정
  - 거의 사용하지 않음

[//]: # ()
[//]: # (## 연관관계 사용)

[//]: # ()
[//]: # (## 양방향 연관관계)

[//]: # ()
[//]: # (## 연관관계 주인)

[//]: # ()
[//]: # (## 양방향 연관관계 저장)

[//]: # ()
[//]: # (## 양방향 연관관계 주의점)
