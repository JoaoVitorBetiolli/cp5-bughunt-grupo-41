package br.com.fiap.petfiap.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Teste unitario do model: sem banco, sem Spring (Aula 15).
public class BanhoPrecoTest {

    @Test
    public void deveCustar60ReaisQuandoPorteForPequeno() {
        // Arrange
        Banho banho = new Banho(1, "Rex", "PEQUENO", "Ana", LocalDateTime.of(2026, 10, 1, 10, 0));

        // Act
        double preco = banho.calcularPreco();

        // Assert: contrato - banho de porte PEQUENO custa R$ 60,00
        assertEquals(60.0, preco, 0.001);
    }
}