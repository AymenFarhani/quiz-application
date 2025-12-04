package com.quiz.system.dto;

import com.quiz.system.model.Answer;

public record ResponseDto(Integer questionNumber, Answer answer) {
}
