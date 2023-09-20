package com.example.spacer.spacerbackend.controllers;

import com.example.spacer.spacerbackend.auth.TokensUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController // Esto es importante, con esto Spring sabe que esta clase es un controlador
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<?> getClientInfo(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            Map<String, Object> payload = TokensUtils.getPayloadFromToken(authorizationHeader.replace("Bearer ", ""));
            return new ResponseEntity<>(payload, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}
