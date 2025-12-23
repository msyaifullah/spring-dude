package com.yyggee.eggs.repositories.ds1;

import com.yyggee.eggs.model.ds1.User;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;

public interface UserJPARepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    User findByUsername(String username);

    @Transactional
    void deleteByUsername(String username);

}