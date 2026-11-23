# CashPlan: núcleo de planejamento financeiro (POO em Java)

Este repositório contém o núcleo de regras de negócio do **CashPlan**, sistema de controle financeiro pessoal desenvolvido no **Projeto de Extensão 1** (Extensão Curricular, ADS, Unifip). Escrito em Java com Programação Orientada a Objetos, ele define como lançamentos, orçamento e saldo se comportam, e é a base que o aplicativo Android do CashPlan usa para controlar as finanças do usuário.

## Sobre o produto final: CashPlan

O CashPlan ajuda o usuário a organizar suas finanças: registrar receitas e despesas, respeitar um limite de orçamento e acompanhar o saldo e os gastos.

- **Plataforma:** aplicativo Android (o escopo do projeto foi reduzido para apenas o app, sem versão web).
- **Telas e protótipo:** [Figma do CashPlan](https://www.figma.com/design/6p7kAXNAnrbmN1qGmAEhTn), com temas claro e escuro.
- **Objetivo da fase atual:** dar início ao projeto e apresentá-lo aos contribuintes, com um MVP funcional.


## Como este código se relaciona com o produto

O app do CashPlan mostra telas e botões; por trás deles é preciso garantir que os números estejam corretos e que as regras sejam respeitadas. Este módulo cumpre esse papel, em três peças dentro do pacote `planejamento`:

```
StatusLancamento  -->  ContaPlanejamento  -->  RelatorioFinanceiro
 (ciclo de vida)       (regras de negócio)       (consulta e exibição)
```

| Componente            | Papel no código                                   | O que representa no CashPlan                                              |
|-----------------------|---------------------------------------------------|---------------------------------------------------------------------------|
| `StatusLancamento`    | Enum com o ciclo de vida de um lançamento         | O estado de cada movimentação: pendente, confirmada ou cancelada          |
| `ContaPlanejamento`   | Conta do usuário, com CRUD e regras de negócio    | Cadastro de receitas/despesas, limite de orçamento e saldo                |
| `RelatorioFinanceiro` | Relatórios de somente leitura sobre a conta       | Os resumos e o balanço que o usuário vê nas telas de acompanhamento       |

## Os três componentes

### 1. `StatusLancamento` (enum)

| Status       | Significado                                                   |
|--------------|---------------------------------------------------------------|
| `PENDENTE`   | Registrado, ainda não efetivado (não afeta o saldo atual).    |
| `CONFIRMADO` | Efetivado (afeta o saldo atual).                              |
| `CANCELADO`  | Encerrado; não pode mais ser confirmado nem alterado (RN02).  |

**No CashPlan:** é a base do domínio. Define o que conta no saldo, no limite de orçamento e nas estatísticas.

### 2. `ContaPlanejamento` (núcleo do sistema)

Representa a conta de planejamento de um usuário e concentra as regras de negócio.

- **Composição** com `Usuario`: a conta nasce e morre com o usuário.
- **Agregação** com `LancamentoFinanceiro`: coleção polimórfica de `Despesa` e `Receita`.
- **Estruturas de dados:** `ArrayList` mantém a ordem de registro; `HashMap` indexa por código para busca, atualização e cancelamento em O(1).
- **Operações:** registrar, buscar, atualizar, cancelar, excluir e listar lançamentos (a listagem devolve uma cópia imutável).
- **Cálculos:** `calcularTotalDespesas()` e `calcularSaldoAtual()`.

| Regra | Descrição                                                                 |
|-------|---------------------------------------------------------------------------|
| RN01  | Bloqueia despesa que ultrapasse o limite de orçamento.                    |
| RN02  | Lançamento cancelado não pode ser alterado.                               |
| RN03  | Lançamento confirmado só pode ser excluído depois de cancelado.           |
| RN04  | O valor do lançamento deve ser maior que zero.                            |

Convenção adotada: `calcularImpactoNoSaldo()` retorna valor **com sinal** (negativo para `Despesa`, positivo para `Receita`).

**No CashPlan:** é a única porta de entrada para alterar os lançamentos. Toda ação do usuário no app (adicionar, editar, cancelar, excluir) passa por aqui e só é aceita se respeitar as regras.

### 3. `RelatorioFinanceiro` (camada de consulta)

Gera relatórios de **somente leitura** sobre uma `ContaPlanejamento`, sem alterá-la:

- `exibirResumoPorStatus()`: quantidade e valor total de lançamentos em cada status.
- `exibirGastosPorDiaSemana()`: total de despesas ativas agrupado por `DiaSemana`.
- `exibirBalancoGeral()`: saldo inicial, receitas e despesas confirmadas, saldo atual, uso do limite e orçamento restante.

Os valores são formatados em reais (R$) com `NumberFormat` e `Locale` pt-BR.

**No CashPlan:** é a informação que o usuário enxerga. Transforma os dados da conta em resumos para decisões do dia a dia, como quanto ainda pode gastar dentro do orçamento.

## Fluxo resumido

1. O usuário registra uma receita ou despesa no app.
2. `ContaPlanejamento` valida (RN01 a RN04) e guarda o lançamento com seu `StatusLancamento`.
3. `RelatorioFinanceiro` consulta a conta e apresenta saldo, gastos e uso do limite.

## Conceitos de POO aplicados

- **Encapsulamento:** atributos privados e listagem imutável.
- **Polimorfismo:** `Despesa` e `Receita` tratadas como `LancamentoFinanceiro`, cada uma com seu `calcularImpactoNoSaldo()`.
- **Composição e agregação:** `ContaPlanejamento` com `Usuario` e com os lançamentos.
- **Enums:** `StatusLancamento` e `DiaSemana` modelam valores fixos do domínio.
- **Coleções:** `ArrayList`, `HashMap` e `EnumMap`.
- **Tratamento de exceções:** `IllegalArgumentException` e `IllegalStateException` nas regras de negócio.

## Requisitos

- Java 19 ou superior (`Locale.of` em `RelatorioFinanceiro` e pattern matching com `instanceof` em `ContaPlanejamento`).
