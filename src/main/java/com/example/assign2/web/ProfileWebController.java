package com.example.assign2.web;

import com.example.assign2.entity.Student;
import com.example.assign2.entity.Teacher;
import com.example.assign2.repository.StudentRepository;
import com.example.assign2.repository.TeacherRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileWebController {

    private final CurrentUserService currentUserService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public ProfileWebController(CurrentUserService currentUserService, StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.currentUserService = currentUserService;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        String role = currentUserService.role(authentication);
        model.addAttribute("role", role);
        model.addAttribute("displayName", currentUserService.displayName(authentication));
        if ("STUDENT".equals(role)) {
            model.addAttribute("student", currentUserService.currentStudent(authentication).orElse(null));
        }
        if ("TEACHER".equals(role)) {
            model.addAttribute("teacher", currentUserService.currentTeacher(authentication).orElse(null));
        }
        return "profile";
    }

    @PostMapping("/profile/student")
    public String updateStudent(Authentication authentication,
                                @RequestParam String name,
                                @RequestParam String email,
                                RedirectAttributes redirectAttributes) {
        Student student = currentUserService.currentStudent(authentication).orElse(null);
        if (student == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student profile not found.");
            return "redirect:/profile";
        }
        student.setName(name);
        student.setEmail(email);
        studentRepository.save(student);
        redirectAttributes.addFlashAttribute("flashMessage", "Profile updated.");
        return "redirect:/profile";
    }

    @PostMapping("/profile/teacher")
    public String updateTeacher(Authentication authentication,
                                @RequestParam String name,
                                @RequestParam String subject,
                                RedirectAttributes redirectAttributes) {
        Teacher teacher = currentUserService.currentTeacher(authentication).orElse(null);
        if (teacher == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Teacher profile not found.");
            return "redirect:/profile";
        }
        teacher.setName(name);
        teacher.setSubject(subject);
        teacherRepository.save(teacher);
        redirectAttributes.addFlashAttribute("flashMessage", "Profile updated.");
        return "redirect:/profile";
    }
}

