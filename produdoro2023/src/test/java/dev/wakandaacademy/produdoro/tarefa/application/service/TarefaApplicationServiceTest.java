package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaResponse;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.springframework.http.HttpStatus;

@DisplayName("Testes unitários da classe TarefaApplicationServiceTest")
class TarefaApplicationServiceTest {
	
	@InjectMocks
	private TarefaApplicationService tarefaApplicationService;
	@Mock
    private TarefaRepository tarefaRepository;
	@Mock
    private UsuarioRepository usuarioRepository;
	
	public static final String TOKEN_VALIDO = "tokenValido@email.com";
	public static final String TOKEN_INVALIDO = "tokenInvalido@email.com";
	public static final UUID ID_TAREFA_VALIDO = UUID.fromString("8d58875d-2455-4075-8b5d-57c73fcf1241");
	public static final UUID ID_TAREFA_INVALIDO = UUID.fromString("b92ee6fa-9ae9-45ac-afe0-fb8e4460d839");
	public static final UUID ID_USUARIO_VALIDO = UUID.fromString("0d51b6fe-ff69-4e36-a6ee-7b6983237872");
	public static final UUID ID_USUARIO_INVALIDO = UUID.fromString("fbd3b3e0-1484-4004-9564-808f1c3516bb");
	public static final UUID ID_AREA = UUID.fromString("462ff63d-412b-4a19-9c43-bc5969b15989");
	public static final UUID ID_PROJETO = UUID.fromString("1fc65f65-4862-4598-a30c-f317dfb3cbe7");
	
	private Optional<Tarefa> OptionalTarefaValidaEsperada;
	private Tarefa tarefaValidaEsperada;
	private Usuario usuarioValido;
	private Usuario usuarioInvalido;
	private TarefaRequest tarefaRequestValida;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		startData();
	}
	
    private void startData(){
    	tarefaValidaEsperada = Tarefa.builder()
    			.idTarefa(ID_TAREFA_VALIDO)
    			.descricao("descricao")
    			.idUsuario(ID_USUARIO_VALIDO)
    			.idArea(ID_AREA)
    			.idProjeto(ID_PROJETO)
    			.status(StatusTarefa.A_FAZER)
    			.statusAtivacao(StatusAtivacaoTarefa.INATIVA)
    			.contagemPomodoro(1)
    			.build();
    	
    	OptionalTarefaValidaEsperada = Optional.of(tarefaValidaEsperada);
    	
    	usuarioValido = Usuario.builder()
    			.idUsuario(ID_USUARIO_VALIDO)
    			.email(TOKEN_VALIDO)
    			.configuracao(null)
    			.status(StatusUsuario.FOCO)
    			.quantidadePomodorosPausaCurta(3)
    			.build();
    	
    	usuarioInvalido = Usuario.builder()
    			.idUsuario(ID_USUARIO_INVALIDO)
    			.email(TOKEN_INVALIDO)
    			.configuracao(null)
    			.status(StatusUsuario.FOCO)
    			.quantidadePomodorosPausaCurta(3)
    			.build();
    	
    	tarefaRequestValida = new TarefaRequest("descricaoEditada", ID_USUARIO_VALIDO, ID_AREA, ID_PROJETO, 1);
    }
	
	@Test
	@DisplayName("execução do metodo editaTarefa em uma tarefa válida de um usuario válido")
	void editaDescricaoDaTarefa_idTarefaValido_TokenValido_retornaUsuarioAtualizado(){
		when(usuarioRepository.buscaUsuarioPorEmail(TOKEN_VALIDO)).thenReturn(usuarioValido);
		when(tarefaRepository.buscaTarefaPorId(ID_TAREFA_VALIDO)).thenReturn(OptionalTarefaValidaEsperada);
		tarefaValidaEsperada.pertenceAoUsuario(usuarioValido);
		tarefaValidaEsperada.edita(tarefaRequestValida);
		tarefaRepository.salva(tarefaValidaEsperada);
		tarefaApplicationService.editaDescricaoDaTarefa(TOKEN_VALIDO, ID_TAREFA_VALIDO, tarefaRequestValida);
		assertEquals(tarefaRequestValida.getDescricao(), tarefaValidaEsperada.getDescricao());
	}
	
	@Test
	@DisplayName("execução do metodo editaTarefa em uma tarefa válida de um usuario inválido retornando uma APIException")
	void editaDescricaoDaTarefa_idTarefaValido_TokenInvalido_retornaAPIException(){
		try {
			when(usuarioRepository.buscaUsuarioPorEmail(TOKEN_INVALIDO)).thenReturn(usuarioInvalido);
			when(tarefaRepository.buscaTarefaPorId(ID_TAREFA_VALIDO)).thenReturn(OptionalTarefaValidaEsperada);
			tarefaValidaEsperada.pertenceAoUsuario(usuarioInvalido);
			tarefaValidaEsperada.edita(tarefaRequestValida);
			tarefaRepository.salva(tarefaValidaEsperada);
		} catch (APIException e) {
			assertThrows(APIException.class,
		            () -> tarefaApplicationService.editaDescricaoDaTarefa(TOKEN_INVALIDO, ID_TAREFA_VALIDO, tarefaRequestValida));
		}
	}
	
	@Test
	@DisplayName("execução do metodo editaTarefa em uma tarefa inválida de um usuario válido retornando uma APIException")
	void editaDescricaoDaTarefa_idTarefaInvalido_TokenValido_retornaAPIException(){
		when(usuarioRepository.buscaUsuarioPorEmail(TOKEN_VALIDO)).thenReturn(usuarioValido);
		when(tarefaRepository.buscaTarefaPorId(ID_TAREFA_INVALIDO)).thenThrow(APIException.class);

		assertThrows(APIException.class, () -> tarefaApplicationService.editaDescricaoDaTarefa(TOKEN_VALIDO, ID_TAREFA_INVALIDO, tarefaRequestValida));
	}
	
	@Test
	@DisplayName("execução do metodo editaTarefa em uma tarefa inválida de um usuario inválido retornando uma APIException")
	void editaDescricaoDaTarefa_idTarefaInvalido_TokenInvalido_retornaAPIException(){
		try {
			when(usuarioRepository.buscaUsuarioPorEmail(TOKEN_INVALIDO)).thenReturn(usuarioInvalido);
			when(tarefaRepository.buscaTarefaPorId(ID_TAREFA_INVALIDO)).thenThrow(APIException.class);
		} catch (APIException e) {
		assertThrows(APIException.class,
	            () -> tarefaApplicationService.editaDescricaoDaTarefa(TOKEN_INVALIDO, ID_TAREFA_INVALIDO, tarefaRequestValida));
		}
	}

	@Test
    void testDeletaTarefa() {

        Tarefa tarefa = getTarefa();
        Optional<Tarefa> ofResult = Optional.of(tarefa);
        when(tarefaRepository.buscaTarefaPorId((UUID) any())).thenReturn(ofResult);
        when(usuarioRepository.buscaUsuarioPorEmail((String) any())).thenReturn(getUser());
        tarefaApplicationService.deletaTarefa("Usuario", UUID.randomUUID());
}

	@Test
	void deveRetornaExceptionQuandoIdUsusarioDiferenteDoToken() throws Exception {
		
		//passando IdUsuario diferente do token
		UUID id = UUID.randomUUID();
		//Mockando a chamada ao metodo usuarioRepository            
         lenient().when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(DataHelper.createUsuario());
    	//Mockando a chamada ao metodo usuarioRepository  
         lenient().when(tarefaRepository.buscaTarefasPorUsuario(any(UUID.class))).thenReturn(DataHelper.createListTarefa());
    	 	    	 
		Exception exception = assertThrows(APIException.class, () -> {
			tarefaApplicationService.buscaTarefasPorUsuario("ususar", id);
		});

		assertTrue(exception.getMessage().contains("ID Usuario incorreto!!!!"));

	}
	
	@Test
    void deveRetornarListaTarefasDousuario() {
		UUID id = DataHelper.createUsuario().getIdUsuario();
		
		//Mockando a chamada ao metodo usuarioRepository            
    	when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(DataHelper.createUsuario());
    	//Mockando a chamada ao metodo usuarioRepository  
    	when(tarefaRepository.buscaTarefasPorUsuario(any(UUID.class))).thenReturn(DataHelper.createListTarefa());
    	 	    	 
    	List<TarefaResponse> retorno = tarefaApplicationService.buscaTarefasPorUsuario("ususar", id);
    	//verifica se retorno ñ é NULL
    	assertNotNull(retorno);
    	//verifica se o retorno eh do tipo ArrayList
    	assertEquals(ArrayList.class, retorno.getClass());
    	//verifica se lista ñ está vazia
//    	assertFalse(retorno.isEmpty());
    	
    }
	
    private Usuario getUser() {
        return Usuario.builder()
                .idUsuario(UUID.fromString("ca0e1b98-42c3-4b7f-98a1-b1c8d450d82e"))
                .email("lucasaquino350@gmail.com")
                .build();
    }

    private Tarefa getTarefa() {
        return Tarefa.builder()
                .contagemPomodoro(2)
                .descricao("Conclui teste")
                .idUsuario(UUID.fromString("ca0e1b98-42c3-4b7f-98a1-b1c8d450d82e"))
                .idArea(UUID.randomUUID())
                .build();
    }
    
    @Test
	void deveRetornarTarefaAtivada() {
		UUID idTarefa = DataHelper.createTarefa().getIdTarefa();
		UUID idUsuario = DataHelper.createUsuario().getIdUsuario();
		String email = "marcioavb.ms@gmail.com";

		when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(DataHelper.createUsuario());
		when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(DataHelper.createTarefa()));

		Tarefa retorno = tarefaApplicationService.ativaTarefa(idUsuario, idTarefa, email);
		verify(tarefaRepository, times(1)).buscaTarefaPorId(idTarefa);
		assertEquals(StatusAtivacaoTarefa.ATIVA, retorno.getStatusAtivacao());
	}

	@Test
	void testIncrementaPomodoro() {
		when(tarefaRepository.salva((Tarefa) any()))
				.thenThrow(APIException.build(HttpStatus.CONTINUE, "An error occurred"));

		Tarefa tarefa = getTarefa();
		assertThrows(APIException.class, () -> tarefaApplicationService.incrementaPomodoro(tarefa));
		verify(tarefaRepository).salva((Tarefa) any());
	}
}
