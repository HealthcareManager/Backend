package com.example.HealthcareManager.Controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Service.AdminService;
import com.example.HealthcareManager.Service.UserDataService;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private AdminService adminService;

    @PostMapping("/admin-login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        System.out.println("123" + user.getUsername());
        try {
            Map<String, String> adminLogin = adminService.login(user);
            return ResponseEntity.ok(adminLogin);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @GetMapping("/user-data/{userId}")
    public ResponseEntity<Object> getUserDataList(@PathVariable String userId) {

        List<Object> userDataList = userDataService.getUserDataList(userId);
        return ResponseEntity.ok(userDataList);
    }

    @GetMapping("/users-data")
    public ResponseEntity<Map<String, Object>> getAllUsersDataList() {
        Map<String, Object> allUsersDataList = userDataService.getAllUserDataList();
        return ResponseEntity.ok(allUsersDataList);
    }

}
