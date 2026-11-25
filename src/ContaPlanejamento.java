import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
 * Conta de planejamento financeiro de um usuário.
 *
 * Papéis no diagrama:
 *  - Composição com Usuario (a conta nasce e morre com o usuário).
 *  - Agregação com LancamentoFinanceiro (coleção polimórfica de Despesa/Receita).
 *  - ArrayList  -> mantém a ordem de registro e permite iteração polimórfica.
 *  - HashMap    -> índice por código para consulta/atualização/cancelamento O(1).
 *
 * Regras de negócio protegidas aqui:
 *  RN01 - bloqueio de Despesa que ultrapasse o limite de orçamento.
 *  RN03 - lançamento CONFIRMADO só pode ser excluído depois de cancelado.
 *  RN04 - (parte do lançamento) valor deve ser maior que zero.
 *
 * Convenção adotada: calcularImpactoNoSaldo() retorna valor COM SINAL
 * (negativo para Despesa, positivo para Receita).
 */
public class ContaPlanejamento {

    private final double limiteOrcamento;
    private final double saldoDisponivel; // saldo inicial da conta
    private final List<LancamentoFinanceiro> lancamentos = new ArrayList<>();
    private final Map<Integer, LancamentoFinanceiro> indicePorCodigo = new HashMap<>();

    public ContaPlanejamento(double limiteOrcamento, double saldoDisponivel) {
        if (limiteOrcamento <= 0) {
            throw new IllegalArgumentException("O limite de orçamento deve ser maior que zero.");
        }
        if (saldoDisponivel < 0) {
            throw new IllegalArgumentException("O saldo inicial não pode ser negativo.");
        }
        this.limiteOrcamento = limiteOrcamento;
        this.saldoDisponivel = saldoDisponivel;
    }

    // ------------------------------------------------------------------
    // CRUD de lançamentos
    // ------------------------------------------------------------------

    /** Registra um lançamento (Despesa ou Receita). Aplica RN01 e RN04. */
    public void registrarLancamento(LancamentoFinanceiro lancamento) {
        if (lancamento == null) {
            throw new IllegalArgumentException("O lançamento não pode ser nulo.");
        }
        if (lancamento.getValor() <= 0) { // RN04
            throw new IllegalArgumentException("O valor do lançamento deve ser maior que zero.");
        }
        if (lancamento.getStatus() == StatusLancamento.CANCELADO) {
            throw new IllegalStateException("Não é possível registrar um lançamento já cancelado.");
        }
        if (getIndicePorCodigo().containsKey(lancamento.getCodigo())) {
            throw new IllegalArgumentException(
                    "Já existe um lançamento com o código " + lancamento.getCodigo() + ".");
        }

        // RN01: despesa nova não pode estourar o limite de orçamento
        if (lancamento instanceof Despesa despesa) {
            double totalAposRegistro = calcularTotalDespesas() + custoDe(despesa);
            if (totalAposRegistro > getLimiteOrcamento()) {
                throw new IllegalStateException(String.format(
                        "Despesa bloqueada: o total de despesas ativas (R$ %.2f) ultrapassaria o limite de R$ %.2f.",
                        totalAposRegistro, getLimiteOrcamento()));
            }
        }

        getLancamentosMutaveis().add(lancamento);
        getIndicePorCodigo().put(lancamento.getCodigo(), lancamento);
    }

    /** Consulta rápida pelo índice (HashMap). */
    public Optional<LancamentoFinanceiro> buscarPorCodigo(int codigo) {
        return Optional.ofNullable(getIndicePorCodigo().get(codigo));
    }

    /**
     * Atualiza nome, descrição e valor de um lançamento.
     * RN02: lançamento CANCELADO não pode ser alterado.
     * RN01: se for Despesa, o novo valor também não pode estourar o limite.
     */
    public void atualizarLancamento(int codigo, String nome, String descricao, double valor) {
        LancamentoFinanceiro lancamento = obterOuFalhar(codigo);

        if (lancamento.getStatus() == StatusLancamento.CANCELADO) { // RN02
            throw new IllegalStateException("Lançamento cancelado não pode ser alterado.");
        }
        if (valor <= 0) { // RN04
            throw new IllegalArgumentException("O valor do lançamento deve ser maior que zero.");
        }

        String nomeAnterior = lancamento.getNome();
        String descricaoAnterior = lancamento.getDescricao();
        double valorAnterior = lancamento.getValor();

        try {
            lancamento.setNome(nome);
            lancamento.setDescricao(descricao);
            lancamento.setValor(valor);

            if (lancamento instanceof Despesa && calcularTotalDespesas() > getLimiteOrcamento()) {
                throw new IllegalStateException(
                        "Atualização bloqueada: o novo valor ultrapassaria o limite de orçamento.");
            }
        } catch (RuntimeException e) {
            lancamento.setNome(nomeAnterior);
            lancamento.setDescricao(descricaoAnterior);
            lancamento.setValor(valorAnterior);
            throw e;
        }
    }

    /** Confirma um lançamento pendente via conta. */
    public void confirmarLancamento(int codigo) {
        LancamentoFinanceiro lancamento = obterOuFalhar(codigo);
        lancamento.confirmar();
    }

    /** Cancela um lançamento (PENDENTE ou CONFIRMADO -> CANCELADO). */
    public void cancelarLancamento(int codigo) {
        LancamentoFinanceiro lancamento = obterOuFalhar(codigo);
        if (lancamento.getStatus() == StatusLancamento.CANCELADO) {
            throw new IllegalStateException("O lançamento " + codigo + " já está cancelado.");
        }
        lancamento.cancelar();
    }

    /** RN03: lançamento CONFIRMADO não pode ser excluído sem antes ser cancelado. */
    public void excluirLancamento(int codigo) {
        LancamentoFinanceiro lancamento = obterOuFalhar(codigo);
        if (lancamento.getStatus() == StatusLancamento.CONFIRMADO) {
            throw new IllegalStateException(
                    "Lançamento confirmado não pode ser excluído. Cancele-o antes de excluir.");
        }
        getLancamentosMutaveis().remove(lancamento);
        getIndicePorCodigo().remove(codigo);
    }

    /** Cópia imutável da lista, para ninguém alterar a coleção por fora. */
    public List<LancamentoFinanceiro> getLancamentos() {
        return Collections.unmodifiableList(getLancamentosMutaveis());
    }

    // ------------------------------------------------------------------
    // Cálculos
    // ------------------------------------------------------------------

    /** Soma das despesas ativas (PENDENTE + CONFIRMADO), já com taxa adicional. */
    public double calcularTotalDespesas() {
        double total = 0;
        for (LancamentoFinanceiro l : getLancamentos()) {
            if (l instanceof Despesa d && d.getStatus() != StatusLancamento.CANCELADO) {
                total += custoDe(d);
            }
        }
        return total;
    }

    /** Saldo inicial + impacto (polimórfico) de todos os lançamentos CONFIRMADOS. */
    public double calcularSaldoAtual() {
        double saldo = getSaldoDisponivel();
        for (LancamentoFinanceiro l : getLancamentos()) {
            if (l.getStatus() == StatusLancamento.CONFIRMADO) {
                saldo += l.calcularImpactoNoSaldo();
            }
        }
        return saldo;
    }

    // ------------------------------------------------------------------
    // Getters e auxiliares internos
    // ------------------------------------------------------------------

    public double getLimiteOrcamento() {
        return limiteOrcamento;
    }

    public double getSaldoDisponivel() {
        return saldoDisponivel;
    }

    private List<LancamentoFinanceiro> getLancamentosMutaveis() {
        return lancamentos;
    }

    private Map<Integer, LancamentoFinanceiro> getIndicePorCodigo() {
        return indicePorCodigo;
    }

    private LancamentoFinanceiro obterOuFalhar(int codigo) {
        LancamentoFinanceiro lancamento = getIndicePorCodigo().get(codigo);
        if (lancamento == null) {
            throw new IllegalArgumentException("Lançamento de código " + codigo + " não encontrado.");
        }
        return lancamento;
    }

    /** Custo positivo da despesa (valor + taxa), independente do sinal do impacto. */
    private double custoDe(Despesa despesa) {
        return Math.abs(despesa.calcularImpactoNoSaldo());
    }
}