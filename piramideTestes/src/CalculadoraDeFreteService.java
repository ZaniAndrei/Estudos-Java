// src/main/java/com/exemplo/CalculadoraDeFreteService.java

public class CalculadoraDeFreteService {
    private static final double TARIFA_BASE_POR_KM = 1.5;

    public double calcular(double distanciaEmKm) {
        if (distanciaEmKm < 0) {
            throw new IllegalArgumentException("Distância não pode ser negativa.");
        }
        return distanciaEmKm * TARIFA_BASE_POR_KM;
    }
}