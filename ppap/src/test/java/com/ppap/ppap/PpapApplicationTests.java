package com.ppap.ppap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ppap.ppap._core.RestDocs;

@ActiveProfiles("test")
@SpringBootTest
class PpapApplicationTests extends RestDocs {

    @Test
    void contextLoads() {
    }

}
