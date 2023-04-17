package dev.wakandaacademy.produdoro.usuario.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

@DisplayName("Testes unitários da classe UsuarioServiceTest")
class UsuarioServiceTest {

	public static final String TOKEN_VALIDO = "tokenValido@email.com";
	public static final String TOKEN_INVALIDO = "tokenInvalido@email.com";
	public static final UUID ID_USUARIO = UUID.fromString("0d51b6fe-ff69-4e36-a6ee-7b6983237872");
	public static final UUID ID_USUARIO_INVALIDO = UUID.fromString("fbd3b3e0-1484-4004-9564-808f1c3516bb");

	@InjectMocks
	private UsuarioService usuarioService;
	@Mock
    private UsuarioRepository usuarioRepository;
	private Usuario usuarioEmFoco;
	private Usuario usuarioEmPausaLonga;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		startData();
	}

	private void startData() {
		usuarioEmFoco = Usuario.builder()
				.idUsuario(ID_USUARIO)
				.email(TOKEN_VALIDO)
				.configuracao(null)
				.status(StatusUsuario.FOCO)
				.quantidadePomodorosPausaCurta(1)
				.build();
		usuarioEmPausaLonga = Usuario.builder()
				.idUsuario(ID_USUARIO)
				.email(TOKEN_VALIDO)
				.configuracao(null)
				.status(StatusUsuario.PAUSA_LONGA)
				.quantidadePomodorosPausaCurta(1)
				.build();		
	}
	
	@Test
	@DisplayName("execuçao do metodo mudaStatusPausaLonga para um Usuario com Status Foco")
	void mudaStatusPausaLonga_idUsuarioValido_TokenValido_usuarioFocoAlteraStatusPausaLonga() {
		when(usuarioRepository.buscaUsuarioPorId(ID_USUARIO)).thenReturn(usuarioEmFoco);
		usuarioEmFoco.equals(TOKEN_VALIDO);
		usuarioEmFoco.mudaStatusPausaLonga();
		usuarioRepository.salva(usuarioEmFoco);
		usuarioService.mudaStatusPausaLonga(TOKEN_VALIDO, ID_USUARIO);
		assertEquals(StatusUsuario.PAUSA_LONGA, usuarioEmFoco.getStatus());
	}
	
	@Test
	@DisplayName("execução do metodo mudaStatusParaPausaCurta para um Usuario com status Foco")
	void mudaStatusParaPausaCurta_idUsuarioValido_TokenValido_usuarioFocoAlteraStatusParaPausaCurta(){
		when(usuarioRepository.buscaUsuarioPorId(ID_USUARIO)).thenReturn(usuarioEmFoco);
		usuarioEmFoco.pausaCurta();
		usuarioRepository.salva(usuarioEmFoco);
		usuarioService.mudaStatusParaPausaCurta(TOKEN_VALIDO, ID_USUARIO);
		assertEquals(StatusUsuario.PAUSA_CURTA, usuarioEmFoco.getStatus());
	}
	
	@Test
	@DisplayName("execução do metodo mudaStatusParaPausaCurta para um Usuario com status Pausa_Longa")
	void mudaStatusParaPausaCurta_idUsuarioValido_TokenValido_usuarioPausaLongaAlteraStatusParaPausaCurta(){
		when(usuarioRepository.buscaUsuarioPorId(ID_USUARIO)).thenReturn(usuarioEmPausaLonga);
		usuarioEmPausaLonga.pausaCurta();
		usuarioRepository.salva(usuarioEmPausaLonga);
		usuarioService.mudaStatusParaPausaCurta(TOKEN_VALIDO, ID_USUARIO);
		assertEquals(StatusUsuario.PAUSA_CURTA, usuarioEmPausaLonga.getStatus());
	}
	
	@Test
	@DisplayName("execução do metodo mudaStatusParaPausaCurta para um Usuario com token inválido retornando uma APIException")
	void mudaStatusParaPausaCurta_idUsuarioValido_TokenInvalido_retornaAPIException(){

		try {
			when(usuarioRepository.buscaUsuarioPorId(ID_USUARIO)).thenReturn(usuarioEmPausaLonga);
			usuarioEmPausaLonga.pausaCurta();
			usuarioRepository.salva(usuarioEmPausaLonga);
		} catch (APIException e){
			assertThrows(APIException.class,
		            () -> usuarioService.mudaStatusParaPausaCurta(TOKEN_INVALIDO, ID_USUARIO));
		}
	}
	
	@Test
	@DisplayName("execução do metodo mudaStatusParaPausaCurta para um Usuario com id inválido retornando uma APIException")
	void mudaStatusParaPausaCurta_idUsuarioInvalido_TokenValido_retornaAPIException(){
		when(usuarioRepository.buscaUsuarioPorId(ID_USUARIO_INVALIDO)).thenThrow(APIException.class);
		usuarioEmPausaLonga.pausaCurta();
		usuarioRepository.salva(usuarioEmPausaLonga);
		assertThrows(APIException.class,
				() -> usuarioService.mudaStatusParaPausaCurta(TOKEN_VALIDO, ID_USUARIO_INVALIDO));
	}
	
	@Test
	@DisplayName("execução do metodo mudaStatusParaPausaCurta para um Usuario com id inválido e um token Invalido retornando uma APIException")
	void mudaStatusParaPausaCurta_idUsuarioInvalido_TokenInvalido_retornaAPIException(){
		try {
			when(usuarioRepository.buscaUsuarioPorId(ID_USUARIO_INVALIDO)).thenThrow(APIException.class);
			usuarioEmPausaLonga.pausaCurta();
			usuarioRepository.salva(usuarioEmPausaLonga);
		} catch (APIException e){
			assertThrows(APIException.class,
					() -> usuarioService.mudaStatusParaPausaCurta(TOKEN_INVALIDO, ID_USUARIO_INVALIDO));
		}
	}	
}
