package com.rahul.typetowin.application;

import com.rahul.typetowin.application.entity.GameResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameResultRepository extends JpaRepository<GameResultEntity, Long> {
}
