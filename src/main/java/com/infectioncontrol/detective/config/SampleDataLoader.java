package com.infectioncontrol.detective.config;

import com.infectioncontrol.detective.domain.ErrorArea;
import com.infectioncontrol.detective.domain.Question;
import com.infectioncontrol.detective.repository.QuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SampleDataLoader implements CommandLineRunner {

    private final QuestionRepository questionRepository;

    public SampleDataLoader(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public void run(String... args) {
        if (questionRepository.count() > 0) {
            return;
        }

        Question q1 = new Question("q1", 1, "/sample/questions/q1.svg", "처치 카트 샘플 이미지", "멸균 물품 주변에 오염 가능 물품이 함께 놓여 있습니다.", 15);
        q1.addErrorArea(new ErrorArea(0.68, 0.63, 0.11, 0.13));

        Question q2 = new Question("q2", 2, "/sample/questions/q2.svg", "격리 물품 보관 샘플 이미지", "격리 물품은 지정 위치에 구분해 보관해야 합니다.", 15);
        q2.addErrorArea(new ErrorArea(0.18, 0.25, 0.2, 0.25));

        Question q3 = new Question("q3", 3, "/sample/questions/q3.svg", "손위생 구역 샘플 이미지", "이 장면에는 명확한 오류가 없습니다.", 15);

        Question q4 = new Question("q4", 4, "/sample/questions/q4.svg", "의료폐기물 관리 샘플 이미지", "오염 물품은 일반 보관 선반과 분리해야 합니다.", 15);
        q4.addErrorArea(new ErrorArea(0.72, 0.22, 0.13, 0.13));

        Question q5 = new Question("q5", 5, "/sample/questions/q5.svg", "환경 소독 샘플 이미지", "소독 완료 물품과 사용 전 물품의 동선이 섞여 있습니다.", 15);
        q5.addErrorArea(new ErrorArea(0.21, 0.31, 0.14, 0.18));

        questionRepository.save(q1);
        questionRepository.save(q2);
        questionRepository.save(q3);
        questionRepository.save(q4);
        questionRepository.save(q5);
    }
}
