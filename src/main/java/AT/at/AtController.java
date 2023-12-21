package AT.at;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import AT.at.consumoExterno.ConsultarCepApi;
import AT.at.exceptions.ClienteInvalidoException;
import AT.at.models.CepResultDTO;
import AT.at.models.Cliente;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/cliente")
@Slf4j
public class AtController {

    private List<Cliente> clientes = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    private final ConsultarCepApi consultarCepApi;

    public AtController(ConsultarCepApi consultarCepApi) {
        this.consultarCepApi = consultarCepApi;
    }

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AtController.class);

    @PostMapping("/adicionar")
    public ResponseEntity<Cliente> adicionarCliente(@RequestBody Cliente clienteJson) {
        try {
            validarCliente(clienteJson);
            Cliente cliente = objectMapper.readValue(objectMapper.writeValueAsString(clienteJson), Cliente.class);
            clientes.add(cliente);

            LOGGER.info("Cliente adicionado com sucesso. Status Code da Resposta: {}", HttpStatus.CREATED.value());

            return new ResponseEntity<>(cliente, HttpStatus.CREATED);
        } catch (ClienteInvalidoException e) {
            LOGGER.error("Erro ao adicionar cliente. Status Code da Resposta: {}. Detalhes: {}",
                    HttpStatus.BAD_REQUEST.value(), e.getMensagem());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            LOGGER.error("Erro ao adicionar cliente. Status Code da Resposta: {}",
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private void validarCliente(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().isEmpty() || cliente.getEmail() == null
                || cliente.getEmail().isEmpty()) {
            throw new ClienteInvalidoException("Nome e email do cliente são obrigatórios");
        }
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes(@RequestParam(required = false) String nome,
            @RequestParam(required = false) String email) {
        try {
            List<Cliente> resultados = new ArrayList<>();
            for (Cliente cliente : clientes) {
                if ((nome == null || cliente.getNome().contains(nome))
                        && (email == null || cliente.getEmail().contains(email))) {
                    resultados.add(cliente);
                }
            }

            LOGGER.info("Listagem de clientes realizada com sucesso. Status Code da Resposta: {}", HttpStatus.OK.value());
            return new ResponseEntity<>(resultados, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Erro ao listar clientes. Status Code da Resposta: {}", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarClientes(@PathVariable Long id,
            @RequestBody Cliente clienteAtualizado) {
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            if (cliente.getId().equals(id)) {
                cliente.setNome(clienteAtualizado.getNome());
                cliente.setEmail(clienteAtualizado.getEmail());
                return new ResponseEntity<>(cliente, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCliente(@PathVariable Long id) {
        int indexToRemove = -1;
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            if (cliente.getId().equals(id)) {
                indexToRemove = i;
                break;
            }
        }
        if (indexToRemove != -1) {
            clientes.remove(indexToRemove);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/consultarCep/{cep}")
    public ResponseEntity<CepResultDTO> consultarCep(@PathVariable String cep) {
        try {
            CepResultDTO resultado = consultarCepApi.consultarCep(cep);
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Erro ao consultar CEP. Status Code da Resposta: {}. Detalhes: {}",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
