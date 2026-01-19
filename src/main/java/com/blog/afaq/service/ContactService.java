package com.blog.afaq.service;

import com.blog.afaq.dto.request.ContactMessageRequest;
import com.blog.afaq.exception.UserNotFoundException;
import com.blog.afaq.model.ContactMessage;
import com.blog.afaq.model.Role;
import com.blog.afaq.model.User;
import com.blog.afaq.repository.ContactMessageRepository;
import com.blog.afaq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ContactMessageRepository contactMessageRepository;

    public void processMessage(ContactMessageRequest request) {
        ContactMessage contactMessage = ContactMessage.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .subject(request.getSubject())
                .message(request.getMessage())
                .createdAt(Instant.now())
                .build();

        contactMessageRepository.save(contactMessage);

        // Notify admin by email
        User admin = userRepository.findFirstByRole(Role.ADMIN)
                .orElseThrow(() -> new UserNotFoundException("Admin user not found."));

        String subject = "📩 New Contact Message: " + request.getSubject();
        String body = """
                <p><strong>Name:</strong> %s</p>
                <p><strong>Email:</strong> %s</p>
                <p><strong>Subject:</strong> %s</p>
                <p><strong>Message:</strong></p>
                <p>%s</p>
                """.formatted(
                request.getFullName(),
                request.getEmail(),
                request.getSubject(),
                request.getMessage()
        );

        emailService.sendHtmlEmailTo(admin.getEmail(), subject, body, request.getEmail());
    }


    public List<ContactMessage> getAllMessages() {
        return contactMessageRepository.findAll();
    }

    public void deleteMessage(String id) {
        contactMessageRepository.deleteById(id);
    }
}
