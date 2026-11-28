import java.util.Objects;

public class Usuario implements Autenticavel {
    private String nome;
    private String email;
    private String senha;
    private ContaPlanejamento conta;

    public Usuario(String nome, String email, String senha, double limiteOrcamento, double saldoInicial) {
        setNome(nome);
        setEmail(email);
        setSenha(senha);
        this.conta = new ContaPlanejamento(limiteOrcamento, saldoInicial);
    }

    @Override
    public boolean autenticar(String senha) {
        if (senha == null || this.getSenha() == null) {
            return false;
        }
        return Objects.equals(this.getSenha(), senha);
    }

    public String getNome() {
        return nome;
    }

    private void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do usuário é obrigatório.");
        }
        this.nome = nome.trim();
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("O email do usuário é obrigatório.");
        }
        this.email = email.trim();
    }

    public String getSenha() {
        return senha;
    }

    private void setSenha(String senha) {
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha do usuário é obrigatória.");
        }
        this.senha = senha;
    }

    public ContaPlanejamento getConta() {
        return conta;
    }
}