package com.example.assign2.web;

import com.example.assign2.entity.Dept;
import com.example.assign2.service.DeptService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DeptWebController {

    private final DeptService deptService;
    private final CurrentUserService currentUserService;

    public DeptWebController(DeptService deptService, CurrentUserService currentUserService) {
        this.deptService = deptService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/depts")
    public String list(Authentication authentication, Model model) {
        model.addAttribute("role", currentUserService.role(authentication));
        model.addAttribute("isTeacher", currentUserService.isTeacher(authentication));
        model.addAttribute("depts", deptService.list());
        return "depts";
    }

    @PostMapping("/depts")
    public String create(@RequestParam String name, RedirectAttributes redirectAttributes) {
        Dept dept = new Dept();
        dept.setName(name);
        deptService.create(dept);
        redirectAttributes.addFlashAttribute("flashMessage", "Department created.");
        return "redirect:/depts";
    }

    @PostMapping("/depts/{id}/update")
    public String update(@PathVariable Long id, @RequestParam String name, RedirectAttributes redirectAttributes) {
        Dept dept = new Dept();
        dept.setName(name);
        deptService.update(id, dept);
        redirectAttributes.addFlashAttribute("flashMessage", "Department updated.");
        return "redirect:/depts";
    }

    @PostMapping("/depts/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        deptService.delete(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Department deleted.");
        return "redirect:/depts";
    }
}

