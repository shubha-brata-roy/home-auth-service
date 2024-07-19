package com.royhome.homeauthservice.repositories;

import com.royhome.homeauthservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    public Optional<Session> findSessionByToken(String token);

    public Session save(Session session);
}
