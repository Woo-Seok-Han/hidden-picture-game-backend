package com.infectioncontrol.detective.config;

import com.infectioncontrol.detective.repository.QuestionRepository;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SampleDataCleanup implements CommandLineRunner {

    private static final Set<String> SAMPLE_IDS = Set.of("q1", "q2", "q3", "q4", "q5");

    private final QuestionRepository questionRepository;

    public SampleDataCleanup(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        SAMPLE_IDS.forEach(id -> questionRepository.findById(id)
                .filter(question -> question.getImageUrl().startsWith("/sample/questions/"))
                .ifPresent(questionRepository::delete));
    }
}
