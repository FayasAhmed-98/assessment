package com.sinolanka.assessment.repository;
import com.sinolanka.assessment.entity.LoginAttempt;
import com.sinolanka.assessment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    List<LoginAttempt> findTop5ByUserOrderByTimestampDesc(User user);
}