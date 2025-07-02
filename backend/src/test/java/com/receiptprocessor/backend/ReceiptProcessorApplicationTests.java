package com.receiptprocessor.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Use the test profile to avoid security configurations
class ReceiptProcessorApplicationTests {

	@Test
	void contextLoads() {
	}

}
