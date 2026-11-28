public class Despesa extends LancamentoFinanceiro {

    private String categoria;
    private double taxaAdicional;

    public Despesa(String nome, String descricao, int codigo, double valor, String categoria, double taxaAdicional) {
        super(nome, descricao, codigo, valor);
        this.setCategoria(categoria);
        this.setTaxaAdicional(taxaAdicional);
    }

    @Override
    public double calcularImpactoNoSaldo() {
        if (this.getStatus() == null) {
            throw new IllegalArgumentException("O status da despesa não pode ser nulo.");
        } else if (this.getStatus() == StatusLancamento.CANCELADO) {
            return 0;
        }
        return -(this.getValor() + this.getTaxaAdicional());
    }

    public void aplicarTaxa(double taxa) {
        if (taxa < 0) {
            throw new IllegalArgumentException("A taxa deve maior que zero.");
        } else if (this.getStatus() == StatusLancamento.CANCELADO) {
            throw new IllegalStateException("Não é possível aplicar taxa, pois a despesa está CANCELADA.");
        }
        this.setTaxaAdicional(taxa);
    }

    @Override
    public String descrever() {
        return super.descrever() + " Categoria: " + this.getCategoria() + " Taxa: " + this.getTaxaAdicional();
    }

    public String getCategoria() {
        return categoria;
    }

    private void setCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("A categoria da despesa não pode ser vazia.");
        } else if (this.getStatus() == StatusLancamento.CANCELADO) {
            throw new IllegalStateException("Não é possível alterar a categoria de uma despesa CANCELADA.");
        }
        this.categoria = categoria.trim();
    }

    public double getTaxaAdicional() {
        return taxaAdicional;
    }

    private void setTaxaAdicional(double taxaAdicional) {
        if (taxaAdicional < 0) {
            throw new IllegalArgumentException("A taxa deve ser maior que zero.");
        }
        this.taxaAdicional = taxaAdicional;
    }
}
