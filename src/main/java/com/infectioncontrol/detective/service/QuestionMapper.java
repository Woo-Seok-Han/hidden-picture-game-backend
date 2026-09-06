package com.infectioncontrol.detective.service;

import com.infectioncontrol.detective.domain.ErrorArea;
import com.infectioncontrol.detective.domain.Question;
import com.infectioncontrol.detective.dto.AdminQuestionResponse;
import com.infectioncontrol.detective.dto.ErrorAreaDto;
import com.infectioncontrol.detective.dto.QuestionSummaryResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {

    public QuestionSummaryResponse toSummary(Question question) {
        return new QuestionSummaryResponse(
                question.getId(),
                question.getQuestionNumber(),
                question.getImageUrl(),
                question.getImageAlt(),
                question.getTimeLimitSeconds()
        );
    }

    public AdminQuestionResponse toAdminResponse(Question question) {
        return new AdminQuestionResponse(
                question.getId(),
                question.getQuestionNumber(),
                question.getImageUrl(),
                question.getImageAlt(),
                question.getExplanation(),
                question.getTimeLimitSeconds(),
                toErrorAreaDtos(question.getErrorAreas()),
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }

    public List<ErrorAreaDto> toErrorAreaDtos(List<ErrorArea> areas) {
        return areas.stream()
                .map(area -> new ErrorAreaDto(area.getId(), area.getX(), area.getY(), area.getWidth(), area.getHeight()))
                .toList();
    }
}
