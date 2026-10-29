package com.renish.backend;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JUnitDemoTest {

    @Test
    void shouldReturnExpectedResult(){

        // Arrange
        Long expected = 5L;

        // Act
        Long actual = 5L;

        // Assert
        assertEquals(expected,actual);
    }
}
