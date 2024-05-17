package com.robertnator.docker.update.sensor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.verify;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ApplicationTests {

    @MockBean
    private ApplicationRunner applicationRunner;

    @BeforeEach
    void setUp() {
        Application.runner = applicationRunner;
    }

    @Test
    void testMain() {
        Application.main(new String[]{});

        verify(applicationRunner).run(new String[]{});
    }
}
