import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculadoraDeFreteTest {

    private final CalculadoraDeFreteService calculadora = new CalculadoraDeFreteService();

    @Test
    void deveCalcularOPrecoCorretoParaDistanciaValida() {
        // Given (Dado)
        double distancia = 10.0;
        double precoEsperado = 15.0;

        // When (Quando)
        double precoCalculado = calculadora.calcular(distancia);

        // Then (Então)
        //verifica se o preco esperado é o mesmo que a função calcula
        assertEquals(precoEsperado, precoCalculado);
    }

    @Test
    void deveLancarExcecaoParaDistanciaNegativa() {
        // Given (Dado)
        double distanciaNegativa = -5.0;

        // When/Then (Quando/Então)
        IllegalArgumentException excecaoCapturada = assertThrows(
                IllegalArgumentException.class,
                () -> {

                    calculadora.calcular(distanciaNegativa);
                }
        );

        //Verificando se a mensagem da exceção está correta.
        assertEquals("Distância não pode ser negativa.", excecaoCapturada.getMessage());
    }
}