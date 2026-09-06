package com.infectioncontrol.detective.service;

import com.infectioncontrol.detective.domain.ErrorArea;
import com.infectioncontrol.detective.domain.GameAnswer;
import com.infectioncontrol.detective.domain.GameSession;
import com.infectioncontrol.detective.domain.GameSessionStatus;
import com.infectioncontrol.detective.domain.Question;
import com.infectioncontrol.detective.dto.AnswerRequest;
import com.infectioncontrol.detective.dto.DetailedGameResultResponse;
import com.infectioncontrol.detective.dto.GameResultResponse;
import com.infectioncontrol.detective.dto.GameStartResponse;
import com.infectioncontrol.detective.dto.SelectedPointDto;
import com.infectioncontrol.detective.repository.GameSessionRepository;
import com.infectioncontrol.detective.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameSessionRepository gameSessionRepository;
    private final QuestionRepository questionRepository;
    private final GameResultMapper gameResultMapper;

    public GameService(
            GameSessionRepository gameSessionRepository,
            QuestionRepository questionRepository,
            GameResultMapper gameResultMapper
    ) {
        this.gameSessionRepository = gameSessionRepository;
        this.questionRepository = questionRepository;
        this.gameResultMapper = gameResultMapper;
    }

    @Transactional
    public GameStartResponse start(String employeeNumber) {
        GameSession session = gameSessionRepository.save(new GameSession(employeeNumber.trim()));
        return new GameStartResponse(session.getId(), session.getEmployeeNumber(), session.getStartTime());
    }

    @Transactional
    public GameResultResponse submit(String sessionId, List<AnswerRequest> answers) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("게임 세션을 찾을 수 없습니다."));
        List<Question> questions = questionRepository.findByActiveTrueOrderByQuestionNumberAsc();
        Map<String, Question> questionById = questions.stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));
        List<GameAnswer> scoredAnswers = (answers == null ? List.<AnswerRequest>of() : answers).stream()
                .map(answer -> toScoredAnswer(answer, questionById.get(answer.questionId())))
                .toList();

        session.complete(scoredAnswers);
        return gameResultMapper.toResult(session, questions.size());
    }

    @Transactional
    public GameResultResponse complete(String sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("게임 세션을 찾을 수 없습니다."));
        if (session.getStatus() != GameSessionStatus.COMPLETED) {
            session.complete(List.of());
        }
        return gameResultMapper.toResult(session, questionRepository.countByActiveTrue());
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public DetailedGameResultResponse getLatestResult(String employeeNumber) {
        GameSession session = gameSessionRepository
                .findFirstByEmployeeNumberAndStatusOrderByCompletedAtDesc(employeeNumber, GameSessionStatus.COMPLETED)
                .orElseThrow(() -> new IllegalArgumentException("완료된 게임 결과를 찾을 수 없습니다."));
        return gameResultMapper.toDetailedResult(session, questionRepository.findByActiveTrueOrderByQuestionNumberAsc());
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<DetailedGameResultResponse> getAllResults() {
        List<Question> questions = questionRepository.findByActiveTrueOrderByQuestionNumberAsc();
        return gameSessionRepository.findByStatusOrderByCompletedAtDesc(GameSessionStatus.COMPLETED).stream()
                .map(session -> gameResultMapper.toDetailedResult(session, questions))
                .toList();
    }

    private GameAnswer toScoredAnswer(AnswerRequest answer, Question question) {
        SelectedPointDto selectedPoint = answer.selectedPoint();
        Double selectedX = selectedPoint == null ? null : selectedPoint.x();
        Double selectedY = selectedPoint == null ? null : selectedPoint.y();
        boolean correct = isCorrect(answer, question);
        int questionNumber = answer.questionNumber() != null
                ? answer.questionNumber()
                : question == null ? 0 : question.getQuestionNumber();

        return new GameAnswer(
                answer.questionId(),
                questionNumber,
                selectedX,
                selectedY,
                answer.hasError(),
                correct,
                answer.timestamp() == null ? Instant.now() : answer.timestamp()
        );
    }

    private boolean isCorrect(AnswerRequest answer, Question question) {
        if (question == null) {
            return false;
        }
        if (!question.hasError()) {
            return !answer.hasError();
        }
        if (!answer.hasError() || answer.selectedPoint() == null) {
            return false;
        }
        double x = answer.selectedPoint().x();
        double y = answer.selectedPoint().y();
        return question.getErrorAreas().stream().anyMatch(area -> area.contains(x, y));
    }
}
