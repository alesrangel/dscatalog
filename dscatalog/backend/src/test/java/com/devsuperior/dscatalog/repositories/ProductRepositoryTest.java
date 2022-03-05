package com.devsuperior.dscatalog.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.Factory.Factory;
import com.devsuperior.dscatalog.entities.Product;

@DataJpaTest
class ProductRepositoryTest {

	@Autowired
	private ProductRepository repository;
	private Long existingId;
	private long nonExistingId;
	
	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistingId = 100000L;
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		repository.deleteById(existingId);
		Optional<Product> obj = repository.findById(existingId);
		assertFalse(obj.isPresent());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdNotExists() {
		assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistingId);
		});
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		Product product = Factory.createProduct();
		product.setId(null);
		product = repository.save(product);
		assertNotNull(product.getId());
		assertEquals(26L, product.getId());
	}
	
	@Test
	public void returnOptionalNotEmptyWithIdExists() {
		Optional<Product> obj = repository.findById(existingId);
		assertTrue(obj.isPresent());
	}
	
	@Test
	public void returnOptionalEmptyWithIdNotExists() {
		Optional<Product> obj = repository.findById(nonExistingId);
		assertTrue(obj.isEmpty());
	}

}
