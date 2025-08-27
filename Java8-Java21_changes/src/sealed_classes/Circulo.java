package sealed_classes;

public final class Circulo implements Forma{
    public final int raio;

    public Circulo(int raio) {
        this.raio = raio;
    }

    public double getArea() {
        return 3.14*Math.pow(this.raio, 2);
    }
}
