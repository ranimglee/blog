package com.blog.afaq.service;

import com.blog.afaq.dto.response.BannedUserDto;
import com.blog.afaq.dto.response.UserDto;
import com.blog.afaq.model.User;
import com.blog.afaq.model.UserStatus;
import com.blog.afaq.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public BannedUserDto banUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        User user = userOpt.get();
        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);

        // Convert User entity to UserDto (simple mapping)
        BannedUserDto dto = new BannedUserDto();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        dto.setCountry(user.getCountry());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setStatus(user.getStatus());
        dto.setRole(user.getRole());
        dto.setBannedAt(Instant.now());

        return dto;
    }
}
