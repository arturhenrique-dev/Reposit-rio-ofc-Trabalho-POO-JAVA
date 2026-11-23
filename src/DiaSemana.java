import java.time.DayOfWeek;
import java.time.LocalDate;

public enum DiaSemana {

    SEGUNDA("Segunda-feira"),
    TERCA("Terça-feira"),
    QUARTA("Quarta-feira"),
    QUINTA("Quinta-feira"),
    SEXTA("Sexta-feira"),
    SABADO("Sábado"),
    DOMINGO("Domingo");

    private final String descricao;

    DiaSemana(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do dia da semana não pode ser vazia.");
        }
        this.descricao = descricao.trim();
    }

    public String getDescricao() {
        return descricao;
    }



    public static DiaSemana converterData(LocalDate data) {
        if (data == null) {
            throw new IllegalArgumentException("A data não pode ser nula para converter o dia da semana.");
        }

        DayOfWeek diaAtual = data.getDayOfWeek();
        return switch (diaAtual) {
            case MONDAY -> SEGUNDA;
            case TUESDAY -> TERCA;
            case THURSDAY -> QUARTA;
            case FRIDAY -> SEXTA;
            case SATURDAY -> SABADO;
            case SUNDAY -> DOMINGO;
            default -> throw new IllegalStateException("Dia da semana não suportado: " + diaAtual);
        };
    }
}
