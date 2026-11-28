import java.time.LocalDate;

public abstract class LancamentoFinanceiro {

    private String nome;
    private String descricao;
    private int codigo;
    private double valor;
    private LocalDate data;
    private DiaSemana diaSemana;
    private StatusLancamento status;

    public LancamentoFinanceiro(String nome, String descricao, int codigo, double valor) {
        this(nome, descricao, codigo, valor, LocalDate.now());
    }

    public LancamentoFinanceiro(String nome, String descricao, int codigo, double valor, LocalDate data) {
        this.setNome(nome);
        this.setDescricao(descricao);
        this.setCodigo(codigo);
        this.setValor(valor);
        this.setData(data);
        this.diaSemana = DiaSemana.converterData(this.getData());
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
        if (codigo <= 0) {
            throw new IllegalArgumentException("O código do lançamento deve ser maior que zero.");
        }
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    protected void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do lançamento é obrigatória.");
        }
        this.nome = nome.trim();
    }

    public String getDescricao() {
        return descricao;
    }

    protected void setDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do lançamento é obrigatória.");
        }
        this.descricao = descricao.trim();
    }

    public double getValor() {
        return valor;
    }

    protected void setValor(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do lançamento deve ser maior que zero.");
        }
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    private void setData(LocalDate data) {
        if (data == null) {
            throw new IllegalArgumentException("A data do lançamento não pode ser nula.");
        }
        this.data = data;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public StatusLancamento getStatus() {
        return status;
    }

    public int getMes() {
        return getData().getMonthValue();
    }

    public int getAno() {
        return getData().getYear();
    }
}
