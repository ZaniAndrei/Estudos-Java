package platform_x_virtual_threads;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExemploVirtualThreads {
    static void main(String[] args) {
        int num_threads = 100_000;
        //threads virtuais serão criadas de acordo com as tasks que são atribuidas
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        Instant start = Instant.now();

        //simulando 100_000 tasks
        for(int i = 0; i < num_threads; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Executando thread virtual: " + Thread.currentThread());
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        executor.shutdown();

        //espera todas as threads terminarem de executar
        try{
            if(!executor.awaitTermination(10, TimeUnit.SECONDS)){
                System.out.println("Timeout");
                executor.shutdownNow();
            }
        }catch(InterruptedException e){
            executor.shutdownNow();
        }

        Instant end = Instant.now();
        long timeElapsed = Duration.between(start, end).toMillis();
        System.out.println("Tempo total de execução das " + num_threads + " tasks: " + timeElapsed +"ms");
    }


}