package com.quiz.system.service;

import com.quiz.system.dto.QuestionDto;
import com.quiz.system.dto.ResponseDto;
import com.quiz.system.dto.StatisticDto;
import com.quiz.system.exception.QuizAlreadySubmittedException;
import com.quiz.system.model.Answer;
import com.quiz.system.model.Statistics;
import com.quiz.system.model.Submission;
import com.quiz.system.model.User;
import com.quiz.system.repository.StatisticsRepository;
import com.quiz.system.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final SubmissionRepository submissionRepository;

    public StatisticsService(StatisticsRepository statisticsRepository, SubmissionRepository submissionRepository) {
        this.statisticsRepository = statisticsRepository;
        this.submissionRepository = submissionRepository;
    }

    public List<QuestionDto> getQuestions() {
        return statisticsRepository.getQuestions();
    }

    public List<StatisticDto> getStatistics() {
        List<StatisticDto> statisticsDto = new ArrayList<>();
        List<Statistics> statistics =  statisticsRepository.findAll();
        for(Statistics statistic : statistics) {
            String question = getQuestionByQuestionNumber(statistic.getQuestionNumber());
            StatisticDto statisticDto = new StatisticDto(question, statistic.getGoCount(), statistic.getNoGoCount());
            statisticsDto.add(statisticDto);
        }
        return statisticsDto;
    }

    public String getQuestionByQuestionNumber(int questionNumber) {
        return statisticsRepository.findByQuestionNumber(questionNumber);
    }

    @Transactional
    public void submitAnswers(User user, List<ResponseDto> responseDtos) {
        isUserExists(user);
        is10ResponsesSubmitted(responseDtos);
        List<Answer> answers = responseDtos.stream()
                .map(ResponseDto::answer).toList();

        Map<Integer, Statistics> existingStats = statisticsRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Statistics::getQuestionNumber, s -> s));

        for (int i = 1; i <= 10; i++) {
            Answer answer = answers.get(i - 1);

            Statistics stats = existingStats.getOrDefault(i, new Statistics(i));

            if (answer == Answer.GO) {
                stats.incrementGo();
            } else {
                stats.incrementNoGo();
            }

            if (stats.getId() == null) {
                statisticsRepository.createStatistics(stats);
            } else {
                statisticsRepository.updateStatistics(stats);
            }
        }

        submissionRepository.createSubmission(new Submission(user));
    }

    private void isUserExists(User user) {
        if (submissionRepository.existsByUserId(user.getId())) {
            throw new QuizAlreadySubmittedException("User has already submitted the quiz");
        }
    }

    private static void is10ResponsesSubmitted(List<ResponseDto> responseDtos) {
        if (responseDtos.size() != 10) {
            throw new IllegalArgumentException("Exactly 10 responses must be submitted");
        }
    }
}
