package br.com.fiap.petfiap.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Teste unitario do model: sem banco, sem Spring (Aula 15).
public class TosaDuracaoTest {

    @Test
    public void deveDurar60MinutosQuandoForTosa() {
        // Arrange
        Tosa tosa = new Tosa(1, "Rex", "PEQUENO", "Ana", LocalDateTime.of(2026, 10, 1, 10, 0));

        // Act
        int duracao = tosa.getDuracaoMinutos();

        // Assert: contrato - a tosa dura 60 minutos
        assertEquals(60, duracao);
    }
}