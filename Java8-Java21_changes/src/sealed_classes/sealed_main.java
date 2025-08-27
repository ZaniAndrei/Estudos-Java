package sealed_classes;

public class sealed_main {
    public static void printAreas(Forma forma){
        if(forma instanceof Circulo circulo){
            System.out.println("Area circulo:" + circulo.getArea());
        }
        if(forma instanceof Quadrado quadrado){
            System.out.println("Area quadrado:" + quadrado.getArea());
        }
    }

    public static void main(String[] args) {
        printAreas(new Circulo(3));
        printAreas(new Quadrado(4));
    }
}
