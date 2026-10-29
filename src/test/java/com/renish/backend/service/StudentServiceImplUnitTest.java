package com.renish.backend.service;


import com.renish.backend.dto.response.StudentResponse;
import com.renish.backend.entity.Address;
import com.renish.backend.entity.Department;
import com.renish.backend.entity.Student;
import com.renish.backend.exception.FileUploadException;
import com.renish.backend.exception.ResourceNotFoundException;
import com.renish.backend.repository.StudentRepository;
import com.renish.backend.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplUnitTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private StudentServiceImpl studentService;

    private static final MultipartFile PNG_FILE =
            new MockMultipartFile("file", "photo.png", "image/png", "png-data".getBytes());

    @Test
    void shouldReturnStudentById(){

        // Arrange
        Student student = new Student();
        student.setId(10L);
        student.setFirstName("Usha");
        student.setLastName("Yadav");
        student.setEmail("usha@gmail.com");

        Address address= new Address();
        address.setCountry("Nepal");
        address.setCity("Kathmandu");
        address.setState("Bagmati");
        student.setAddress(address);

        Department department = new Department();
        department.setDepartmentName("Computer science");

        student.setCourses(List.of());
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        // Act
        StudentResponse result = studentService.getStudentById(10L);


        // Assert
        assertEquals(10L,result.getId());
        assertEquals("Usha Yadav",result.getName());
        assertEquals("usha@gmail.com",result.getEmail());
        verify(studentRepository,times(1)).findById(10L);


//        studentRepository.delete(student);
        verify(studentRepository,never()).delete(student);

    }

    @Test
    void shouldThrowExceptionWhenRepositoryFails(){
        // Arrange
        when(studentRepository.findById(10L)).thenThrow(new RuntimeException());

        // Act
        RuntimeException exception = assertThrows(
                RuntimeException.class,()->{
                    studentService.getStudentById(10L);
                }
        );
//        studentService.getStudentById(10L);


    }

    @Test
    void shouldThrowExceptionWhenStudentNotFound(){
        // Arrange
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
               ResourceNotFoundException.class,()->studentService.getStudentById(99L)
        );

    }

    private Student studentWithImage(String fileName) {
        Student student = new Student();
        student.setId(10L);
        student.setFirstName("Usha");
        student.setLastName("Yadav");
        student.setEmail("usha@gmail.com");
        student.setCourses(List.of());
        student.setProfileImage(fileName);
        return student;
    }

    @Test
    void shouldDeletePreviousImageWhenProfileImageIsReplaced() throws IOException {
        // Arrange
        Student student = studentWithImage("old.png");
        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        when(fileStorageService.storeFile(any())).thenReturn("new.png");
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        StudentResponse response = studentService.uploadProfileImage(10L, PNG_FILE);

        // Assert
        assertEquals("new.png", response.getProfileImage());
        assertEquals("new.png", student.getProfileImage());
        verify(fileStorageService).deleteFile("old.png");
        verify(fileStorageService, never()).deleteFile("new.png");
    }

    @Test
    void shouldKeepStoredFileWhenStudentHasNoPreviousImage() throws IOException {
        // Arrange
        Student student = studentWithImage(null);
        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        when(fileStorageService.storeFile(any())).thenReturn("first.png");
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        StudentResponse response = studentService.uploadProfileImage(10L, PNG_FILE);

        // Assert
        assertEquals("first.png", response.getProfileImage());
        verify(fileStorageService, never()).deleteFile(anyString());
    }

    @Test
    void shouldNotSaveStudentWhenFileStoreFails() throws IOException {
        // Arrange
        Student student = studentWithImage("old.png");
        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        when(fileStorageService.storeFile(any())).thenThrow(new IOException("disk full"));

        // Act & Assert
        assertThrows(FileUploadException.class,
                () -> studentService.uploadProfileImage(10L, PNG_FILE));

        verify(studentRepository, never()).save(any(Student.class));
        verify(fileStorageService, never()).deleteFile(anyString());
    }

    @Test
    void shouldDeleteStoredFileWhenSavingStudentFails() throws IOException {
        // Arrange
        Student student = studentWithImage("old.png");
        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        when(fileStorageService.storeFile(any())).thenReturn("new.png");
        when(studentRepository.save(any(Student.class))).thenThrow(new RuntimeException("db down"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> studentService.uploadProfileImage(10L, PNG_FILE));

        verify(fileStorageService).deleteFile("new.png");
        verify(fileStorageService, never()).deleteFile("old.png");
    }

    @Test
    void shouldRejectUnsupportedImageType() throws IOException {
        // Arrange
        Student student = studentWithImage("old.png");
        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        MultipartFile gif = new MockMultipartFile("file", "anim.gif", "image/gif", "gif-data".getBytes());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> studentService.uploadProfileImage(10L, gif));

        verify(fileStorageService, never()).storeFile(any());
    }

}
