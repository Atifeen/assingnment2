package com.example.assign2;

import com.example.assign2.entity.Student;
import com.example.assign2.entity.UserAccount;
import com.example.assign2.repository.UserAccountRepository;
import com.example.assign2.web.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CurrentUserServiceUnitTests {

    @Test
    void displayNameAndRoleForStudent() {
        UserAccountRepository repo = mock(UserAccountRepository.class);
        CurrentUserService svc = new CurrentUserService(repo);

        UserAccount ua = new UserAccount();
        ua.setUsername("alice");
        ua.setRole("ROLE_STUDENT");
        Student s = new Student();
        s.setName("Alice Student");
        ua.setStudent(s);

        when(repo.findByUsername("alice")).thenReturn(Optional.of(ua));

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("alice");

        assertEquals("STUDENT", svc.role(auth));
        assertEquals("Alice Student", svc.displayName(auth));
        assertFalse(svc.isTeacher(auth));
    }
}
