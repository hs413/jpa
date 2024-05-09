package jpa.v6.lazy;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Member {
    @Id @GeneratedValue
    private Long id;

    /*
    * @ManyToOne, @OneToOne 기본이 즉시 로딩(EAGER)
    * 연관 관계 클래스는 지연 로딩 사용을 권장
    * */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;
}
