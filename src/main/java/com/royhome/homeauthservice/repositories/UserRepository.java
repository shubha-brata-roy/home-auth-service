package com.royhome.homeauthservice.repositories;

import com.royhome.homeauthservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findUserByEmail(String email);

    public User save(User user);
}
