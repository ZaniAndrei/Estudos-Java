package var_palavra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class var_example {
    public static void main(String[] args) {
        // Criando uma lista de strings
        var listaDeNomes = new ArrayList<String>();
        listaDeNomes.add("Ana");
        listaDeNomes.add("Bruno");

        if(listaDeNomes instanceof ArrayList<String>){
            System.out.println("Lista de Nomes é uma lista de array de Strings");

        }

    }
}
