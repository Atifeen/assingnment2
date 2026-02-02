package com.example.assign2.web;

import com.example.assign2.entity.Course;
import com.example.assign2.entity.Teacher;
import com.example.assign2.service.CourseService;
import com.example.assign2.service.TeacherService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CourseWebController {

    private final CourseService courseService;
    private final TeacherService teacherService;
    private final CurrentUserService currentUserService;

    public CourseWebController(CourseService courseService, TeacherService teacherService, CurrentUserService currentUserService) {
        this.courseService = courseService;
        this.teacherService = teacherService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/courses")
    public String list(Authentication authentication, Model model) {
        model.addAttribute("role", currentUserService.role(authentication));
        model.addAttribute("isTeacher", currentUserService.isTeacher(authentication));
        model.addAttribute("courses", courseService.list());
        model.addAttribute("teachers", teacherService.list());
        return "courses";
    }

    @PostMapping("/courses")
    public String create(@RequestParam String name,
                         @RequestParam(required = false) Long teacherId,
                         RedirectAttributes redirectAttributes) {
        Course course = new Course();
        course.setName(name);
        if (teacherId != null) {
            Teacher teacher = teacherService.get(teacherId).orElse(null);
            course.setTeacher(teacher);
        }
        courseService.create(course);
        redirectAttributes.addFlashAttribute("flashMessage", "Course created.");
        return "redirect:/courses";
    }

    @PostMapping("/courses/{id}/update")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam(required = false) Long teacherId,
                         RedirectAttributes redirectAttributes) {
        Course course = new Course();
        course.setName(name);
        if (teacherId != null) {
            course.setTeacher(teacherService.get(teacherId).orElse(null));
        } else {
            course.setTeacher(null);
        }
        courseService.update(id, course);
        redirectAttributes.addFlashAttribute("flashMessage", "Course updated.");
        return "redirect:/courses";
    }

    @PostMapping("/courses/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        courseService.delete(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Course deleted.");
        return "redirect:/courses";
    }
}

