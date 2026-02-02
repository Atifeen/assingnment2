package com.example.assign2.web;

import com.example.assign2.entity.Student;
import com.example.assign2.entity.Teacher;
import com.example.assign2.entity.UserAccount;
import com.example.assign2.repository.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentUserService {

    private final UserAccountRepository userAccountRepository;

    public CurrentUserService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public Optional<UserAccount> getAccount(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return Optional.empty();
        }
        return userAccountRepository.findByUsername(authentication.getName());
    }

    public String role(Authentication authentication) {
        return getAccount(authentication)
                .map(UserAccount::getRole)
                .map(r -> r.equals("ROLE_TEACHER") ? "TEACHER" : "STUDENT")
                .orElse("—");
    }

    public boolean isTeacher(Authentication authentication) {
        return getAccount(authentication).map(UserAccount::getRole).map("ROLE_TEACHER"::equals).orElse(false);
    }

    public Optional<Student> currentStudent(Authentication authentication) {
        return getAccount(authentication).map(UserAccount::getStudent);
    }

    public Optional<Teacher> currentTeacher(Authentication authentication) {
        return getAccount(authentication).map(UserAccount::getTeacher);
    }

    public String displayName(Authentication authentication) {
        return getAccount(authentication)
                .map(a -> {
                    if ("ROLE_STUDENT".equals(a.getRole()) && a.getStudent() != null && a.getStudent().getName() != null) {
                        return a.getStudent().getName();
                    }
                    if ("ROLE_TEACHER".equals(a.getRole()) && a.getTeacher() != null && a.getTeacher().getName() != null) {
                        return a.getTeacher().getName();
                    }
                    return a.getUsername();
                })
                .orElse("—");
    }
}

