package com.example.HealthcareManager.Controller;

import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.UserRepository;
import com.example.HealthcareManager.Service.HealthMetricsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


@RestController
public class HealthMetricsController {
    @Autowired
    private final HealthMetricsService healthMetricsService;
    @Autowired
    private final UserRepository userRepository;

    public HealthMetricsController(HealthMetricsService healthMetricsService, UserRepository userRepository) {
        this.healthMetricsService = healthMetricsService;
        this.userRepository = userRepository;
    }

    @PostMapping("/api/generate-fake-data/{userId}")
    public ResponseEntity<String> generateFakeData(@PathVariable String userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    healthMetricsService.generateFakeData(user);
    return ResponseEntity.ok("Fake data generated!");
}

}
