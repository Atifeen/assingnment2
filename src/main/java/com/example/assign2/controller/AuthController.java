package com.example.assign2.controller;

import com.example.assign2.entity.Student;
import com.example.assign2.entity.Teacher;
import com.example.assign2.entity.UserAccount;
import com.example.assign2.repository.StudentRepository;
import com.example.assign2.repository.TeacherRepository;
import com.example.assign2.repository.UserAccountRepository;
import com.example.assign2.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserAccountRepository userAccountRepository,
                          StudentRepository studentRepository,
                          TeacherRepository teacherRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userAccountRepository = userAccountRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        String username = request.username() == null ? "" : request.username().trim();
        String password = request.password() == null ? "" : request.password();
        String role = request.role() == null ? "STUDENT" : request.role().trim().toUpperCase();
        String name = request.name() == null ? "" : request.name().trim();

        if (username.isEmpty() || password.isBlank() || name.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Missing required fields."));
        }

        if (userAccountRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Username already exists."));
        }

        UserAccount account = new UserAccount();
        account.setUsername(username);
        account.setPasswordHash(passwordEncoder.encode(password));

        if ("TEACHER".equals(role)) {
            Teacher teacher = new Teacher();
            teacher.setName(name);
            teacher.setSubject(request.subject() == null ? "" : request.subject().trim());
            Teacher savedTeacher = teacherRepository.save(teacher);

            account.setRole("ROLE_TEACHER");
            account.setTeacher(savedTeacher);
        } else {
            Student student = new Student();
            student.setName(name);
            student.setEmail(request.email() == null ? "" : request.email().trim());
            Student savedStudent = studentRepository.save(student);

            account.setRole("ROLE_STUDENT");
            account.setStudent(savedStudent);
        }

        userAccountRepository.save(account);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(account.getRole()))
        );
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponse(token));
    }

    public record LoginRequest(String username, String password) {
    }

    public record SignupRequest(String username, String password, String role, String name, String email, String subject) {
    }

    public record ErrorResponse(String message) {
    }

    public record LoginResponse(String token) {
    }
}
