package com.example.assign2.service;

import com.example.assign2.entity.Course;
import com.example.assign2.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> list() {
        return courseRepository.findAll();
    }

    public Optional<Course> get(Long id) {
        return courseRepository.findById(id);
    }

    public Course create(Course course) {
        return courseRepository.save(course);
    }

    public Optional<Course> update(Long id, Course course) {
        return courseRepository.findById(id).map(existing -> {
            existing.setName(course.getName());
            existing.setTeacher(course.getTeacher());
            existing.setStudents(course.getStudents());
            return courseRepository.save(existing);
        });
    }

    public void delete(Long id) {
        courseRepository.deleteById(id);
    }
}
