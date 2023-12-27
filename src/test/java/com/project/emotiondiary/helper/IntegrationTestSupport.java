package com.project.emotiondiary.helper;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestSupport {

	@Autowired
	protected TestDatabaseCleaner databaseCleaner;

	@AfterEach
	void tearDown() {
		databaseCleaner.execute();
	}
}
