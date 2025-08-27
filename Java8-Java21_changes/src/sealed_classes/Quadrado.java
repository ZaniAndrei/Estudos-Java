package sealed_classes;

public final class Quadrado implements Forma{
    public final int lado;
    public Quadrado(int lado) {
        this.lado = lado;
    }
    public double getArea(){
        return this.lado*this.lado;
    }
}
