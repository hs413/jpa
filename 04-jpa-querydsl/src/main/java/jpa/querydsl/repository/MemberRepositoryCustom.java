package jpa.querydsl.repository;

import jpa.querydsl.dto.MemberSearchCondition;
import jpa.querydsl.dto.MemberTeamDto;
import jpa.querydsl.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepositoryCustom{
    List<MemberTeamDto> search(MemberSearchCondition condition);
}
