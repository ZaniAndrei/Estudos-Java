import org.junit.jupiter.api.Test;
// 1. Importe os Assertions do JUnit 5. Eles contêm tudo que precisamos.
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
        // 2. Trocamos o assertThat do Hamcrest pelo assertEquals do JUnit 5.
        // A ordem é (esperado, atual).
        assertEquals(precoEsperado, precoCalculado);
    }

    @Test
    void deveLancarExcecaoParaDistanciaNegativa() {
        // Given (Dado)
        double distanciaNegativa = -5.0;

        // When/Then (Quando/Então)
        // 3. Esta é a principal mudança. Usamos assertThrows.

        IllegalArgumentException excecaoCapturada = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    // O código que DEVE lançar a exceção vai aqui dentro
                    calculadora.calcular(distanciaNegativa);
                }
        );

        // 4. (Opcional, mas recomendado) Verificamos se a mensagem da exceção está correta.
        assertEquals("Distância não pode ser negativa.", excecaoCapturada.getMessage());
    }
}