package com.example.assign2.service;

import com.example.assign2.entity.Student;
import com.example.assign2.entity.UserAccount;
import com.example.assign2.repository.StudentRepository;
import com.example.assign2.repository.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final UserAccountRepository userAccountRepository;

    public StudentService(StudentRepository studentRepository, UserAccountRepository userAccountRepository) {
        this.studentRepository = studentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public List<Student> list() {
        return studentRepository.findAll();
    }

    public Optional<Student> get(Long id) {
        return studentRepository.findById(id);
    }

    public Student create(Student student) {
        return studentRepository.save(student);
    }

    public Optional<Student> update(Long id, Student student) {
        return studentRepository.findById(id).map(existing -> {
            existing.setName(student.getName());
            existing.setEmail(student.getEmail());
            existing.setCourses(student.getCourses());
            existing.setDept(student.getDept());
            return studentRepository.save(existing);
        });
    }

    public void delete(Long id) {
        studentRepository.deleteById(id);
    }

    public Optional<Student> getByUsername(String username) {
        return userAccountRepository.findByUsername(username)
                .map(UserAccount::getStudent);
    }

    public Optional<Student> updateSelf(String username, String name, String email) {
        return getByUsername(username)
                .map(existing -> {
                    existing.setName(name);
                    existing.setEmail(email);
                    return studentRepository.save(existing);
                });
    }
}
