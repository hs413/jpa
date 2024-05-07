package jpa.v3;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team")
    List<Member> members = new ArrayList<>();

    public List<Member> getMembers() {
        return members;
    }

//    연관관계 편의 메소드가 양 쪽에 있을 경우
//    문제의 소지가 될 확률이 높으니 한 쪽에만 생성
    /*public void addMember(Member member) {
        member.setTeam(this);
        members.add(member);
    }*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
