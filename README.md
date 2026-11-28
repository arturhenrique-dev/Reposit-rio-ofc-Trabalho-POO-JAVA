# CashPlan — Núcleo de Lançamentos Financeiros

Este documento detalha as classes e enumerações desenvolvidas para modelar o domínio de movimentações financeiras do sistema **CashPlan**, aplicando conceitos fundamentais de Programação Orientada a Objetos (POO) em Java como abstração, herança, polimorfismo, encapsulamento e tipagem forte com enums.

---

## Visão Geral das Contribuições

O núcleo de lançamentos financeiros é responsável por representar qualquer entrada ou saída de dinheiro na conta do usuário, garantindo integridade dos dados, controle de ciclo de vida (`PENDENTE`, `CONFIRMADO`, `CANCELADO`), cálculo polimórfico de impacto no saldo e extração automática de informações temporais (dia da semana, mês e ano).

| Componente | Tipo | Papel Arquitetural |
| :--- | :--- | :--- |
| **`LancamentoFinanceiro`** | Classe Abstrata | Superclasse base que define atributos comuns, validações e o contrato polimórfico dos lançamentos. |
| **`Despesa`** | Classe Concreta | Especialização de `LancamentoFinanceiro` que representa saídas de recursos com categoria e taxa adicional. |
| **`Receita`** | Classe Concreta | Especialização de `LancamentoFinanceiro` que representa entradas de recursos com origem e frequência. |
| **`DiaSemana`** | Enumeração (`enum`) | Representação tipada dos dias da semana com conversão automática a partir de `LocalDate`. |

---

## Detalhamento de Cada Classe

### 1. `LancamentoFinanceiro` (Classe Abstrata)
É a peça central da hierarquia de movimentações financeiras. Como um lançamento genérico não existe isoladamente no mundo real sem ser uma entrada ou uma saída, a classe foi modelada como `abstract`.

* **O que faz:**
  * Armazena os dados fundamentais de qualquer movimentação: `nome`, `descricao`, `codigo`, `valor`, `data` (`LocalDate`), `diaSemana` (`DiaSemana`) e `status` (`StatusLancamento`).
  * Inicializa todo novo lançamento no estado `PENDENTE` e deriva automaticamente o `DiaSemana` a partir da data registrada.
  * Fornece métodos de controle de ciclo de vida (`confirmar()` e `cancelar()`), bloqueando transições inválidas (como confirmar um lançamento já cancelado).
  * Disponibiliza os métodos `getMes()` e `getAno()` a partir de `LocalDate`, dando suporte direto aos relatórios mensais e anuais sem redundância de atributos.
  * Define o método abstrato `calcularImpactoNoSaldo()`, obrigando cada subclasse a determinar como afeta o saldo da conta.
* **Importância no código:**
  * Permite que a `ContaPlanejamento` e o `RelatorioFinanceiro` manipulem despesas e receitas de forma unificada por meio de **polimorfismo** (`List<LancamentoFinanceiro>`), eliminando duplicação de código e facilitando futuras extensões do sistema.

---

### 2. `Despesa` (Subclasse de `LancamentoFinanceiro`)
Representa todo gasto ou saída financeira registrada pelo usuário, como contas fixas, compras ou serviços.

* **O que faz:**
  * Estende `LancamentoFinanceiro` e adiciona atributos específicos de gastos: `categoria` (ex.: Alimentação, Transporte, Lazer) e `taxaAdicional` (juros, tarifas ou encargos).
  * Sobrescreve `calcularImpactoNoSaldo()` retornando um **valor negativo** (`-(valor + taxaAdicional)`) quando ativa, ou `0` caso esteja `CANCELADA`.
  * Disponibiliza o método `aplicarTaxa(double taxa)` para atualização segura de encargos, impedindo valores negativos ou alterações em despesas canceladas.
  * Sobrescreve `descrever()` para incluir a categoria e a taxa adicional na exibição detalhada.
* **Importância no código:**
  * Garante que toda saída de dinheiro reduza o saldo atual de forma padronizada e permite à `ContaPlanejamento` validar o teto de gastos (`limiteOrcamento`) e ao `RelatorioFinanceiro` consolidar gastos por dia da semana, mês e ano.

---

### 3. `Receita` (Subclasse de `LancamentoFinanceiro`)
Representa toda entrada financeira que aumenta o poder de compra ou recompõe o saldo do usuário.

* **O que faz:**
  * Estende `LancamentoFinanceiro` e adiciona atributos próprios de entradas: `origem` (ex.: Salário, Freelance, Venda, Reembolso) e `frequencia` (ex.: Mensal, Pontual, Semanal, Anual).
  * Sobrescreve `calcularImpactoNoSaldo()` retornando um **valor positivo** (`+valor`) quando confirmada, ou `0` caso esteja `CANCELADA`.
  * Sobrescreve `descrever()` acrescentando a origem e a frequência da receita.
* **Importância no código:**
  * Equilibra o fluxo financeiro da conta: quando confirmada, soma positivamente ao saldo atual, permitindo inclusive cobrir saldos temporariamente negativos gerados por despesas anteriores.

---

### 4. `DiaSemana` (Enumeração)
Representa de forma segura e padronizada os sete dias da semana em português (`SEGUNDA` a `DOMINGO`).

* **O que faz:**
  * Associa cada constante do `enum` a uma descrição legível (ex.: `"Segunda-feira"`, `"Sábado"`).
  * Implementa o método estático `converterData(LocalDate data)`, que traduz o `DayOfWeek` da API `java.time` para o `DiaSemana` correspondente do domínio do sistema, validando entradas nulas.
* **Importância no código:**
  * Substitui o uso frágil de `String` ou números mágicos por **segurança de tipos em tempo de compilação**, garantindo integridade nos agrupamentos do relatório de gastos por dia da semana (`EnumMap<DiaSemana, Double>`).

---

## Como Esses Componentes Trabalham Juntos

1. Ao instanciar uma **`Despesa`** ou **`Receita`**, o construtor da superclasse **`LancamentoFinanceiro`** valida os dados básicos, define a data e consulta **`DiaSemana.converterData(...)`** para registrar o dia da semana automaticamente.
2. A conta armazena ambos os tipos em uma única coleção polimórfica de **`LancamentoFinanceiro`**.
3. Quando o usuário confirma os lançamentos, o cálculo de saldo percorre a lista chamando `calcularImpactoNoSaldo()` — onde **`Receita`** soma e **`Despesa`** subtrai automaticamente, sem necessidade de verificações condicionais espalhadas pelo sistema.

## Autoria
Módulo implementado por **Walison José** na branch correspondente às suas contribuições no projeto **CashPlan**.
