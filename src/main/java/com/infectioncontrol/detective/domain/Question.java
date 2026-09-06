package com.infectioncontrol.detective.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question {

    @Id
    private String id;

    @Column(nullable = false)
    private int questionNumber;

    @Column(nullable = false, length = 1000)
    private String imageUrl;

    @Column(nullable = false)
    private String imageAlt;

    @Column(nullable = false, length = 2000)
    private String explanation;

    @Column(nullable = false)
    private int timeLimitSeconds;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<ErrorArea> errorAreas = new ArrayList<>();

    protected Question() {
    }

    public Question(String id, int questionNumber, String imageUrl, String imageAlt, String explanation, int timeLimitSeconds) {
        this.id = id;
        this.questionNumber = questionNumber;
        this.imageUrl = imageUrl;
        this.imageAlt = imageAlt;
        this.explanation = explanation;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public void update(String imageUrl, String imageAlt, String explanation, int timeLimitSeconds, List<ErrorArea> newErrorAreas) {
        if (imageUrl != null && !imageUrl.isBlank()) {
            this.imageUrl = imageUrl;
        }
        this.imageAlt = imageAlt;
        this.explanation = explanation;
        this.timeLimitSeconds = timeLimitSeconds;
        this.updatedAt = Instant.now();
        this.errorAreas.clear();
        newErrorAreas.forEach(this::addErrorArea);
    }

    public void addErrorArea(ErrorArea area) {
        area.attachTo(this);
        this.errorAreas.add(area);
    }

    public boolean hasError() {
        return !errorAreas.isEmpty();
    }

    public String getId() {
        return id;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getImageAlt() {
        return imageAlt;
    }

    public String getExplanation() {
        return explanation;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<ErrorArea> getErrorAreas() {
        return errorAreas;
    }
}
