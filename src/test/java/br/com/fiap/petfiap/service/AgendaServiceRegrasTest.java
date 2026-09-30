package br.com.fiap.petfiap.service;

import br.com.fiap.petfiap.exception.StatusInvalidoException;
import br.com.fiap.petfiap.model.Banho;
import br.com.fiap.petfiap.repository.AtendimentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Regras de agendamento sem cobertura na suite original (Aula 15).
// O @Mock substitui o repository: sem banco e sem Spring.
@ExtendWith(MockitoExtension.class)
public class AgendaServiceRegrasTest {

    @Mock
    private AtendimentoRepository repository;

    @InjectMocks
    private AgendaService service;

    private Banho banhoDoRexAmanha() {
        return new Banho(1, "Rex", "PEQUENO", "Ana", LocalDateTime.now().plusDays(1).withNano(0));
    }

    @Test
    public void deveRecusarCancelamentoQuandoAtendimentoJaConcluido() {
        // Arrange: o atendimento ja foi realizado
        Banho jaConcluido = banhoDoRexAmanha();
        jaConcluido.setStatus("CONCLUIDO");
        when(repository.findById(1L)).thenReturn(Optional.of(jaConcluido));

        // Act + Assert: contrato - cancelar atendimento CONCLUIDO e recusado
        assertThrows(StatusInvalidoException.class, () -> service.cancelar(1L));

        // Nada e salvo quando a operacao e recusada
        verify(repository, never()).save(any());
    }
    
    @Test
    public void deveRecusarAgendamentoQuandoDataHoraForNoPassado() {
        // Arrange: data/hora de ontem
        Banho noPassado = new Banho(1, "Rex", "PEQUENO", "Ana", LocalDateTime.now().minusDays(1));

        // Act + Assert: contrato - agendar no passado e recusado
        assertThrows(IllegalArgumentException.class, () -> service.agendar(noPassado));

        // O banco nem e consultado nem acionado
        verify(repository, never()).findByPetNome(any());
        verify(repository, never()).save(any());
    }
}