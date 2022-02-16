package com.devsuperior.dscatalog.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.Factory.Factory;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductResourceIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ProductRepository repository;

	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;

	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistingId = 10000L;
		countTotalProducts = 25L;
	}

	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {

		ResultActions result = mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
		result.andExpect(jsonPath("$.content").exists());
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
	}

	@Test
	public void findByIdShouldReturnProductDTO() throws Exception {

		ResultActions result = mockMvc.perform(get("/products/{id}", existingId).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.name").value("The Lord of the Rings"));
	}

	@Test
	public void inputShouldReturnProductDTO() throws Exception {
		ProductDTO productDTO = Factory.createProductDTO();

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc.perform(post("/products").content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		result.andExpect(jsonPath("$.price").exists());
		result.andExpect(jsonPath("$.imgUrl").exists());
	}

	@Test
	public void updateShoudReturnProductDTOwhenIdExists() throws Exception {
		ProductDTO productDTO = Factory.createProductDTO();

		String expectedName = productDTO.getName();
		String expectedDescription = productDTO.getDescription();

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc.perform(put("/products/{id}", existingId).content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(existingId));
		result.andExpect(jsonPath("$.name").value(expectedName));
		result.andExpect(jsonPath("$.description").value(expectedDescription));

	}

	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ProductDTO productDTO = Factory.createProductDTO();
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/product/{id}", nonExistingId).content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}

	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		
		ResultActions result = mockMvc.perform(delete("/products/{id}", existingId).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		assertEquals(countTotalProducts-1, repository.count());
		result.andExpect(status().isNoContent());
	}
}
