package com.example.assign2.config;

import com.example.assign2.entity.Student;
import com.example.assign2.entity.Teacher;
import com.example.assign2.entity.UserAccount;
import com.example.assign2.repository.StudentRepository;
import com.example.assign2.repository.TeacherRepository;
import com.example.assign2.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedUsers(
            UserAccountRepository userAccountRepository,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userAccountRepository.findByUsername("alice").isEmpty()) {
                Student student = new Student();
                student.setName("Alice");
                student.setEmail("alice@example.com");
                Student savedStudent = studentRepository.save(student);

                UserAccount account = new UserAccount();
                account.setUsername("alice");
                account.setPasswordHash(passwordEncoder.encode("password"));
                account.setRole("ROLE_STUDENT");
                account.setStudent(savedStudent);
                userAccountRepository.save(account);
            }

            if (userAccountRepository.findByUsername("bob").isEmpty()) {
                Teacher teacher = new Teacher();
                teacher.setName("Bob");
                teacher.setSubject("General");
                Teacher savedTeacher = teacherRepository.save(teacher);

                UserAccount account = new UserAccount();
                account.setUsername("bob");
                account.setPasswordHash(passwordEncoder.encode("password"));
                account.setRole("ROLE_TEACHER");
                account.setTeacher(savedTeacher);
                userAccountRepository.save(account);
            }
        };
    }
}

