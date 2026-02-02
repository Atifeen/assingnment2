package com.example.assign2.controller;

import com.example.assign2.entity.Dept;
import com.example.assign2.service.DeptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/depts")
public class DeptController {
    private final DeptService deptService;

    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    @GetMapping
    public List<Dept> list() {
        return deptService.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dept> get(@PathVariable Long id) {
        Optional<Dept> d = deptService.get(id);
        return d.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Dept> create(@RequestBody Dept dept) {
        Dept saved = deptService.create(dept);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dept> update(@PathVariable Long id, @RequestBody Dept dept) {
        return deptService.update(id, dept)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!deptService.get(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        deptService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
