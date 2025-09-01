package consulta_threads;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class ConsultaThreads {

    public static Map.Entry<String, Double> getMelhorOferta(HashMap<String, Double> hash){

        if(hash.isEmpty()){
            return null;
        }

        Map.Entry<String, Double> menorEntrada;
        menorEntrada = Collections.min(
                hash.entrySet(),
                Map.Entry.comparingByValue()
        );

        return menorEntrada;
    }

    public static List<Future<Oferta>> consultaEmpresas(List<String> listaNomeEmpresas, ExecutorService executor){
        Random random = new Random();
        List<Future<Oferta>> listaFutures =  new ArrayList<>();

        for(String nomeEmpresa : listaNomeEmpresas) {
            Callable<Oferta> ofertaConsulta = () -> {

                int sleepTime = random.nextInt(750, 2000);
                Double oferta = random.nextDouble(150.00, 259.99);
                int probabilidade = random.nextInt(100);

                if(probabilidade < 75){
                    Oferta empresa = new Oferta(nomeEmpresa, oferta);
                    System.out.println("Consultando " + nomeEmpresa);
                    Thread.sleep(sleepTime);
                    return empresa;
                }else{
                    Thread.sleep(sleepTime);
                    throw new Exception("Falha na consulta da " + nomeEmpresa);
                }

            };

            Future<Oferta> future =  executor.submit(ofertaConsulta);
            listaFutures.add(future);

        }
        return listaFutures;
    }

    public static List<Oferta> getEmpresas(List<Future<Oferta>>listaFutures){
        List<Oferta> empresasList = new ArrayList<>();
        for(Future<Oferta> future: listaFutures ){
            try {
                Oferta empresa = future.get();
                empresasList.add(empresa);

            } catch (InterruptedException | ExecutionException e) {
                System.out.println(e.getMessage());
            }
        }
        return empresasList;
    }

    public static HashMap<String, Double> getHashmap(List<Oferta> empresasList){
        HashMap<String, Double> hashmap = new HashMap<>();
        for(Oferta empresa : empresasList){
            hashmap.put(empresa.nomeFornecedor(), empresa.oferta());
        }
        return hashmap;
    }


    static void main(String[] args) throws ExecutionException, InterruptedException {
        List<String> listaNomeEmpresas = new ArrayList<>();
        int numeroEmpresas = 10;
        for(int i = 0; i < numeroEmpresas; i++){
            listaNomeEmpresas.add("Empresa " + i);
        }

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        Instant start = Instant.now();
        List<Future<Oferta>> listaFutures = consultaEmpresas(listaNomeEmpresas, executor);
        List<Oferta> listaEmpresas = getEmpresas(listaFutures);
        executor.shutdown();
        Instant end = Instant.now();

        HashMap<String, Double> hash = getHashmap(listaEmpresas);
        Map.Entry<String, Double> melhorOferta = getMelhorOferta(hash);


        long timeElapsed = Duration.between(start, end).toMillis();

        if(melhorOferta == null){
            System.out.println("Todas as consultas falharam!");
            System.out.println("Tempo total de execução: " + timeElapsed + "ms");

        }else{
            System.out.print("Todas as ofertas encontradas: ");
            for(Oferta empresas:listaEmpresas ){
                double oferta = Math.round(empresas.oferta()*100.0)/100.0;
                System.out.print(oferta + "; ");
            }
            System.out.println();
            System.out.println("------------- Relatório de Consultas ----------------");
            System.out.println("Melhor oferta encontrada: ");
            System.out.println("Fornecedor: " + melhorOferta.getKey());

            double ofertaRound = Math.round(melhorOferta.getValue() * 100.0)/100.0;
            System.out.println("Preço: R$" + ofertaRound);


            System.out.println("Tempo total de execução: " + timeElapsed + "ms");
        }
    }
}
