package ru.conditer.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.conditer.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
