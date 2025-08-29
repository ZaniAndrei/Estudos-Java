package platform_x_virtual_threads;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class ExemploVirtualThreads {

    // Um número alto de tarefas que seria dificil de lidar com threads de plataforma.
    private static final int NUM_TAREFAS = 100_000;

    public static void main(String[] args) {
        System.out.println("### Iniciando demonstração com Threads de Plataforma ###");
        // Esta primeira parte provavelmente vai falhar com um erro de memória.
        // Isso é esperado e serve para demonstrar a limitação.
        rodarComPlatformThreads();

        System.out.println("\n-------------------------------------------------\n");

        System.out.println("### Iniciando demonstração com Threads Virtuais ###");
        // Esta parte deve executar com sucesso.
        rodarComVirtualThreads();
    }

    /**
     * Tenta executar as tarefas usando o modelo tradicional de uma thread por tarefa.
     * Este método vai falhar se ficar sem memória.
     */
    public static void rodarComPlatformThreads() {
        Instant start = Instant.now();
        // newThreadPerTaskExecutor cria uma nova thread de plataforma para cada tarefa.
        try (ExecutorService executor = Executors.newThreadPerTaskExecutor(Executors.defaultThreadFactory())) {

            IntStream.range(0, NUM_TAREFAS).forEach(i -> {
                executor.submit(() -> {
                    Thread.sleep(Duration.ofSeconds(1));
                    // A linha abaixo pode não ser impressa, pois o programa quebrará antes.
                    //System.out.println("Tarefa de plataforma " + i + " finalizada.");
                    return i;
                });
            });

        } catch (OutOfMemoryError e) {
            System.err.println("!!! ERRO ESPERADO !!!");
            System.err.println("Falha ao criar threads de plataforma: " + e.getMessage());
            System.err.println("O sistema operacional não conseguiu alocar memória para tantas threads.");
        } finally {
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            System.out.println("Tempo total de execução (Plataforma): " + timeElapsed + " ms");
        }

    }

    /**
     * Executa as mesmas tarefas usando Threads Virtuais.
     * Este método é projetado para ter sucesso.
     */
    public static void rodarComVirtualThreads() {
        Instant start = Instant.now();

        // newVirtualThreadPerTaskExecutor cria uma nova thread virtual para cada tarefa.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            IntStream.range(0, NUM_TAREFAS).forEach(i -> {
                executor.submit(() -> {
                    // A tarefa é idêntica à anterior.
                    // O código é o mesmo, mas a execução por baixo dos panos é diferente.
                    //System.out.println("Iniciando tarefa virtual " + i + ". Rodando em: " + Thread.currentThread());
                    Thread.sleep(Duration.ofSeconds(1));
                    // System.out.println("Tarefa virtual " + i + " finalizada.");
                    return i;
                });
            });

        } // O try-with-resources garante que o executor espere todas as tarefas terminarem.

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Todas as " + NUM_TAREFAS + " tarefas virtuais foram concluídas.");
        System.out.println("Tempo total de execução (Virtual): " + timeElapsed + " ms");
    }
}