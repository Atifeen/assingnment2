package com.example.assign2.web;

import com.example.assign2.entity.Student;
import com.example.assign2.entity.Teacher;
import com.example.assign2.entity.UserAccount;
import com.example.assign2.repository.StudentRepository;
import com.example.assign2.repository.TeacherRepository;
import com.example.assign2.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SignupWebController {

    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupWebController(UserAccountRepository userAccountRepository,
                               StudentRepository studentRepository,
                               TeacherRepository teacherRepository,
                               PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("defaultRole", "STUDENT");
        return "signup";
    }

    @PostMapping("/signup")
    public String createAccount(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String role,
                                @RequestParam String name,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String subject,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        String normalizedUsername = username == null ? "" : username.trim();
        String normalizedRole = role == null ? "" : role.trim().toUpperCase();

        if (normalizedUsername.isEmpty() || password == null || password.isBlank() || name == null || name.isBlank()) {
            model.addAttribute("errorMessage", "Please fill all required fields.");
            model.addAttribute("defaultRole", normalizedRole.isEmpty() ? "STUDENT" : normalizedRole);
            return "signup";
        }

        if (userAccountRepository.findByUsername(normalizedUsername).isPresent()) {
            model.addAttribute("errorMessage", "Username already exists.");
            model.addAttribute("defaultRole", normalizedRole.isEmpty() ? "STUDENT" : normalizedRole);
            return "signup";
        }

        UserAccount account = new UserAccount();
        account.setUsername(normalizedUsername);
        account.setPasswordHash(passwordEncoder.encode(password));

        if ("TEACHER".equals(normalizedRole)) {
            Teacher teacher = new Teacher();
            teacher.setName(name.trim());
            teacher.setSubject(subject == null ? "" : subject.trim());
            Teacher savedTeacher = teacherRepository.save(teacher);

            account.setRole("ROLE_TEACHER");
            account.setTeacher(savedTeacher);
        } else {
            Student student = new Student();
            student.setName(name.trim());
            student.setEmail(email == null ? "" : email.trim());
            Student savedStudent = studentRepository.save(student);

            account.setRole("ROLE_STUDENT");
            account.setStudent(savedStudent);
        }

        userAccountRepository.save(account);
        redirectAttributes.addFlashAttribute("flashMessage", "Account created. Please login.");
        return "redirect:/login";
    }
}

