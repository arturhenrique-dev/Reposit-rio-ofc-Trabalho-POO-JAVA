public class Administrador extends Usuario {

    private final String codigoAcesso;

    public Administrador(String nome, String email, String senha, double limiteOrcamento, double saldoInicial, String codigoAcesso) {
        super(nome, email, senha, limiteOrcamento, saldoInicial);
        if (codigoAcesso == null || codigoAcesso.trim().isEmpty()) {
            throw new IllegalArgumentException("O código de acesso é obrigatório.");
        }
        this.codigoAcesso = codigoAcesso.trim();
    }

    public String getCodigoAcesso() {
        return codigoAcesso;
    }
}