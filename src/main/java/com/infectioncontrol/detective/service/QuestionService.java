package com.infectioncontrol.detective.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infectioncontrol.detective.domain.ErrorArea;
import com.infectioncontrol.detective.domain.Question;
import com.infectioncontrol.detective.dto.AdminQuestionResponse;
import com.infectioncontrol.detective.dto.ErrorAreaDto;
import com.infectioncontrol.detective.dto.QuestionSummaryResponse;
import com.infectioncontrol.detective.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;

    public QuestionService(
            QuestionRepository questionRepository,
            QuestionMapper questionMapper,
            StorageService storageService,
            ObjectMapper objectMapper
    ) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
        this.storageService = storageService;
        this.objectMapper = objectMapper;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<QuestionSummaryResponse> getPlayableQuestions() {
        List<Question> activeQuestions = new ArrayList<>(questionRepository.findByActiveTrueOrderByQuestionNumberAsc());
        if (activeQuestions.size() < 5) {
            throw new IllegalArgumentException("출제 가능한 문제가 5개 이상 필요합니다.");
        }

        Collections.shuffle(activeQuestions);
        return activeQuestions.stream()
                .limit(5)
                .map(questionMapper::toSummary)
                .toList();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<AdminQuestionResponse> getAdminQuestions() {
        return questionRepository.findAllByOrderByQuestionNumberAsc().stream()
                .map(questionMapper::toAdminResponse)
                .toList();
    }

    @Transactional
    public AdminQuestionResponse createQuestion(
            MultipartFile image,
            String imageAlt,
            String explanation,
            int timeLimitSeconds,
            String errorAreasJson
    ) {
        String imageUrl = storageService.storeImage(image);
        int nextQuestionNumber = nextQuestionNumber();
        Question question = new Question(
                UUID.randomUUID().toString(),
                nextQuestionNumber,
                imageUrl,
                defaultText(imageAlt, "감염관리 문제 이미지"),
                defaultText(explanation, ""),
                normalizeTimeLimit(timeLimitSeconds)
        );
        parseErrorAreas(errorAreasJson).forEach(question::addErrorArea);
        return questionMapper.toAdminResponse(questionRepository.save(question));
    }

    @Transactional
    public AdminQuestionResponse updateQuestion(
            String id,
            MultipartFile image,
            String imageAlt,
            String explanation,
            int timeLimitSeconds,
            String errorAreasJson
    ) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));
        String imageUrl = image == null || image.isEmpty() ? null : storageService.storeImage(image);
        question.update(
                imageUrl,
                defaultText(imageAlt, question.getImageAlt()),
                defaultText(explanation, ""),
                normalizeTimeLimit(timeLimitSeconds),
                parseErrorAreas(errorAreasJson)
        );
        return questionMapper.toAdminResponse(question);
    }

    @Transactional
    public void deleteQuestion(String id) {
        if (!questionRepository.existsById(id)) {
            throw new IllegalArgumentException("문제를 찾을 수 없습니다.");
        }
        questionRepository.deleteById(id);
    }

    @Transactional
    public AdminQuestionResponse changeActive(String id, boolean active) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));
        question.changeActive(active);
        return questionMapper.toAdminResponse(question);
    }

    private List<ErrorArea> parseErrorAreas(String errorAreasJson) {
        if (errorAreasJson == null || errorAreasJson.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(errorAreasJson, new TypeReference<List<ErrorAreaDto>>() {
                    })
                    .stream()
                    .map(area -> new ErrorArea(area.x(), area.y(), area.width(), area.height()))
                    .toList();
        } catch (Exception e) {
            throw new IllegalArgumentException("errorAreas JSON 형식이 올바르지 않습니다.", e);
        }
    }

    private String defaultText(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private int normalizeTimeLimit(int seconds) {
        if (seconds <= 0) {
            return 30;
        }
        return Math.max(5, Math.min(300, seconds));
    }

    private int nextQuestionNumber() {
        return questionRepository.findAll().stream()
                .mapToInt(Question::getQuestionNumber)
                .max()
                .orElse(0) + 1;
    }
}
