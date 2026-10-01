# Checkpoint 5 — Bug Hunt PetFiap

## Identificação

**Grupo:** Grupo 41

| Integrante | RM | Turma |
|---|---|---|
| João Vitor Betiolli | 561835 | 2CCPY |
| João Victor Caitano Tabuso | 562525 | 2CCPY |
| João Pedro Tomas Dominguito | 562166 | 2CCPY |
| Luiz Gustavo Lima da Silva | 563554 | 2CCPY |
| Vicente Casellato Rodriguez | 563865 | 2CCPY |

| Campo | |
|---|---|
| **Total de bugs corrigidos** | 12 / 12 |
| **Total de ajustes de Clean Code** | 6 / 6 |
| **Total de testes novos escritos** | 6 / 6 |
| **Suíte final (Run As → JUnit Test)** | 26 testes, 0 falhas |

---

## Parte 1 — Bugs encontrados

| # | Sintoma observado (o que fiz/vi) | Causa raiz (arquivo e linha aproximada) | Correção aplicada | Conceito da disciplina |
|---|---|---|---|---|
| bug01 | `GeradorProtocoloTest.deveManterUmaUnicaInstancia`: `expected: <GeradorProtocolo@389c4eb1> but was: <GeradorProtocolo@3fc79729>`. O Console mostrava "GeradorProtocolo criado!" três vezes | `GeradorProtocolo.getInstancia()`, linha ~19: devolvia `new GeradorProtocolo()` sem guardar o objeto em `instancia`, que ficava sempre `null` | `instancia = new GeradorProtocolo();` antes do `return instancia;` | Padrão Singleton (Aula 14) |
| bug02 | `AtendimentoBuilderTest.deveMontarAtendimentoCompleto`: `expected: <Rex> but was: <null>` | `AtendimentoBuilder.comPet`, linha ~24: `petNome = petNome;` atribuía o parâmetro a ele mesmo, faltava o `this` | `this.petNome = petNome;` | Escopo de variável e palavra-chave `this` (POO) |
| bug03 | `deveRecusarMontagemSemNomeDoPet` e `deveRecusarMontagemSemPorte`: `Expected IllegalArgumentException to be thrown, but nothing was thrown` | `AtendimentoBuilder.construir`, linha ~41: não validava nada. O comentário delegava a validação ao controller, que também não valida | `construir` lança `IllegalArgumentException` se nome ou porte forem nulos ou em branco, antes de chamar a Factory | Padrão Builder (Aula 14), validação e fail fast (Aula 11) |
| bug04 | `AtendimentoFactoryTest.deveCriarTosaQuandoTipoForTosa`: o objeto criado era um `Banho`, não uma `Tosa` | `AtendimentoFactory.criar`, linha ~17: `case "TOSA" -> new Banho(...)` | `case "TOSA" -> new Tosa(...)` | Padrão Factory e polimorfismo (Aula 14) |
| bug05 | `devePreencherOsDadosDoPetNaConsulta`: `expected: <Mimi> but was: <null>` | `ConsultaVeterinaria`, construtor com parâmetros, linha ~17: chamava `super()` sem argumentos, então `Atendimento` nunca recebia os dados nem o status AGENDADO | `super(protocolo, petNome, petPorte, tutorNome, dataHora);` | Herança e construtores, chamada ao `super` (Aula 7) |
| bug06 | `AgendaServiceTest.deveRecusarAgendamentoComHorarioJaOcupado`: esperava `HorarioOcupadoException` e veio `NullPointerException` (o agendamento duplicado passou e o `save` do mock devolveu `null`) | `AgendaService.agendar`, linha ~23: comparava nome e `LocalDateTime` com `==` (referência) em vez de `.equals()` | `a.getPetNome().equals(novo.getPetNome()) && a.getDataHora().equals(novo.getDataHora())` | `==` vs `.equals()` (Aula 7) |
| bug07 | `deveLancarExcecaoQuandoAtendimentoNaoExiste`: `Expected AtendimentoNaoEncontradoException to be thrown, but nothing was thrown` | `AgendaService.buscarPorId`, linha ~40: `catch (Exception e) { return null; }` engolia a exceção lançada pelo `orElseThrow` | Remoção do `try/catch`, deixando o `orElseThrow` propagar a exceção | Tratamento de exceções, catch genérico e exceções unchecked (Aula 11) |
| bug08 | Teste novo `BanhoPrecoTest`: `expected: <60.0> but was: <100.0>` | `Banho.calcularPreco`, linha ~27: os preços de PEQUENO (100) e GRANDE (60) estavam invertidos em relação ao contrato | PEQUENO = 60, MEDIO = 80, demais = 100 | Regras de negócio no model e polimorfismo (Aula 14), testes como contrato (Aula 15) |
| bug09 | Teste novo `TosaDuracaoTest`: `expected: <60> but was: <30>` | `Tosa`, linha ~40: `getDuracaoMinutos(String porte)` tinha assinatura diferente da classe pai, então era sobrecarga e não sobrescrita. A Tosa continuava usando o método herdado (30) | `@Override public int getDuracaoMinutos()` sem parâmetro, devolvendo 60 | Sobrescrita (override) vs sobrecarga (overload) e `@Override` (Aula 7) |
| bug10 | Teste novo `deveRecusarCancelamentoQuandoAtendimentoJaConcluido`: `Expected StatusInvalidoException to be thrown, but nothing was thrown` | `Atendimento.cancelar`, linha ~63: trocava o status para CANCELADO sem validar o status atual, ao contrário do `concluir()` | `cancelar()` lança `StatusInvalidoException` se o status não for AGENDADO | Regras de negócio no model e exceções customizadas (Aulas 11 e 14) |
| bug11 | Teste novo `deveRecusarAgendamentoQuandoDataHoraForNoPassado`: esperava `IllegalArgumentException` e veio `NullPointerException` (o agendamento no passado chegou até o `save`) | `AgendaService.agendar`, linha ~20: não validava a data/hora antes de consultar e salvar | Validação no início do método: se `dataHora` for anterior a `LocalDateTime.now()`, lança `IllegalArgumentException` antes de acessar o repository | Regras de negócio no service, fail fast e exceções (Aulas 11 e 13) |
| bug12 | A API sobe normalmente, mas `POST /api/atendimentos?tipo=BANHO&petNome=Rex&porte=PEQUENO&tutorNome=Ana&dataHora=2026-12-01T10:00` devolvia HTTP 500. O Console mostrava `Identifier of entity 'Banho' must be manually assigned before calling 'persist()'`. A suíte não detectava, porque o repository é mockado | `Atendimento`, linha ~14: o campo `@Id private Long id` não tinha `@GeneratedValue`, então ninguém gerava o id no `save` | `@GeneratedValue(strategy = GenerationType.IDENTITY)`. Depois da correção, o mesmo POST devolve HTTP 201 com `"id":1` | Persistência com JPA e mapeamento de entidades (Aula 13); limite do teste unitário com mock (Aula 15) |

## Parte 2 — Ajustes de Clean Code

| # | Onde estava | Qual princípio/boas práticas era violado | O que eu mudei |
|---|---|---|---|
| clean01 | `AtendimentoFactory.criar`: parâmetros `p`, `t`, `n`, `po`, `tu`, `d` | Nomes significativos e que revelam a intenção | Renomeei para `protocolo`, `tipo`, `petNome`, `petPorte`, `tutorNome`, `dataHora` |
| clean02 | `AtendimentoController`: método privado `calcularDescontoFidelidade`, nunca chamado, com comentário de funcionalidade futura | Código morto e comentário que descreve o que não existe | Removi o método e o bloco de comentário |
| clean03 | `AgendaService.agendar`: o recibo era impresso com `System.out.println` | Uso de logger em vez de saída padrão (boas práticas de logging) | Troquei por `log.info(...)` com um `Logger` SLF4J, que já vem no Spring Boot (sem mexer no `pom.xml`) |
| clean04 | `GeradorProtocolo`: o comentário dizia "Thread-safe", mas `getInstancia()` e `proximo()` não eram sincronizados | Comentário que descreve o que o código não faz; Singleton sem proteção contra concorrência | Marquei `getInstancia()` e `proximo()` como `synchronized` |
| clean05 | `Atendimento` e `AgendaService`: as strings `"AGENDADO"`, `"CONCLUIDO"` e `"CANCELADO"` repetidas pelo código | Valores mágicos (magic strings) e duplicação (DRY) | Criei as constantes `STATUS_AGENDADO`, `STATUS_CONCLUIDO` e `STATUS_CANCELADO` em `Atendimento` e passei a usá-las nos dois arquivos |
| clean06 | `Banho.calcularPreco` e `Tosa.calcularPreco`: preços como números soltos (60.0, 80.0, 100.0 e 70.0, 90.0, 120.0) | Números mágicos | Criei as constantes `PRECO_PEQUENO`, `PRECO_MEDIO` e `PRECO_GRANDE` em cada classe e passei a usá-las no cálculo |

## Parte 3 — Testes novos (regras que estavam sem cobertura)

| # | Teste escrito (classe.método) | Regra coberta | Resultado ao escrever (vermelho/verde) |
|---|---|---|---|
| teste01 | `BanhoPrecoTest.deveCustar60ReaisQuandoPorteForPequeno` | Banho de porte PEQUENO custa R$ 60,00 | Vermelho: `expected: <60.0> but was: <100.0>` (revelou o bug08) |
| teste02 | `TosaDuracaoTest.deveDurar60MinutosQuandoForTosa` | A tosa dura 60 minutos | Vermelho: `expected: <60> but was: <30>` (revelou o bug09) |
| teste03 | `AgendaServiceRegrasTest.deveRecusarCancelamentoQuandoAtendimentoJaConcluido` | Cancelar um atendimento já CONCLUÍDO é recusado com `StatusInvalidoException` | Vermelho: nenhuma exceção foi lançada (revelou o bug10) |
| teste04 | `AgendaServiceRegrasTest.deveRecusarAgendamentoQuandoDataHoraForNoPassado` | Agendar com data/hora no passado é recusado com `IllegalArgumentException`, sem consultar o banco | Vermelho: veio `NullPointerException` (revelou o bug11) |
| teste05 | `ConsultaPrecoTest.deveCustar150ReaisQuandoPorteForGrande` | A consulta veterinária custa R$ 150,00 fixo, e o porte não muda o preço | Verde de cara (a regra já estava correta; o teste protege contra regressão) |
| teste06 | `AgendaServiceRegrasTest.deveRecusarConclusaoQuandoAtendimentoCancelado` | Concluir um atendimento CANCELADO é recusado com `StatusInvalidoException` | Verde de cara (a regra já estava correta; o teste protege contra regressão) |

---

## Parte 4 — Perguntas de reflexão

### 1. A suíte como contrato (Aula 15)
Começamos com 20 testes e 9 vermelhos e usamos a primeira linha do Failure Trace de cada um como pista. O `expected: <GeradorProtocolo@389c4eb1> but was: <GeradorProtocolo@3fc79729>` mostrou dois objetos diferentes e levou ao `getInstancia()`, que nunca guardava a instância. O `expected: <Rex> but was: <null>` levou ao `comPet` do Builder, onde faltava o `this`. A suíte é melhor que testar na mão com curl porque roda em segundos, sem banco nem rede, é repetível e avisa na hora se uma correção quebrou outra regra. Ela também tem limite: o bug12 (falta de `@GeneratedValue`) passou despercebido pelos 26 testes e só apareceu quando subimos a API e o POST devolveu 500.

### 2. Mock e injeção de dependência (Aulas 13 a 15)
No `AgendaService`, o `repository` tem `@Autowired`: em produção, o container do Spring cria o `AtendimentoRepository` real e o coloca no campo. No `AgendaServiceTest`, o `@Mock` cria um repository falso e o `@InjectMocks` faz o papel do Spring: o Mockito instancia o `AgendaService` e injeta o falso no campo. Com `when(repository.findByPetNome("Rex")).thenReturn(...)` ensinamos o falso a responder o que o teste precisa. O teste roda sem banco e sem subir o Spring porque o `AgendaService` é uma classe Java comum que depende só da interface do repository, e não importa se a implementação é real ou falsa. Pelo mesmo motivo o bug12 não aparecia: o `save` do mock nunca chega ao Hibernate.

### 3. `==` vs `.equals()` (Aula 7)
No `AgendaService.agendar`, o conflito de horário comparava `a.getPetNome() == novo.getPetNome()` e `a.getDataHora() == novo.getDataHora()`. O `==` compara se os dois lados são o mesmo objeto na memória, e não se têm o mesmo valor. No teste, o novo horário vem de `LocalDateTime.parse(...)`, que cria outro objeto com a mesma data e hora, então o `==` dava `false` e o conflito passava. Com `"Rex"` o `==` funcionaria por sorte, porque literais iguais compartilham o mesmo objeto no pool de Strings; mas na API o nome chega por `@RequestParam`, uma String nova a cada requisição, e o `==` também falharia. A correção foi usar `.equals()` nas duas comparações, que compara o conteúdo.

### 4. Sobrescrita vs sobrecarga (Aula 7)
A classe `Atendimento` tem `getDuracaoMinutos()` sem parâmetros, que devolve 30. Na `Tosa` havia `getDuracaoMinutos(String porte)`, que devolve 60. Como a assinatura é diferente, isso é sobrecarga (overload): um método novo, que ninguém chamava. A sobrescrita (override) exige a mesma assinatura da classe pai. Por isso `tosa.getDuracaoMinutos()` continuava usando o método herdado e devolvia 30, e o código compilava normalmente. Com `@Override`, o compilador acusaria na hora que o método não sobrescreve nada. A correção foi `@Override public int getDuracaoMinutos()` sem parâmetro, como já era no `Banho`.

### 5. Singleton manual vs bean do Spring (Aula 14)
O `GeradorProtocolo` garante uma única instância e, com ela, um contador global de protocolos sequenciais (1, 2, 3...). O bug estava no `getInstancia()`: com `instancia` nula, ele fazia `return new GeradorProtocolo()` sem guardar o objeto, então cada chamada criava um gerador novo com o contador zerado e todo protocolo seria 1. O `AgendaService`, com `@Service`, não corre esse risco porque o Spring cria um único bean por contexto (escopo singleton por padrão) e injeta o mesmo objeto onde ele é pedido; não existe um `getInstancia()` manual para esquecermos de guardar a instância. Já o `GeradorProtocolo` guarda estado (`contador`), e por isso sincronizamos `getInstancia()` e `proximo()` no clean04.

### 6. Cobertura de testes: onde parar? (Aula 15)
Dos 6 testes novos, 4 ficaram vermelhos e revelaram os bugs 08 a 11; os outros 2 (`ConsultaPrecoTest` e `deveRecusarConclusaoQuandoAtendimentoCancelado`) ficaram verdes de cara. Vale manter os verdes: eles documentam o contrato e quebram se alguém mudar a regra sem querer, por exemplo fazendo a consulta depender do porte. Num projeto real com prazo, eu cobriria primeiro o caminho feliz das regras principais (agendar, preço, status) e logo depois os caminhos de erro, onde ficaram vários dos nossos bugs (cancelar concluído, data no passado, id inexistente). Perseguir 100% de cobertura não é a meta: o projeto tinha testes verdes e o POST ainda dava 500 por causa do bug12, que um teste com mock não vê. Por isso vale ter também pelo menos um teste de integração com o banco.