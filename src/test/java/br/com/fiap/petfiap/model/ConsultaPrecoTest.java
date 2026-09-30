package br.com.fiap.petfiap.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Teste unitario do model: sem banco, sem Spring (Aula 15).
public class ConsultaPrecoTest {

    @Test
    public void deveCustar150ReaisQuandoPorteForGrande() {
        // Arrange: o porte do pet nao deve influenciar o preco da consulta
        ConsultaVeterinaria consulta = new ConsultaVeterinaria(
                1, "Thor", "GRANDE", "Carla", LocalDateTime.of(2026, 10, 1, 14, 0));

        // Act
        double preco = consulta.calcularPreco();

        // Assert: contrato - consulta tem preco fixo de R$ 150,00
        assertEquals(150.0, preco, 0.001);
    }
}