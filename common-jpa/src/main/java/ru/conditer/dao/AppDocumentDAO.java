package ru.conditer.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.conditer.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
