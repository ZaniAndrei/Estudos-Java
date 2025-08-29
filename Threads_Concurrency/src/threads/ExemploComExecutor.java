import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExemploComExecutor {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("### INICIANDO EXECUTORSERVICE ###");

        // 1. CRIAÇÃO: Uma linha para criar um pool com 3 threads.
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 10; i++) {
            final int idTarefa = i;
            // 2. SUBMISSÃO: Apenas entregamos a tarefa ao executor.
            executor.execute(() -> {

                System.out.println("Tarefa " + idTarefa + " está sendo processada por: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000); // Simula trabalho
                } catch (InterruptedException e) {}
                System.out.println("Tarefa " + idTarefa + " finalizada.");
            });
        }

        System.out.println("Enviando sinal de desligamento...");
        executor.shutdown(); // Inicia o desligamento: não aceita novas tarefas, mas termina as atuais.

        System.out.println("### FINALIZADO EXECUTORSERVICE ###");
    }
}