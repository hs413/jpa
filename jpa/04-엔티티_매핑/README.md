# 엔티티 매핑

## @Entity
- **@Entity가 붙은 클래스는 JPA가 관리, 이를 엔티티라고 함**
- 테이블과 매핑할 클래스는 @Entity 필수

### 주의 사항
  - **기본 생성자 필수** (파라미터가 없는 public 또는 protected 생성자)
  - final, enum, interface, inner 클래스 사용X
    - 해당 클래스는 @Entity를 붙여서 DB Table과 매핑할 수 없음
  - DB에 저장할 필드에 final 사용X

### 속성 
- name
  ```java
  @Entity(name = "Member")
  public class Member { ... }
  ```
  - JPA에서 사용할 엔티티 이름을 지정
  - 기본 값: **클래스 이름을 그대로 사용**
    - Ex) Member
  - 같은 클래스 이름이 없으면 가급적 기본 값을 사용
## @Table
- **엔티티와 매핑할 테이블을 지정**
- 생략하면 매핑한 엔티티 이름을 테이블 이름으로 사용

### 속성
- name: 매핑할 테이블 이름
  - 기본값: 엔티티 이름
- catalog: catalog 기능이 있는 DB에서 catalog 매핑
- schema: schema 기능이 있는 DB에서 schema 매핑
- uniqueConstraints: DDL 생성 시 유니크 제약 조건을 만듦
  - 2개 이상 복합키 제약도 가능
  - 스키마 자동 생성 기능을 사용할 때만 사용

## 다양한 매핑 & DDL 생성 기능

### [Member 엔티티](start/src/main/java/jpabook/start/Member.java)

## 스키마 자동 생성
- 애플리케이션 실행 시점에 DDL을 자동으로 생성
- 테이블 중심 → 객체 중심
- DB 방언을 활용해서 DB에 맞는 적절한 DDL 생성
  
### hibernate.hbm2ddl.auto 속성
- create: 기존 테이블 삭제 후 다시 생성 (DROP + CREATE)
- create-drop: create(DROP + CREATE) + DROP
- update: 매핑정보를 비교, 변경 사항만 반영
- validate:  매핑정보를 비교, 차이가 있으면 경고를 남기고 애플리케이션을 실행하지 않음
- none:	자동 생성 기능을 사용하지 않음

### [설정](start/src/main/resources/application.yml)

## 키본 키 매핑

### 키 생성 전략
- 직접 할당: @Id만 사용
  - 기본 키를 애플리케이션에서 직접 할당

- 자동 생성(@GeneratedValue): 대리 키 사용 방식
  - IDENTITY: DB에 위임, MYSQL
  - SEQUENCE: DB 시퀀스 오브젝트 사용, ORACLE
    - @SequenceGenerator 필요
  - TABLE: 키 생성용 테이블 사용, 모든 DB에서 사용
    - @TableGenerator 필요
  - AUTO: 방언에 따라 자동 지정, 기본값

### 기본 키 직접 할당
**@Id로 매핑**
```java
@Id
@Column(name = "id")
private String id;
```
**가능 타입**
- 자바 기본형
- 자바 래퍼형

**엔티티를 저장하기 전 기본 키를 직접 할당**
```java
Board board = new Board();
board. setld ("idl") //기본 키 직접 할당
em.persist(board) ;
```

### IDENTITY 전략
- GenerationType.IDENTITY 사용
```java
@Id
@GeneratedValue (strategy = GenerationType.IDENTITY)
private Long id;
```
- 주로 MySQL, PostgreSQL, SQL Server, DB2에서 사용
  - Ex) MySQL의 AUTO_ INCREMENT
- IDENTITY 전략은 INSERT 한 후에 식별자 조회 가능
  - 엔티티에 식별자를 할당하려면 추가로 DB를 조회해야 함
  - 엔티티가 영속 상태가 되려면 식별자 필수
    - persist() 호출 즉시 INSERT SQL이 DB에 전달 됨
    - 따라서 트랜잭션 쓰기 지연을 사용할 수 없음

### SEQUENCE 전략
- 시퀀스는 유일한 값을 순서대로 생성하는 특별한 DB 오브젝트
- 오라클, PostgreSQL, DB2, H2 DB에서 사용 가능
```java
@Entity
@SequenceGenerator(
        name = "BOARD_SEQ_GENERATOR",
        sequenceName = "BOARD_SEQ", //매핑할 DB 시퀀스 이름
        initialVa丄ue = 1, allocationsize = 1)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "BQARD_SEQ_GENERATOR")
    private Long id;
}
```
- persist() 호출 시 먼저 DB 시퀀스를 사용해서 식별자를 조회
- 조회한 식별자를 엔티티에 할당한 후 엔티티를 영속성 컨텍스트에 저장
- 이후 트랜잭션 커밋으로 플러시가 일어나면 엔티티를 DB에 저장

**SEQUENCE 전략 최적화**
- SEQUENCE 전략은 DB 시퀀스를 통해 식별자를 조회하는 추가 작업 필요(DB와 2번 통신)
- DB 접근 횟수를 줄이기 위해 allocationSize 사용
  - 시퀀스를 한번에 size 만큼 조회한 뒤 메모리에 할당한 뒤 사용하여 성능 최적화
  - 동시성 문제도 해결
    - 2명의 클라이언트가 접근 한 경우
    - 첫 번째 클라이언트는 1~50의 식별자를 할당
    - 두 번째 클라이언트는 51~100의 식별자를 할당
    - 중복되지 않음

### TABLE 전략
- 키 생성 전용 테이블을 하나 만들어, DB 시퀀스를 흉내내는 전략
- 모든 DB에 적용 가능
- 성능이 좋지 않음
- 운영에서 사용하지 않음

### AUTO 전략
- DB 방언에 따라 IDENTITY, SEQUENCE, TABLE 전략 중 자동으로 선택
- DB를 변경해도 코드를 수정할 필요가 없음

## 필드와 컬럼 매핑

### @Column
- 객체 필드를 테이블 컬럼에 매핑
- name, nullable 속성이 주로 사용

**속성**
- name: 필드와 매핑할 컬럼 이름
  - 기본 값: 필드 이름
- insertable, updatable
  - 등록 변경 가능 여부
  - 읽기 전용일 때 false 사용
  - 기본 값: true
- nullable(DDL)
  - null값 허용 여부 설정
  - false 설정 시 not null 제약
  - 기본 값: true
- unique(DDL)
  - 유니크 제약조건을 걸 때 사용
  - @table 에서 사용하는 것을 권장
- columnDefinition(DDL)
  - 데이터베이스 컬럼 정보를 직접 줄 수 있다
  - ex) varchar(100) default 'EMPTY'
- length(DDL)
  - 문자 길이 제약조건, String 타입만 사용
  - 기본 값: 255
- precision, scale(DDL)
  - BigDecimal 타입에서 사용
  - 소수점을 포함한 전체 자릿수 지정
  - 아주 큰 숫자나 정밀한 소수를 다루어야 할 때 사용
  
### @Enumerated
- enum 타입을 매핑할 때 사용
- ORDINAL은 사용 하지 않음

### @Temporal
- 날짜 타입(java.util.Date, java.util.Calendar)을 매핑할 때 사용
- DB는 세 가지 타입 존재
  - date(2024-01-01)
  - time(12:00:00)
  - timestamp(2024-01-01 12:00:00)
- 생략할 경우 timestamp(datetime)로 정의

### @Lob
- BLOB, CLOB 타입과 매핑
- 매핑하는 필드 타입이 문자면 CLOB 매핑, 나머지는 BLOB 매핑
    - CLOB: String, char[], java.sql.CLOB
    - BLOB: byte[], java.sql.BLOB
  
### @Transient
- 해당 필드는 매핑하지 않음
  - DB에 저장 및 조회하지 않음
- 메모리 상에서 임시로 값을 보관하고 싶을 때 사용


