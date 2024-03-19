package ru.conditer.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.conditer.entity.RawData;

public interface RawDataDao extends JpaRepository<RawData, Long> {
}
