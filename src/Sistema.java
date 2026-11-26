import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Sistema {
    private static final double LIMITE_PADRAO = 4000.0;

    private final List<Usuario> usuarios;
    private Usuario usuarioLogado;
    private final Scanner scanner;

    public Sistema() {
        this.usuarioLogado = null;
        this.scanner = new Scanner(System.in);
        this.usuarios = new ArrayList<>();

        this.usuarios.add(new Administrador(
                "Administrador",
                "admin@cashplan.com",
                "admin123",
                100000.0,
                10000.0,
                "ADM01"));
    }

    public void iniciar() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n=== CASHPLAN ===");
            System.out.println("1 - Realizar login");
            System.out.println("2 - Cadastrar usuario");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opcao: ");

            try {
                opcao = Integer.parseInt(this.getScanner().nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite apenas numeros.");
                continue;
            }

            switch (opcao) {
                case 1 -> {
                    if (realizarLogin()) {
                        exibirMenuUsuario();
                    }
                }
                case 2 -> cadastrarUsuario();
                case 0 -> System.out.println("Encerrando o sistema...");
                default -> System.out.println("Opcao invalida.");
            }
        }

        this.getScanner().close();
    }

    private boolean realizarLogin() {
        System.out.println("\n--- Login ---");
        System.out.print("Email: ");
        String email = this.getScanner().nextLine().trim();

        System.out.print("Senha: ");
        String senha = this.getScanner().nextLine();

        for (Usuario u : this.getUsuarios()) {
            if (u.getEmail().equalsIgnoreCase(email) && u.autenticar(senha)) {
                if (u instanceof Administrador admin) {
                    System.out.print("Codigo de acesso: ");
                    String codigo = this.getScanner().nextLine().trim();
                    if (!admin.getCodigoAcesso().equals(codigo)) {
                        System.out.println("Codigo de acesso invalido.");
                        return false;
                    }
                }

                this.usuarioLogado = u;
                System.out.println("Login realizado! Bem-vindo, " + getUsuarioLogado().getNome() + ".");
                return true;
            }
        }

        System.out.println("Email ou senha incorretos.");
        return false;
    }

    private boolean cadastrarUsuario() {
        System.out.println("\n--- Cadastro de Usuario ---");
        System.out.print("Nome: ");
        String nome = this.getScanner().nextLine();

        System.out.print("Email: ");
        String email = this.getScanner().nextLine().trim();

        for (Usuario u : this.getUsuarios()) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                System.out.println("Ja existe um usuario cadastrado com esse email.");
                return false;
            }
        }

        System.out.print("Senha: ");
        String senha = this.getScanner().nextLine();

        double saldoInicial;
        try {
            System.out.print("Saldo inicial (ex: 1000.00): ");
            saldoInicial = Double.parseDouble(this.getScanner().nextLine().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("Saldo inicial invalido.");
            return false;
        }

        try {
            Usuario novoUsuario = new Usuario(nome, email, senha, LIMITE_PADRAO, saldoInicial);
            this.getUsuarios().add(novoUsuario);
            System.out.println("Usuario cadastrado com sucesso! Limite de orcamento: R$ " + String.format("%.2f", LIMITE_PADRAO));
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Erro no cadastro: " + e.getMessage());
            return false;
        }
    }

    private void exibirMenuUsuario() {
        int opcao = -1;
        boolean isAdmin = getUsuarioLogado() instanceof Administrador;

        while (opcao != 0) {
            System.out.println("\n--- Menu de " + getUsuarioLogado().getNome() + " ---");
            System.out.println("1 - Cadastrar lancamento");
            System.out.println("2 - Listar lancamentos (ver descricoes)");
            System.out.println("3 - Confirmar lancamento");
            System.out.println("4 - Cancelar lancamento");
            System.out.println("5 - Excluir lancamento");
            System.out.println("6 - Gerar relatorios");
            if (isAdmin) {
                System.out.println("7 - Listar usuarios cadastrados");
                System.out.println("8 - Excluir usuario");
            }
            System.out.println("0 - Logout");
            System.out.print("Escolha uma opcao: ");

            try {
                opcao = Integer.parseInt(this.getScanner().nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite apenas numeros.");
                continue;
            }

            switch (opcao) {
                case 1 -> cadastrarLancamento();
                case 2 -> listarLancamentos();
                case 3 -> confirmarLancamento();
                case 4 -> cancelarLancamento();
                case 5 -> excluirLancamento();
                case 6 -> gerarRelatorios();
                case 7 -> {
                    if (isAdmin) {
                        listarUsuariosCadastrados();
                    } else {
                        System.out.println("Opcao invalida.");
                    }
                }
                case 8 -> {
                    if (isAdmin) {
                        excluirUsuario();
                    } else {
                        System.out.println("Opcao invalida.");
                    }
                }
                case 0 -> {
                    this.usuarioLogado = null;
                    System.out.println("Logout realizado.");
                }
                default -> System.out.println("Opcao invalida.");
            }
        }
    }

    private void listarUsuariosCadastrados() {
        if (!(getUsuarioLogado() instanceof Administrador)) {
            System.out.println("Acesso restrito a administradores.");
            return;
        }

        System.out.println("\n--- Usuarios Cadastrados ---");
        for (Usuario u : this.getUsuarios()) {
            String tipo = (u instanceof Administrador) ? "Administrador" : "Usuario";
            System.out.println("Nome: " + u.getNome() + " | Email: " + u.getEmail() + " | Tipo: " + tipo);
        }
    }

    private void excluirUsuario() {
        if (!(getUsuarioLogado() instanceof Administrador)) {
            System.out.println("Acesso restrito a administradores.");
            return;
        }

        System.out.println("\n--- Excluir Usuario ---");
        System.out.print("Informe o email do usuario a excluir: ");
        String emailAlvo = this.getScanner().nextLine().trim();

        if (emailAlvo.isEmpty()) {
            System.out.println("Email invalido.");
            return;
        }

        if (getUsuarioLogado().getEmail().equalsIgnoreCase(emailAlvo)) {
            System.out.println("Nao e possivel excluir o administrador atualmente logado.");
            return;
        }

        Usuario alvo = null;
        for (Usuario u : this.getUsuarios()) {
            if (u.getEmail().equalsIgnoreCase(emailAlvo)) {
                alvo = u;
                break;
            }
        }

        if (alvo == null) {
            System.out.println("Usuario nao encontrado.");
            return;
        }

        this.getUsuarios().remove(alvo);
        System.out.println("Usuario '" + alvo.getNome() + "' excluido com sucesso!");
    }

    private void cadastrarLancamento() {
        System.out.println("\n--- Novo Lancamento ---");
        System.out.println("1 - Despesa");
        System.out.println("2 - Receita");
        System.out.print("Tipo: ");

        int tipo;
        try {
            tipo = Integer.parseInt(this.getScanner().nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Tipo invalido.");
            return;
        }

        if (tipo != 1 && tipo != 2) {
            System.out.println("Escolha 1 para Despesa ou 2 para Receita.");
            return;
        }

        System.out.print("Nome: ");
        String nome = this.getScanner().nextLine();

        System.out.print("Descricao: ");
        String descricao = this.getScanner().nextLine();

        int codigo;
        double valor;
        try {
            System.out.print("Codigo: ");
            codigo = Integer.parseInt(this.getScanner().nextLine().trim());

            System.out.print("Valor: ");
            valor = Double.parseDouble(this.getScanner().nextLine().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("Codigo ou valor invalido.");
            return;
        }

        try {
            LancamentoFinanceiro lancamento;
            if (tipo == 1) {
                System.out.print("Categoria: ");
                String categoria = this.getScanner().nextLine();

                System.out.print("Taxa adicional (ex: 0 ou 15.50): ");
                double taxa = Double.parseDouble(this.getScanner().nextLine().trim().replace(",", "."));

                lancamento = new Despesa(nome, descricao, codigo, valor, categoria, taxa);
            } else {
                System.out.print("Origem: ");
                String origem = this.getScanner().nextLine();

                System.out.print("Frequencia (ex: Mensal, Diaria, Anual, Pontual): ");
                String frequencia = this.getScanner().nextLine();

                lancamento = new Receita(nome, descricao, codigo, valor, origem, frequencia);
            }

            getUsuarioLogado().getConta().registrarLancamento(lancamento);
            System.out.println("Lancamento registrado com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("Taxa adicional invalida.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Nao foi possivel registrar o lancamento: " + e.getMessage());
        }
    }

    private void listarLancamentos() {
        System.out.println("\n--- Lancamentos Cadastrados ---");
        List<LancamentoFinanceiro> lista = getUsuarioLogado().getConta().getLancamentos();

        if (lista.isEmpty()) {
            System.out.println("Nenhum lancamento cadastrado.");
            return;
        }

        for (LancamentoFinanceiro l : lista) {
            String tipo = (l instanceof Despesa) ? "DESPESA" : "RECEITA";
            System.out.println(
                    "Codigo: " + l.getCodigo() +
                            " | Tipo: " + tipo +
                            " | Nome: " + l.getNome() +
                            " | Descricao: " + l.getDescricao() +
                            " | Valor base: R$ " + String.format("%.2f", l.getValor()) +
                            " | Status: " + l.getStatus()
            );
        }
    }

    private void confirmarLancamento() {
        System.out.println("\n--- Confirmar Lancamento ---");
        int codigo = lerCodigo();
        if (codigo == -1) {
            return;
        }

        LancamentoFinanceiro encontrado = buscarLancamentoPorCodigo(codigo);
        if (encontrado == null) {
            System.out.println("Lancamento com codigo " + codigo + " nao encontrado.");
            return;
        }

        try {
            encontrado.confirmar();
            System.out.println("Lancamento confirmado com sucesso!");
        } catch (IllegalStateException e) {
            System.out.println("Nao foi possivel confirmar: " + e.getMessage());
        }
    }

    private void cancelarLancamento() {
        System.out.println("\n--- Cancelar Lancamento ---");
        int codigo = lerCodigo();
        if (codigo == -1) {
            return;
        }

        try {
            getUsuarioLogado().getConta().cancelarLancamento(codigo);
            System.out.println("Lancamento cancelado com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Nao foi possivel cancelar: " + e.getMessage());
        }
    }

    private void excluirLancamento() {
        System.out.println("\n--- Excluir Lancamento ---");
        int codigo = lerCodigo();
        if (codigo == -1) {
            return;
        }

        try {
            getUsuarioLogado().getConta().excluirLancamento(codigo);
            System.out.println("Lancamento excluido com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Nao foi possivel excluir: " + e.getMessage());
        }
    }

    private int lerCodigo() {
        System.out.print("Informe o codigo do lancamento: ");
        try {
            int codigo = Integer.parseInt(this.getScanner().nextLine().trim());
            if (codigo <= 0) {
                System.out.println("Digite um codigo maior que zero.");
                return -1;
            }
            return codigo;
        } catch (NumberFormatException e) {
            System.out.println("Codigo invalido.");
            return -1;
        }
    }

    private LancamentoFinanceiro buscarLancamentoPorCodigo(int codigo) {
        for (LancamentoFinanceiro l : getUsuarioLogado().getConta().getLancamentos()) {
            if (l.getCodigo() == codigo) {
                return l;
            }
        }
        return null;
    }

    private void gerarRelatorios() {
        RelatorioFinanceiro relatorio = new RelatorioFinanceiro(getUsuarioLogado().getConta());

        System.out.println("\n--- Tipo de Relatorio ---");
        System.out.println("1 - Relatorio completo (status, dia da semana e balanco geral)");
        System.out.println("2 - Relatorio mensal");
        System.out.println("3 - Relatorio anual");
        System.out.print("Escolha uma opcao: ");

        int escolha;
        try {
            escolha = Integer.parseInt(this.getScanner().nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Opcao invalida.");
            return;
        }

        try {
            switch (escolha) {
                case 1 -> {
                    relatorio.exibirResumoPorStatus();
                    relatorio.exibirGastosPorDiaSemana();
                    relatorio.exibirBalancoGeral();
                }
                case 2 -> {
                    System.out.print("Informe o mes (1 a 12): ");
                    int mes = Integer.parseInt(this.getScanner().nextLine().trim());
                    System.out.print("Informe o ano (ex: 2026): ");
                    int ano = Integer.parseInt(this.getScanner().nextLine().trim());
                    relatorio.exibirResumoMensal(mes, ano);
                }
                case 3 -> {
                    System.out.print("Informe o ano (ex: 2026): ");
                    int ano = Integer.parseInt(this.getScanner().nextLine().trim());
                    relatorio.exibirResumoAnual(ano);
                }
                default -> System.out.println("Opcao invalida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Digite apenas numeros validos para mes e ano.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro no relatorio: " + e.getMessage());
        }
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public Scanner getScanner() {
        return scanner;
    }
}