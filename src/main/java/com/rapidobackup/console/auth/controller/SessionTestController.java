package com.rapidobackup.console.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for testing session functionality
 * This will help us debug if sessions are working
 */
@RestController
@RequestMapping("/api/test")
public class SessionTestController {

    @GetMapping("/session")
    public ResponseEntity<?> testSession(HttpServletRequest request) {
        HttpSession session = request.getSession(true); // Force create session

        String sessionId = session.getId();
        boolean isNew = session.isNew();
        long creationTime = session.getCreationTime();
        long lastAccessedTime = session.getLastAccessedTime();

        return ResponseEntity.ok(java.util.Map.of(
            "sessionId", sessionId,
            "isNew", isNew,
            "creationTime", creationTime,
            "lastAccessedTime", lastAccessedTime,
            "message", "Session created/accessed successfully"
        ));
    }
}