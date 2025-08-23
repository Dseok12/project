package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final Random random = new SecureRandom();
    private final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String generateRandomUsername() {
        int length = 10;
        StringBuilder sb = new StringBuilder();
        do {
            sb.setLength(0);
            for (int i = 0; i < length; i++) {
                sb.append(CHAR_SET.charAt(random.nextInt(CHAR_SET.length())));
            }
        } while(userRepository.existsByUsername(sb.toString()));
        return sb.toString();
    }

    public User registerUser(String email, String rawPassword) throws RuntimeException {
        if(userRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 등록된 이메일입니다.");
        }

        String username = generateRandomUsername();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User(username, email, encodedPassword);
        return userRepository.save(user);
    }
}
