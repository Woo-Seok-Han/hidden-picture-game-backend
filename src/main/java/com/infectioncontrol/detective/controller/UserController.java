package com.infectioncontrol.detective.controller;

import com.infectioncontrol.detective.dto.DetailedGameResultResponse;
import com.infectioncontrol.detective.dto.EmployeeValidateRequest;
import com.infectioncontrol.detective.dto.EmployeeValidateResponse;
import com.infectioncontrol.detective.service.GameService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final GameService gameService;

    public UserController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/validate")
    public EmployeeValidateResponse validate(@Valid @RequestBody EmployeeValidateRequest request) {
        return new EmployeeValidateResponse(true, "직원이 확인되었습니다.");
    }

    @GetMapping("/{employeeNumber}/results")
    public DetailedGameResultResponse latestResult(@PathVariable String employeeNumber) {
        return gameService.getLatestResult(employeeNumber);
    }
}
