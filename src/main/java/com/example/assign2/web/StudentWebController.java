package com.example.assign2.web;

import com.example.assign2.entity.Course;
import com.example.assign2.entity.Dept;
import com.example.assign2.entity.Student;
import com.example.assign2.repository.CourseRepository;
import com.example.assign2.repository.DeptRepository;
import com.example.assign2.repository.StudentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class StudentWebController {

    private final StudentRepository studentRepository;
    private final DeptRepository deptRepository;
    private final CourseRepository courseRepository;
    private final CurrentUserService currentUserService;

    public StudentWebController(StudentRepository studentRepository,
                                DeptRepository deptRepository,
                                CourseRepository courseRepository,
                                CurrentUserService currentUserService) {
        this.studentRepository = studentRepository;
        this.deptRepository = deptRepository;
        this.courseRepository = courseRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/students")
    public String list(Authentication authentication, Model model) {
        model.addAttribute("role", currentUserService.role(authentication));
        model.addAttribute("isTeacher", currentUserService.isTeacher(authentication));
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("depts", deptRepository.findAll());
        model.addAttribute("courses", courseRepository.findAll());
        return "students";
    }

    @PostMapping("/students")
    public String create(@RequestParam String name,
                         @RequestParam String email,
                         @RequestParam(required = false) Long deptId,
                         @RequestParam(required = false) List<Long> courseIds,
                         RedirectAttributes redirectAttributes) {
        Student student = new Student();
        student.setName(name);
        student.setEmail(email);

        if (deptId != null) {
            Dept dept = deptRepository.findById(deptId).orElse(null);
            student.setDept(dept);
        }

        if (courseIds != null && !courseIds.isEmpty()) {
            Set<Course> courses = new HashSet<>(courseRepository.findAllById(courseIds));
            student.setCourses(courses);
        }

        studentRepository.save(student);
        redirectAttributes.addFlashAttribute("flashMessage", "Student created.");
        return "redirect:/students";
    }

    @PostMapping("/students/{id}/update")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam String email,
                         @RequestParam(required = false) Long deptId,
                         RedirectAttributes redirectAttributes) {
        studentRepository.findById(id).ifPresent(existing -> {
            existing.setName(name);
            existing.setEmail(email);
            if (deptId != null) {
                existing.setDept(deptRepository.findById(deptId).orElse(null));
            } else {
                existing.setDept(null);
            }
            studentRepository.save(existing);
        });
        redirectAttributes.addFlashAttribute("flashMessage", "Student updated.");
        return "redirect:/students";
    }

    @PostMapping("/students/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        studentRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Student deleted.");
        return "redirect:/students";
    }
}

