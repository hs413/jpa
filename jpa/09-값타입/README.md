## 값 타입

### 기본값 타입
- 자바 기본 타입
- 래퍼 클래스
- String

### 임베디드 타입
- 값 타입들을 모아 새로 정의한 타입(클래스)
- 기본 생성자 필수
- 응집도가 높고 재사용 가능

**임베디드 타입 사용법**
- `@Embeddable`: 값 타입을 정의하는 곳에
- `@Embedded`: 값 타입을 사용하는 곳에

```java
@Entity
public class Member {

  @Id @GeneratedValue
  private Long id;
  private String name;

  // 근무 기간
  @Embedded Period workPeriod;
  // 집 주소
  @Embedded Address homeAddress;
}

@Embeddable
public class Period {
  @Temporal(TemporalType.DATE) java.util.Date startDate;
  @Temporal(TemporalType.DATE) java.util.Date endDate;

  public boolean isWork(Date date) {
    // .. 값 타입을 위한 메소드를 정의할 수 있다.
  }

  public Period() { // 기본생성자 필수 
  }
}

@Embeddable
public class Address {
  @Column(name = "city")    // 매핑할 컬럼 정의 가능
  private String city;
  private String street;
  private String zipcode;

  public Address() { // 기본생성자 필수 
  }
}
```
**@AttributeOverride: 속성 재정의**
```java
@Entity
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String name;

    @Embedded Address homeAddress;
    
    // 컬럼명이 중복된다
    // @Embedded Address companyAddress;

    // @AttributeOverride 애너테이션으로 속성 재정의
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="city",
                    column=@Column(name = "COMPANY_CITY")),
            @AttributeOverride(name="street",
                    column=@Column(name = "COMPANY_STREET")),
            @AttributeOverrdie(name="zipcode",
                    column=@Column(name = "COMPANY_ZIPCODE"))
    })
    Address companyAddress;
}
```
