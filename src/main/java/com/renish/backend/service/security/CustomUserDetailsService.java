package com.renish.backend.service.security;

import com.renish.backend.entity.Student;
import com.renish.backend.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student student = studentRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("Student not found with email: "+username) );

        return User.builder()
                .username(student.getEmail())
                .password(student.getPassword())
                .roles(student.getRole())
                .build();
    }

}
