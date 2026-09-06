package com.infectioncontrol.detective.service;

import com.infectioncontrol.detective.domain.GameAnswer;
import com.infectioncontrol.detective.domain.GameSession;
import com.infectioncontrol.detective.domain.Question;
import com.infectioncontrol.detective.dto.AnswerResponse;
import com.infectioncontrol.detective.dto.DetailedGameResultResponse;
import com.infectioncontrol.detective.dto.GameResultResponse;
import com.infectioncontrol.detective.dto.QuestionDetailResponse;
import com.infectioncontrol.detective.dto.SelectedPointDto;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class GameResultMapper {

    private final StorageService storageService;

    public GameResultMapper(StorageService storageService) {
        this.storageService = storageService;
    }

    public GameResultResponse toResult(GameSession session, int totalQuestions) {
        List<AnswerResponse> answers = toAnswerResponses(session.getAnswers());
        int correctAnswers = (int) session.getAnswers().stream().filter(GameAnswer::isCorrect).count();
        return new GameResultResponse(
                session.getId(),
                session.getEmployeeNumber(),
                answers,
                correctAnswers,
                totalQuestions,
                totalQuestions == 0 ? 0 : (double) correctAnswers / totalQuestions,
                formatDuration(session.elapsedTime()),
                session.getCompletedAt()
        );
    }

    public DetailedGameResultResponse toDetailedResult(GameSession session, List<Question> questions) {
        GameResultResponse result = toResult(session, questions.size());
        Map<String, Question> questionById = questions.stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));
        List<QuestionDetailResponse> details = session.getAnswers().stream()
                .map(answer -> toDetail(answer, questionById.get(answer.getQuestionId())))
                .toList();

        return new DetailedGameResultResponse(
                result.sessionId(),
                result.employeeNumber(),
                result.answers(),
                result.correctAnswers(),
                result.totalQuestions(),
                result.accuracy(),
                result.totalTime(),
                result.completedAt(),
                details
        );
    }

    private QuestionDetailResponse toDetail(GameAnswer answer, Question question) {
        String correctAnswer = question != null && question.hasError() ? "오류 있음" : "오류 없음";
        String userAnswer = answer.isHasError() ? "오류 있음" : "오류 없음";
        return new QuestionDetailResponse(
                answer.getQuestionId(),
                answer.getQuestionNumber() == null ? 0 : answer.getQuestionNumber(),
                question == null ? "문제" : "문제 " + question.getQuestionNumber(),
                question == null ? "" : storageService.resolvePublicUrl(question.getImageUrl()),
                userAnswer,
                correctAnswer,
                answer.isCorrect(),
                question == null ? "" : question.getExplanation(),
                toSelectedPoint(answer)
        );
    }

    private List<AnswerResponse> toAnswerResponses(List<GameAnswer> answers) {
        return answers.stream()
                .map(answer -> new AnswerResponse(
                        answer.getQuestionId(),
                        answer.getQuestionNumber(),
                        toSelectedPoint(answer),
                        answer.isHasError(),
                        answer.isCorrect(),
                        answer.getAnsweredAt()
                ))
                .toList();
    }

    private SelectedPointDto toSelectedPoint(GameAnswer answer) {
        if (answer.getSelectedX() == null || answer.getSelectedY() == null) {
            return null;
        }
        return new SelectedPointDto(answer.getSelectedX(), answer.getSelectedY());
    }

    private String formatDuration(Duration duration) {
        long seconds = Math.max(0, duration.toSeconds());
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
        }
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
