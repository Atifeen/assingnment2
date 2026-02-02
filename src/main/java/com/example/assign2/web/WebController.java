package com.example.assign2.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    private final CurrentUserService currentUserService;
    private final com.example.assign2.repository.DeptRepository deptRepository;
    private final com.example.assign2.repository.TeacherRepository teacherRepository;
    private final com.example.assign2.repository.CourseRepository courseRepository;
    private final com.example.assign2.repository.StudentRepository studentRepository;

    public WebController(CurrentUserService currentUserService,
                         com.example.assign2.repository.DeptRepository deptRepository,
                         com.example.assign2.repository.TeacherRepository teacherRepository,
                         com.example.assign2.repository.CourseRepository courseRepository,
                         com.example.assign2.repository.StudentRepository studentRepository) {
        this.currentUserService = currentUserService;
        this.deptRepository = deptRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/")
    public String root(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("role", currentUserService.role(authentication));
        model.addAttribute("displayName", currentUserService.displayName(authentication));
        model.addAttribute("subtitle", "Dashboard");
        model.addAttribute("deptCount", deptRepository.count());
        model.addAttribute("teacherCount", teacherRepository.count());
        model.addAttribute("courseCount", courseRepository.count());
        model.addAttribute("studentCount", studentRepository.count());
        return "dashboard";
    }
}
