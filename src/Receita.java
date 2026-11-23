public class Receita extends LancamentoFinanceiro {

    private String origem;
    private String frequencia;

    public Receita(String nome, String descricao, int codigo, double valor, String origem, String frequencia) {
        super(nome, descricao, codigo, valor);
        this.origem = origem;
        this.frequencia = frequencia;
    }

    @Override
    public double calcularImpactoNoSaldo() {
        if (this.getStatus() == null) {
            throw new IllegalArgumentException("O status da receita não pode ser nulo.");
        } else if (this.getStatus() == StatusLancamento.CANCELADO) {
            return 0;
        }
        return this.getValor();
    }

    @Override
    public String descrever() {
        return super.descrever() + " Origem: " + this.getOrigem() + " Frequência: " + this.getFrequencia();
    }

    public String getOrigem() {
        return origem;
    }

    private void setOrigem(String origem) {
        if (origem == null || origem.trim().isEmpty()) {
            throw new IllegalArgumentException("A origem da receita é obrigatória.");
        }
        this.origem = origem.trim();
    }

    public String getFrequencia() {
        return frequencia;
    }

    private void setFrequencia(String frequencia) {
        if (frequencia == null || frequencia.trim().isEmpty()) {
            throw new IllegalArgumentException("A frequência da receita é obrigatória.");
        }
        this.frequencia = frequencia.trim();
    }
}
