package com.infectioncontrol.detective.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class GameSession {

    @Id
    private String id;

    @Column(nullable = false)
    private String employeeNumber;

    @Column(nullable = false)
    private Instant startTime;

    private Instant completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameSessionStatus status;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameAnswer> answers = new ArrayList<>();

    protected GameSession() {
    }

    public GameSession(String employeeNumber) {
        this.id = UUID.randomUUID().toString();
        this.employeeNumber = employeeNumber;
        this.startTime = Instant.now();
        this.status = GameSessionStatus.STARTED;
    }

    public void complete(List<GameAnswer> newAnswers) {
        answers.clear();
        newAnswers.forEach(this::addAnswer);
        this.completedAt = Instant.now();
        this.status = GameSessionStatus.COMPLETED;
    }

    private void addAnswer(GameAnswer answer) {
        answer.attachTo(this);
        answers.add(answer);
    }

    public Duration elapsedTime() {
        Instant end = completedAt == null ? Instant.now() : completedAt;
        return Duration.between(startTime, end);
    }

    public String getId() {
        return id;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public GameSessionStatus getStatus() {
        return status;
    }

    public List<GameAnswer> getAnswers() {
        return answers;
    }
}
