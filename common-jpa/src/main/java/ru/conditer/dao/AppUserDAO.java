package ru.conditer.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.conditer.entity.AppUser;

import java.util.Optional;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findAppByTelegramUserId(Long id);
}
