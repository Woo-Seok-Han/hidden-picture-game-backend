package com.infectioncontrol.detective.repository;

import com.infectioncontrol.detective.domain.Question;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, String> {

    List<Question> findByActiveTrueOrderByQuestionNumberAsc();

    int countByActiveTrue();

    List<Question> findAllByOrderByQuestionNumberAsc();
}
