package com.infectioncontrol.detective.repository;

import com.infectioncontrol.detective.domain.GameSession;
import com.infectioncontrol.detective.domain.GameSessionStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameSessionRepository extends JpaRepository<GameSession, String> {

    Optional<GameSession> findFirstByEmployeeNumberAndStatusOrderByCompletedAtDesc(String employeeNumber, GameSessionStatus status);

    List<GameSession> findByStatusOrderByCompletedAtDesc(GameSessionStatus status);
}
