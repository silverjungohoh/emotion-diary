package com.project.emotiondiary.helper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;

@Component
public class TestDatabaseCleaner {

	@PersistenceContext
	private EntityManager entityManager;

	private List<String> tableNames;

	@PostConstruct
	public void init() {
		tableNames = entityManager.getMetamodel().getEntities().stream()
			.filter(entityType -> entityType.getJavaType().getAnnotation(Entity.class) != null)
			.map(EntityType::getName)
			.collect(Collectors.toList());
	}

	@Transactional
	public void execute() {
		entityManager.flush();
		entityManager.clear();

		entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
		for (String tableName : tableNames) {
			entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
			entityManager
				.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN " + tableName + "_id RESTART WITH 1")
				.executeUpdate();
		}
		entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
	}
}
