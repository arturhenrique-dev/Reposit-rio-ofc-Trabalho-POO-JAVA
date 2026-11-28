import java.text.NumberFormat;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * Gera relatórios de leitura sobre uma ContaPlanejamento.
 * Não altera a conta: apenas consulta e imprime.
 */
public class RelatorioFinanceiro {

    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final ContaPlanejamento conta;

    public RelatorioFinanceiro(ContaPlanejamento conta) {
        if (conta == null) {
            throw new IllegalArgumentException("A conta não pode ser nula.");
        }
        this.conta = conta;
    }

    public ContaPlanejamento getConta() {
        return conta;
    }

    /** Quantidade e valor total (impacto absoluto) de lançamentos em cada status. */
    public void exibirResumoPorStatus() {
        Map<StatusLancamento, Integer> quantidades = new EnumMap<>(StatusLancamento.class);
        Map<StatusLancamento, Double> totais = new EnumMap<>(StatusLancamento.class);
        for (StatusLancamento s : StatusLancamento.values()) {
            quantidades.put(s, 0);
            totais.put(s, 0.0);
        }

        for (LancamentoFinanceiro l : getConta().getLancamentos()) {
            quantidades.merge(l.getStatus(), 1, Integer::sum);
            totais.merge(l.getStatus(), Math.abs(l.calcularImpactoNoSaldo()), Double::sum);
        }

        System.out.println("\n===== RESUMO POR STATUS =====");
        for (StatusLancamento s : StatusLancamento.values()) {
            System.out.printf("%-12s | %3d lançamento(s) | %s%n",
                    s.getDescricao(), quantidades.get(s), MOEDA.format(totais.get(s)));
        }
    }

    /** Total de despesas ativas (não canceladas) agrupado por dia da semana. */
    public void exibirGastosPorDiaSemana() {
        Map<DiaSemana, Double> gastos = new EnumMap<>(DiaSemana.class);
        for (DiaSemana d : DiaSemana.values()) {
            gastos.put(d, 0.0);
        }

        for (LancamentoFinanceiro l : getConta().getLancamentos()) {
            if (l instanceof Despesa && l.getStatus() != StatusLancamento.CANCELADO) {
                gastos.merge(l.getDiaSemana(), Math.abs(l.calcularImpactoNoSaldo()), Double::sum);
            }
        }

        System.out.println("\n===== GASTOS POR DIA DA SEMANA =====");
        for (DiaSemana d : DiaSemana.values()) {
            System.out.printf("%-15s | %s%n", d.getDescricao(), MOEDA.format(gastos.get(d)));
        }
    }

    /** Resumo financeiro referente a um mês e ano específicos. */
    public void exibirResumoMensal(int mes, int ano) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("O mês deve estar entre 1 e 12.");
        }
        if (ano <= 0) {
            throw new IllegalArgumentException("O ano deve ser maior que zero.");
        }

        double receitasConfirmadas = 0;
        double despesasConfirmadas = 0;

        for (LancamentoFinanceiro l : getConta().getLancamentos()) {
            if (l.getStatus() == StatusLancamento.CONFIRMADO
                    && l.getMes() == mes
                    && l.getAno() == ano) {
                double impacto = l.calcularImpactoNoSaldo();
                if (l instanceof Receita) {
                    receitasConfirmadas += impacto;
                } else if (l instanceof Despesa) {
                    despesasConfirmadas += Math.abs(impacto);
                }
            }
        }

        double despesasAtivasMes = getConta().calcularTotalDespesasPorMes(mes, ano);
        double saldoLiquidoMes = getConta().calcularSaldoPorMes(mes, ano);

        System.out.printf("%n===== RESUMO MENSAL (%02d/%d) =====%n", mes, ano);
        System.out.printf("Receitas confirmadas.......: %s%n", MOEDA.format(receitasConfirmadas));
        System.out.printf("Despesas confirmadas.......: %s%n", MOEDA.format(despesasConfirmadas));
        System.out.printf("Despesas ativas (c/ pend.).: %s%n", MOEDA.format(despesasAtivasMes));
        System.out.printf("Resultado líquido (confirm.): %s%n", MOEDA.format(saldoLiquidoMes));
    }

    /** Resumo financeiro referente a um ano específico. */
    public void exibirResumoAnual(int ano) {
        if (ano <= 0) {
            throw new IllegalArgumentException("O ano deve ser maior que zero.");
        }

        double receitasConfirmadas = 0;
        double despesasConfirmadas = 0;

        for (LancamentoFinanceiro l : getConta().getLancamentos()) {
            if (l.getStatus() == StatusLancamento.CONFIRMADO && l.getAno() == ano) {
                double impacto = l.calcularImpactoNoSaldo();
                if (l instanceof Receita) {
                    receitasConfirmadas += impacto;
                } else if (l instanceof Despesa) {
                    despesasConfirmadas += Math.abs(impacto);
                }
            }
        }

        double despesasAtivasAno = getConta().calcularTotalDespesasPorAno(ano);
        double saldoLiquidoAno = getConta().calcularSaldoPorAno(ano);

        System.out.printf("%n===== RESUMO ANUAL (%d) =====%n", ano);
        System.out.printf("Receitas confirmadas.......: %s%n", MOEDA.format(receitasConfirmadas));
        System.out.printf("Despesas confirmadas.......: %s%n", MOEDA.format(despesasConfirmadas));
        System.out.printf("Despesas ativas (c/ pend.).: %s%n", MOEDA.format(despesasAtivasAno));
        System.out.printf("Resultado líquido (confirm.): %s%n", MOEDA.format(saldoLiquidoAno));
    }

    /** Visão geral: saldo, receitas, despesas e uso do limite de orçamento. */
    public void exibirBalancoGeral() {
        double receitasConfirmadas = 0;
        double despesasConfirmadas = 0;
        for (LancamentoFinanceiro l : getConta().getLancamentos()) {
            if (l.getStatus() != StatusLancamento.CONFIRMADO) {
                continue;
            }
            double impacto = l.calcularImpactoNoSaldo();
            if (l instanceof Receita) {
                receitasConfirmadas += impacto;
            } else if (l instanceof Despesa) {
                despesasConfirmadas += Math.abs(impacto);
            }
        }

        ContaPlanejamento contaAtual = getConta();
        double despesasAtivas = contaAtual.calcularTotalDespesas();
        double limite = contaAtual.getLimiteOrcamento();
        double saldoInicial = contaAtual.getSaldoDisponivel();
        double saldoAtual = contaAtual.calcularSaldoAtual();
        double usoDoLimite = (despesasAtivas / limite) * 100;

        System.out.println("\n===== BALANÇO GERAL =====");
        System.out.printf("Saldo inicial..............: %s%n", MOEDA.format(saldoInicial));
        System.out.printf("Receitas confirmadas.......: %s%n", MOEDA.format(receitasConfirmadas));
        System.out.printf("Despesas confirmadas.......: %s%n", MOEDA.format(despesasConfirmadas));
        System.out.printf("Saldo atual................: %s%n", MOEDA.format(saldoAtual));
        System.out.println("-------------------------------------------");
        System.out.printf("Despesas ativas (c/ pend.).: %s%n", MOEDA.format(despesasAtivas));
        System.out.printf("Limite de orçamento........: %s%n", MOEDA.format(limite));
        System.out.printf("Uso do limite..............: %.1f%%%n", usoDoLimite);
        System.out.printf("Orçamento restante.........: %s%n", MOEDA.format(Math.max(0, limite - despesasAtivas)));
    }
}