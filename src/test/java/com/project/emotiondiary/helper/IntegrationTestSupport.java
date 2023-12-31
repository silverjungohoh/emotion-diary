package com.project.emotiondiary.helper;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.project.emotiondiary.global.config.EmbeddedRedisConfig;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = EmbeddedRedisConfig.class)
public abstract class IntegrationTestSupport {

	@Autowired
	protected TestDatabaseCleaner databaseCleaner;

	@AfterEach
	void tearDown() {
		databaseCleaner.execute();
	}
}
