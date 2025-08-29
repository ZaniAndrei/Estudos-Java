package threads;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExemploCalculoQuadrado {
    public static int somaList(List<Integer> resultado){
        int soma = 0;
        for(Integer integer : resultado){
            soma += integer;
        }
        return soma;
    }

    public static void main(String[] args) throws Exception {
        List<Integer> numbersList = Arrays.asList(5,8,10,3,7);
        List<Integer> resultado = new ArrayList<>();
        //pool de threads com o numero de elementos da lista
        ExecutorService executor = Executors.newFixedThreadPool(numbersList.size());

        for(Integer integer : numbersList) {
            //objeto callable vai ser o responsável por calcular o quadrado do número atribuido a thread
            Callable<Integer> calculatedNumber = new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    Thread.sleep(500);
                    return integer*integer;
                }
            };
            System.out.println("Calculando o quadrado de " + integer + "...");

            //Callable é enviado para o executor, que irá retornar um valor no futuro(quando a thread terminar de executar)
            Future<Integer> future = executor.submit(calculatedNumber);
            //metodo get pega o resultado calculado na thread
            resultado.add(future.get());
        }
        executor.shutdown();

        //após o término dos cálculos, a soma dos quadrados é realizada
        System.out.println("A Soma dos quadrados foi: " + somaList(resultado));

    }
}
