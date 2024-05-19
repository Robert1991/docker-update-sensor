package com.robertnator.docker.update.sensor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ApplicationTests {

    @Mock
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
