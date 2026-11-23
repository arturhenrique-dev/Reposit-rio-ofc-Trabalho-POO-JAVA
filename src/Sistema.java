import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Sistema {
    private List<Usuario> usuarios;
    private Usuario usuarioLogado;
    private Scanner scanner;

    public Sistema() {
        this.usuarios = new ArrayList<>();
        this.usuarioLogado = null;
        this.scanner = new Scanner(System.in);
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
                opcao = Integer.parseInt(scanner.nextLine());
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

        scanner.close();
    }

    private boolean realizarLogin() {
        System.out.println("\n--- Login ---");
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email) && u.autenticar(senha)) {
                this.usuarioLogado = u;
                System.out.println("Login realizado! Bem-vindo, " + u.getNome() + ".");
                return true;
            }
        }

        System.out.println("Email ou senha incorretos.");
        return false;
    }

    private boolean cadastrarUsuario() {
        System.out.println("\n--- Cadastro de Usuario ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                System.out.println("Ja existe um usuario cadastrado com esse email.");
                return false;
            }
        }

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        Usuario novoUsuario = new Usuario(nome, email, senha);
        usuarios.add(novoUsuario);
        System.out.println("Usuario cadastrado com sucesso!");
        return true;
    }

    private void exibirMenuUsuario() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n--- Menu de " + usuarioLogado.getNome() + " ---");
            System.out.println("1 - Cadastrar lancamento");
            System.out.println("2 - Gerar relatorios");
            System.out.println("0 - Logout");
            System.out.print("Escolha uma opcao: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Digite apenas numeros.");
                continue;
            }

            switch (opcao) {
                case 1 -> cadastrarLancamento();
                case 2 -> gerarRelatorios();
                case 0 -> {
                    this.usuarioLogado = null;
                    System.out.println("Logout realizado.");
                }
                default -> System.out.println("Opcao invalida.");
            }
        }
    }

    private void cadastrarLancamento() {
        System.out.println("\n--- Novo Lancamento ---");
        System.out.print("Descricao: ");
        String descricao = scanner.nextLine();

        double valor;
        try {
            System.out.print("Valor: ");
            valor = Double.parseDouble(scanner.nextLine().replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("Valor invalido.");
            return;
        }

        Lancamento lancamento = new Lancamento(descricao, valor);
        usuarioLogado.adicionarLancamento(lancamento);
        System.out.println("Lancamento salvo!");
    }

    private void gerarRelatorios() {
        System.out.println("\n--- Relatorio de " + usuarioLogado.getNome() + " ---");
        List<Lancamento> lista = usuarioLogado.getLancamentos();

        if (lista.isEmpty()) {
            System.out.println("Nenhum lancamento encontrado.");
            return;
        }

        double total = 0;
        for (Lancamento l : lista) {
            System.out.println(l.getData() + " | " + l.getDescricao() + " | R$ " + String.format("%.2f", l.getValor()));
            total += l.getValor();
        }
        System.out.println("Total: R$ " + String.format("%.2f", total));
    }
}