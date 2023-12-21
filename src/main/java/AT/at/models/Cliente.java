package AT.at.models;
import lombok.Data;

@Data
public class Cliente {
	private Long id;
	private String nome;
	private String[] produtos;
	private String email;
	
	public Cliente() {
	
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String[] getProdutos() {
		return produtos;
	}
	public void setProdutos(String[] produtos) {
		this.produtos = produtos;
	}
}
