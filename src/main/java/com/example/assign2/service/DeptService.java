package com.example.assign2.service;

import com.example.assign2.entity.Dept;
import com.example.assign2.repository.DeptRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeptService {
    private final DeptRepository deptRepository;

    public DeptService(DeptRepository deptRepository) {
        this.deptRepository = deptRepository;
    }

    public List<Dept> list() {
        return deptRepository.findAll();
    }

    public Optional<Dept> get(Long id) {
        return deptRepository.findById(id);
    }

    public Dept create(Dept dept) {
        return deptRepository.save(dept);
    }

    public Optional<Dept> update(Long id, Dept dept) {
        return deptRepository.findById(id).map(existing -> {
            existing.setName(dept.getName());
            existing.setStudents(dept.getStudents());
            existing.setTeachers(dept.getTeachers());
            return deptRepository.save(existing);
        });
    }

    public void delete(Long id) {
        deptRepository.deleteById(id);
    }
}
