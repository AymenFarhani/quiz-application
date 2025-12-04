package com.quiz.system.controller;

import com.quiz.system.dto.QuestionDto;
import com.quiz.system.dto.ResponseDto;
import com.quiz.system.dto.StatisticDto;
import com.quiz.system.model.User;
import com.quiz.system.repository.UserRepository;
import com.quiz.system.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
@Tag(name = "Statistics", description = "Endpoints to manage quiz statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;
    private final UserRepository userRepository;

    public StatisticsController(StatisticsService statisticsService, UserRepository userRepository) {
        this.statisticsService = statisticsService;
        this.userRepository = userRepository;
    }

    @SecurityRequirement(name = "JWT Auth")
    @GetMapping("/questions")
    @Operation(summary = "Get all questions", description = "Returns all questions")
    public ResponseEntity<List<QuestionDto>> getQuestions() {
        return ResponseEntity.ok(statisticsService.getQuestions());
    }

    @SecurityRequirement(name = "JWT Auth")
    @GetMapping
    @Operation(summary = "Get all statistics", description = "Returns the GO/NO-GO count for all questions")
    public ResponseEntity<List<StatisticDto>> getStatistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @SecurityRequirement(name = "JWT Auth")
    @PostMapping("/submit")
    @Operation(summary = "Submit quiz answers by an authenticated user", description = "Submit a list of 10 responses by an authenticated user")
    public ResponseEntity<String> submitAnswers(@AuthenticationPrincipal UserDetails userDetails, @RequestBody List<ResponseDto> responses) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        if(user == null) {
            throw new UsernameNotFoundException("User Not found!");
        }
        statisticsService.submitAnswers(user, responses);
        return ResponseEntity.ok("Answers submitted successfully!");
    }
}
