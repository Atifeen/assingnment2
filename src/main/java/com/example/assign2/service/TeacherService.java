package com.example.assign2.service;

import com.example.assign2.entity.Teacher;
import com.example.assign2.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {
    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<Teacher> list() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> get(Long id) {
        return teacherRepository.findById(id);
    }

    public Teacher create(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public Optional<Teacher> update(Long id, Teacher teacher) {
        return teacherRepository.findById(id).map(existing -> {
            existing.setName(teacher.getName());
            existing.setSubject(teacher.getSubject());
            existing.setDept(teacher.getDept());
            return teacherRepository.save(existing);
        });
    }

    public void delete(Long id) {
        teacherRepository.deleteById(id);
    }
}
