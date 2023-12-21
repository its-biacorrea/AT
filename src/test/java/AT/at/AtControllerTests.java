package AT.at;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import AT.at.models.Cliente;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
class AtControllerTests {

	private MockMvc mockMvc;
	private List<Cliente> clientes = new ArrayList<>();
	private static final Logger LOGGER = LoggerFactory.getLogger(AtControllerTests.class);
	
	@Test
	public void testAdicionarClienteComJsonInvalido() {
	    String clienteJsonInvalido = "{ \"nome\": \"Novo Cliente\", \"email\": \"novo@cliente.com\", \"id\": 1 }";

	    assertThrows(HttpMessageNotReadableException.class, () -> {
	        mockMvc.perform(MockMvcRequestBuilders.post("/api/cliente/adicionar")
	                .content(clienteJsonInvalido)
	                .contentType(MediaType.APPLICATION_JSON));
	    });
	}
	
	@Test
	public void testAtualizarClienteComIdInexistente() {
	    Cliente clienteAtualizado = new Cliente();
	    clienteAtualizado.setNome("Novo Nome");
	    clienteAtualizado.setEmail("novo@nome.com");

	    assertThrows(ResponseStatusException.class, () -> {
	        mockMvc.perform(MockMvcRequestBuilders.put("/api/cliente/100")
	                .content(asJsonString(clienteAtualizado))
	                .contentType(MediaType.APPLICATION_JSON));
	    });
	}
	
	@Test
	public void testAtualizarClienteComSucesso() throws Exception {
	    Cliente clienteExistente = new Cliente();
	    clienteExistente.setId(1L);
	    clienteExistente.setNome("Cliente Existente");
	    clienteExistente.setEmail("existente@cliente.com");
	    clientes.add(clienteExistente);

	    Cliente clienteAtualizado = new Cliente();
	    clienteAtualizado.setNome("Novo Nome");
	    clienteAtualizado.setEmail("novo@nome.com");

	    mockMvc.perform(MockMvcRequestBuilders.put("/api/cliente/1")
	            .content(asJsonString(clienteAtualizado))
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(result -> {
	                int statusCode = result.getResponse().getStatus();
	                LOGGER.info("Status Code da Resposta: {}", statusCode);
	            })
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Novo Nome"))
	            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("novo@nome.com"));
	}

	@Test
	public void testExcluirClienteComSucesso() throws Exception {
	    Cliente clienteExistente = new Cliente();
	    clienteExistente.setId(1L);
	    clienteExistente.setNome("Cliente Existente");
	    clienteExistente.setEmail("existente@cliente.com");
	    clientes.add(clienteExistente);

	    mockMvc.perform(MockMvcRequestBuilders.delete("/api/cliente/1"))
        .andExpect(result -> {
            int statusCode = result.getResponse().getStatus();
            LOGGER.info("Status Code da Resposta: {}", statusCode);
        })
        .andExpect(MockMvcResultMatchers.status().isNoContent());

	    assertTrue(clientes.isEmpty());
	}

	@Test
	public void testConsultarCepComSucesso() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/cliente/consultarCep/12345678"))
        .andExpect(result -> {
            int statusCode = result.getResponse().getStatus();
            LOGGER.info("Status Code da Resposta: {}", statusCode);
        })
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.cep").value("12345678"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.logradouro").exists());
	}

	private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


