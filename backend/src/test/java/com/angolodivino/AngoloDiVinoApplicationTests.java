package com.angolodivino;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class AngoloDiVinoApplicationTests {
    private static final Path TEST_DATA_DIRECTORY = createTempDataDirectory();

    @DynamicPropertySource
    static void menuDataDirectory(DynamicPropertyRegistry registry) {
        registry.add("app.menu.data-directory", TEST_DATA_DIRECTORY::toString);
    }

    private static Path createTempDataDirectory() {
        try {
            return Files.createTempDirectory("application-context-test");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    void contextLoads() {
    }
}
