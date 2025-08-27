package pattern_matching;

public class pattern_matching_main {
    public static void imprimirDetalhes(Forma forma) {

        if (forma instanceof Circulo circulo) {
            System.out.println("É um círculo com raio: " + circulo.raio());

        } else if (forma instanceof Quadrado quadrado) {
            System.out.println("É um quadrado com lado: " + quadrado.lado());

        } else {
            System.out.println("É uma forma desconhecida.");
        }
    }

    public static void main(String[] args) {
        imprimirDetalhes(new Circulo(2.4));
        imprimirDetalhes(new Quadrado(6));

    }
}
