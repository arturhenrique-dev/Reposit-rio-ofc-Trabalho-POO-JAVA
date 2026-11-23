import java.util.ArrayList;
import java.util.List;

public class Usuario implements Autenticavel {
    private String nome;
    private String email;
    private String senha;
    private List<Lancamento> lancamentos;

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.lancamentos = new ArrayList<>();
    }

    @Override
    public boolean autenticar(String senha) {
        if (senha == null || this.senha == null) {
            return false;
        }
        return this.senha.equals(senha);
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public void adicionarLancamento(Lancamento lancamento) {
        if (lancamento != null) {
            this.lancamentos.add(lancamento);
        }
    }

    public List<Lancamento> getLancamentos() {
        return lancamentos;
    }
}