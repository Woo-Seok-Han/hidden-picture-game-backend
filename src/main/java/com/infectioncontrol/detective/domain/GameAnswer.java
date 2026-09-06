package com.infectioncontrol.detective.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class GameAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionId;
    private Integer questionNumber;
    private Double selectedX;
    private Double selectedY;
    private boolean hasError;
    private boolean correct;
    private Instant answeredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private GameSession session;

    protected GameAnswer() {
    }

    public GameAnswer(String questionId, Integer questionNumber, Double selectedX, Double selectedY, boolean hasError, boolean correct, Instant answeredAt) {
        this.questionId = questionId;
        this.questionNumber = questionNumber;
        this.selectedX = selectedX;
        this.selectedY = selectedY;
        this.hasError = hasError;
        this.correct = correct;
        this.answeredAt = answeredAt;
    }

    void attachTo(GameSession session) {
        this.session = session;
    }

    public Long getId() {
        return id;
    }

    public String getQuestionId() {
        return questionId;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public Double getSelectedX() {
        return selectedX;
    }

    public Double getSelectedY() {
        return selectedY;
    }

    public boolean isHasError() {
        return hasError;
    }

    public boolean isCorrect() {
        return correct;
    }

    public Instant getAnsweredAt() {
        return answeredAt;
    }
}
