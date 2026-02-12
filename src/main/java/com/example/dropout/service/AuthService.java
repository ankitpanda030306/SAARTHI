package com.example.dropout.service;

import com.example.dropout.entity.User;
import com.example.dropout.repository.UserRepository;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    
    // Simple in-memory storage for locked schools
    private static final Set<String> lockedSchools = new HashSet<>();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String username, String password) throws AuthException {
        User user = userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);

        if (user == null) {
            throw new AuthException("Invalid Credentials");
        }

        // CHECK IF SCHOOL IS LOCKED (If user is a Teacher)
        if ("TEACHER".equals(user.getRole()) && isSchoolLocked(user.getSchool())) {
            throw new AuthException("Portal is CLOSED by the Principal.");
        }

        // Login Success
        VaadinSession.getCurrent().setAttribute(User.class, user);
        return user;
    }

    public User getCurrentUser() {
        return VaadinSession.getCurrent().getAttribute(User.class);
    }

    public void logout() {
        VaadinSession.getCurrent().setAttribute(User.class, null);
    }

    // --- LOCKING LOGIC ---
    public void lockSchool(String schoolName) {
        lockedSchools.add(schoolName);
    }

    public void unlockSchool(String schoolName) {
        lockedSchools.remove(schoolName);
    }

    public boolean isSchoolLocked(String schoolName) {
        return lockedSchools.contains(schoolName);
    }

    // Custom Exception for cleaner error handling
    public static class AuthException extends Exception {
        public AuthException(String message) { super(message); }
    }
}