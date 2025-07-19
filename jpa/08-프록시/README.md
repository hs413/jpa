# 프록시
- 엔터티를 조회할 때 연관된 엔티티들이 항상 사용되는 것은 아님

```java
// 회원과 팀 정보 출력
public void printUserAndTeam(String memberId) {
    Member member = em.find(Member.class, memberId);
    Team team = member.getTeam();
    System.out.println("회원 이름 : " + member.getUsername());
    System.out.println("소속팀 : " + team.getName());
}

// 회원만 출력
public String printUser(String memberId) {
    Member member = em.find(Member.class, memberId);
    System.out.println("회원 이름 : " + member.getUsername());
}
```
**printUser()**
- 메서드는 회원 정보만 출력
- DB 조회 시 회원 정보만 조회하는 것이 효율적
- 팀 엔티티는 실제 값을 사용하는 시점에 DB에서 조회하는 것이 효율적

**지연로딩**
- 엔티티를 실제 사용하는 시점에 DB에서 조회

**프록시 객체**
- 지연 로딩을 사용 시 실제 엔티티 객체 대신 DB 조회를 지연할 수 있는 가짜 객체



