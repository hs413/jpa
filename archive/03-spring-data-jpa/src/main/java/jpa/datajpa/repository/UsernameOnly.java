package jpa.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    // close Projections - select 쿼리 최적화 가능
//    String getUsername();

    // Open Projections - JPQL SELECT 최적화가 안됨
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
