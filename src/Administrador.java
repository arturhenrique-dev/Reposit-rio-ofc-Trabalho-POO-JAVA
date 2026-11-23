public class Administrador extends Usuario implements Autenticavel {
    private String codigoAcesso;

    public Administrador(String nome, String email, String senha, String codigoAcesso) {
        super(nome, email, senha);
        this.codigoAcesso = codigoAcesso;
    }

    public String getCodigoAcesso() {
        return codigoAcesso;
    }

    @Override
    public boolean autenticar(String senha) {
        if (senha == null) {
            return false;
        }
        return super.autenticar(senha);
    }
}