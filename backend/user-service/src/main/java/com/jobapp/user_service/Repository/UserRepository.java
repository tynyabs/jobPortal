package com.jobapp.user_service.Repository;


import com.jobapp.user_service.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);
    User findUserByName(String name);
    User findUserByUsername(String username) ;
    Optional<User> findUserByMsidn(String msidn);
    Optional<User> findUserByIdn(String idn);
    Optional<User> findUserById(Long id);
    Optional<User> findRoleById(Long id);
}