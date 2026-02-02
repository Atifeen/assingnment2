package com.example.assign2.web;

import com.example.assign2.entity.Dept;
import com.example.assign2.entity.Teacher;
import com.example.assign2.service.DeptService;
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
public class TeacherWebController {

    private final TeacherService teacherService;
    private final DeptService deptService;
    private final CurrentUserService currentUserService;

    public TeacherWebController(TeacherService teacherService, DeptService deptService, CurrentUserService currentUserService) {
        this.teacherService = teacherService;
        this.deptService = deptService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/teachers")
    public String list(Authentication authentication, Model model) {
        model.addAttribute("role", currentUserService.role(authentication));
        model.addAttribute("isTeacher", currentUserService.isTeacher(authentication));
        model.addAttribute("teachers", teacherService.list());
        model.addAttribute("depts", deptService.list());
        return "teachers";
    }

    @PostMapping("/teachers")
    public String create(@RequestParam String name,
                         @RequestParam String subject,
                         @RequestParam(required = false) Long deptId,
                         RedirectAttributes redirectAttributes) {
        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setSubject(subject);
        if (deptId != null) {
            Dept dept = deptService.get(deptId).orElse(null);
            teacher.setDept(dept);
        }
        teacherService.create(teacher);
        redirectAttributes.addFlashAttribute("flashMessage", "Teacher created.");
        return "redirect:/teachers";
    }

    @PostMapping("/teachers/{id}/update")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam String subject,
                         @RequestParam(required = false) Long deptId,
                         RedirectAttributes redirectAttributes) {
        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setSubject(subject);
        if (deptId != null) {
            teacher.setDept(deptService.get(deptId).orElse(null));
        } else {
            teacher.setDept(null);
        }
        teacherService.update(id, teacher);
        redirectAttributes.addFlashAttribute("flashMessage", "Teacher updated.");
        return "redirect:/teachers";
    }

    @PostMapping("/teachers/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        teacherService.delete(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Teacher deleted.");
        return "redirect:/teachers";
    }
}

