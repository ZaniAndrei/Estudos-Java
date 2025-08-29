package structured_concurrency;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;

public class ExemploConcorrenciaEstruturada {

    public static void main(String[] args) {
        System.out.println("### Iniciando demonstração de SUCESSO. ###\n");
        demonstrarSucesso();

        System.out.println("\n\n### Iniciando demonstração de FALHA. ###\n");
        demonstrarFalha();
    }

    // --- Demonstração do caminho feliz ---
    public static void demonstrarSucesso() {
        // StructuredTaskScope usa Virtual Threads por padrão para cada tarefa.
        // A política ShutdownOnFailure garante que, se uma tarefa falhar, todas as outras são canceladas.
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            System.out.println("Main: Iniciando tarefas concorrentes (fork)...");

            // fork() submete a tarefa para execução em uma nova Thread Virtual e retorna um Future.
            StructuredTaskScope.Subtask<String> precoFuture = scope.fork(() -> buscarPrecoDoProduto());
            StructuredTaskScope.Subtask<Integer> estoqueFuture = scope.fork(() -> buscarEstoqueDoProduto());

            System.out.println("Main: Tarefas iniciadas. Aguardando a finalização (join)...");

            // join() espera até que AMBAS as tarefas tenham sido concluídas.
            scope.join();

            System.out.println("Main: Tarefas finalizadas. Processando resultados...");

            // throwIfFailed() lançaria uma exceção se alguma tarefa tivesse falhado.
            // Como estamos no cenário de sucesso, ele não fará nada.
            scope.throwIfFailed();

            // resultNow() é usado para obter o resultado. Como já demos 'join',
            // temos certeza de que o resultado está disponível e não haverá bloqueio.
            String preco = precoFuture.get();
            Integer estoque = estoqueFuture.get();

            System.out.printf("Resultado final -> Preço: %s | Estoque: %d unidades%n", preco, estoque);

        } catch (Exception e) {
            // Este bloco não deve ser executado no cenário de sucesso.
            System.err.println("Ocorreu um erro inesperado: " + e.getMessage());
        }
    }

    // --- Demonstração de como as falhas são tratadas ---
    public static void demonstrarFalha() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            System.out.println("Main: Iniciando tarefas concorrentes (uma delas vai falhar)...");

            StructuredTaskScope.Subtask<String> precoFuture = scope.fork(() -> buscarPrecoDoProduto());
            // Esta tarefa irá lançar uma exceção.
            StructuredTaskScope.Subtask<Integer> estoqueFuture = scope.fork(() -> buscarEstoqueDoProdutoComFalha());

            System.out.println("Main: Tarefas iniciadas. Aguardando a finalização (join)...");
            scope.join();

            // Assim que buscarEstoqueDoProdutoComFalha() falhar, a política ShutdownOnFailure
            // CANCELARÁ a tarefa buscarPrecoDoProduto(). O join() retornará, e a linha
            // abaixo lançará a exceção que causou a falha.
            System.out.println("Main: Verificando falhas...");
            scope.throwIfFailed();

        } catch (Exception e) {
            // A exceção da tarefa falha é capturada aqui!
            System.err.println("Main: Falha capturada com sucesso! Causa: " + e.getMessage());
        }
    }

    // --- Métodos de Simulação ---

    private static String buscarPrecoDoProduto() throws InterruptedException {
        System.out.println("--> [PREÇO] Buscando... Rodando em: " + Thread.currentThread());
        Thread.sleep(Duration.ofSeconds(1));
        System.out.println("<-- [PREÇO] Encontrado!");
        return "R$ 125,99";
    }

    private static Integer buscarEstoqueDoProduto() throws InterruptedException {
        System.out.println("--> [ESTOQUE] Buscando... Rodando em: " + Thread.currentThread());
        Thread.sleep(Duration.ofSeconds(2));
        System.out.println("<-- [ESTOQUE] Encontrado!");
        return 215;
    }

    private static Integer buscarEstoqueDoProdutoComFalha() throws InterruptedException {
        System.out.println("--> [ESTOQUE COM FALHA] Buscando... Rodando em: " + Thread.currentThread());
        Thread.sleep(Duration.ofMillis(500)); // Falha rapidamente
        throw new RuntimeException("Simulação de erro: Banco de dados indisponível");
    }
}