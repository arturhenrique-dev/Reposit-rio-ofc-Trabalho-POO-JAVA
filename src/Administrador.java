public class Administrador extends Usuario {

    private String codigoAcesso;

    public Administrador(String nome, String email, String senha, double limiteOrcamento, double saldoInicial, String codigoAcesso) {
        super(nome, email, senha, limiteOrcamento, saldoInicial);
        setCodigoAcesso(codigoAcesso);
    }

    public String getCodigoAcesso() {
        return codigoAcesso;
    }

    private void setCodigoAcesso(String codigoAcesso) {
        if (codigoAcesso == null || codigoAcesso.trim().isEmpty()) {
            throw new IllegalArgumentException("O código de acesso do administrador é obrigatório.");
        }
        this.codigoAcesso = codigoAcesso.trim();
    }
}