package threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExemploComExecutor {
    public static void main(String[] args) throws InterruptedException {
        //pool de threads com 2 threads
        ExecutorService executor = Executors.newFixedThreadPool(2);

        //5 tasks para as 2 threads fixas rodarem concorrentemente
        for(int i = 0; i < 5; i++){
            final int taskId = i;
            executor.execute(()->{
                try {
                    System.out.println("Preparando o pedido #"+ taskId + " na thread " + Thread.currentThread().getName());
                    Thread.sleep(1000);
                    System.out.println("Pedido #"+taskId+" finalizado");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });

        }

        executor.shutdown();

        //espera todas as threads encerrarem de executar
        try{
            if(!executor.awaitTermination(10, TimeUnit.SECONDS)){
                System.out.println("Erro");
                executor.shutdownNow();
            }

        }catch(InterruptedException e){
            executor.shutdownNow();
        }
        System.out.println("Tarefa finalizada");
    }
}