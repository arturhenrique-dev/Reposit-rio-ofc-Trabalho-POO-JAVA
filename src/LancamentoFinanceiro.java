import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class LancamentoFinanceiro {

    protected String nome;
    protected String descricao;
    protected int codigo;
    protected double valor;
    protected LocalDate data;
    protected DiaSemana diaSemana;
    protected StatusLancamento status;
    
    public LancamentoFinanceiro(String nome, String descricao, int codigo, double valor) {
        this.setNome(nome);
        this.setDescricao(descricao);
        this.setCodigo(codigo);
        this.setValor(valor);
        this.data = LocalDate.now();
        this.diaSemana = DiaSemana.converterData(this.data);
        this.status = StatusLancamento.PENDENTE;
    }

    public abstract double calcularImpactoNoSaldo();

    public void confirmar() {
        if (this.getStatus() == null) {
            throw new IllegalArgumentException("O status do lançamento não pode ser nulo.");
        } else if (this.getStatus() == StatusLancamento.CANCELADO) {
            throw new IllegalStateException("Um lançamento CANCELADO não pode ser confirmado.");
        }
        this.status = StatusLancamento.CONFIRMADO;
    }

    public void cancelar() {
        if (this.getStatus() == null) {
            throw new IllegalArgumentException("O status do lançamento não pode ser nulo.");
        } else if (this.getStatus() == StatusLancamento.CANCELADO) {
            throw new IllegalStateException("Este lançamento já está CANCELADO.");
        }
        this.status = StatusLancamento.CANCELADO;
    }

    public String descrever() {
        return "Nome: " + this.getNome() + " Descrição: " + this.getDescricao() + " Id: " + this.getCodigo() + " Valor: " + this.getValor() + " Data: " + this.getData() + " | "
                + this.getDiaSemana() + " Status: " + this.getStatus();
    }

    public int getCodigo() {
        return codigo;
    }

    private void setCodigo(int codigo) {
        if (codigo < 0) {
            throw new IllegalArgumentException("O código do lançamento deve ser maior que zero.");
        }
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    private void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do lançamento é obrigatória.");
        }
        this.nome = nome.trim();
    }

    public String getDescricao() {
        return descricao;
    }

    private void setDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do lançamento é obrigatória.");
        }
        this.descricao = descricao.trim();
    }

    public double getValor() {
        return valor;
    }

    private void setValor(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do lançamento deve ser maior que zero.");
        }
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public StatusLancamento getStatus() {
        return status;
    }
}
