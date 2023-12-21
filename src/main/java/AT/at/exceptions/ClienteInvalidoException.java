package AT.at.exceptions;

public class ClienteInvalidoException extends RuntimeException {
    private final String mensagem;

    public ClienteInvalidoException(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }
}