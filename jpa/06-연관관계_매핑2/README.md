# 연관관계 매핑2

## 다대일 [N:1]
- 외래 키(연관관계 주인)는 항상 N에 있다
- 가장 많이 사용됨

### 다대일 단방향 [N:1]
```java
@Entity
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;
    
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;
}

@Entity
public class Team {
    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;
}

```
### 다대일 양방향 [N:1, 1:N]
```java
@Entity
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;
    
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    public void setTeam(Team team) {
        this.team = team;
        
        if (!team.getMembers().contains(this)) {
            team.getMembers().add(this);
        }
    }
}

@Entity
public class Team {
    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<Member>();
  
    public void addMember(Member member) {
        this.members.add(member);
        
        if (member.getTeam() != this) {
            member.setTeam(this);
        }
    }
}

```
** 양방향 **
- 외래 키가 있는 쪽이 연관관계 주인
- 항상 서로를 참조해야 함

## 일대다 [1:N]
- 엔티티를 하나 이상 참조할 수 있어 컬렉션(List, Set, Map) 중 하나 사용함

### 일대다 단방향 [1:N]
- 보통 엔티티 객체는 매핑한 테이블의 외래 키를 관리하지만 일대다 단방향은 반대 테이블의 외래 키를 관리한다
  - 외래 키는 항상 N에 해당하는 테이블에 있으나 해당 매핑은 N에 해당하는 객체에 참조 필드가 없음
  - 1에 해당하는 객체에서 N을 참조하는 컬렉션을 사용해 외래 키를 관리

```java
@Entity
public class Team {
    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;
  
    @OneToMany
    @JoinColumn(name = "TEAM_ID") // MEMBER 테이블의 TEAM_ID (FK)
    private List<Member> members = new ArrayList<Member>();
}
@Entity
public class Member {
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;
}
```
- 일대다 단방향 관계 매핑 시 @JoinColumn 명시 필수
  - 명시하지 않으면 조인 테이블 전략을 기본으로 사용(7장에서 설명)
- 매핑한 객체가 관리하는 외래 키가 다른 테이블에 있음
  - 연관관계 처리를 위한 UPDATE SQL 추가 실행 필요
- 일대다 단방향보다 다대일 양방향 사용 권장


### 일대다 양방향 [1:N, N:1]
- 존재하지 않음, 대신 다대일 양방향 매핑 사용


