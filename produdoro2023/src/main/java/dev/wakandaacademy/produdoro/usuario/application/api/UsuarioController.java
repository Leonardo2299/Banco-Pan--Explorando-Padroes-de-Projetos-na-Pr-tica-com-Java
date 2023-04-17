package dev.wakandaacademy.produdoro.usuario.application.api;

import java.util.UUID;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import dev.wakandaacademy.produdoro.config.security.service.TokenService;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.service.UsuarioApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Validated
@Log4j2
@RequiredArgsConstructor
public class UsuarioController implements UsuarioAPI {
	private final UsuarioApplicationService usuarioAppplicationService;
	private final TokenService tokenService;

	@Override
	public UsuarioCriadoResponse postNovoUsuario(@Valid UsuarioNovoRequest usuarioNovo) {
		log.info("[start] UsuarioController - postNovoUsuario");
		UsuarioCriadoResponse usuarioCriado = usuarioAppplicationService.criaNovoUsuario(usuarioNovo);
		log.info("[finish] UsuarioController - postNovoUsuario");
		return usuarioCriado;
	}

	@Override
	public UsuarioCriadoResponse buscaUsuarioPorId(UUID idUsuario) {
		log.info("[start] UsuarioController - buscaUsuarioPorId");
		log.info("[idUsuario] {}", idUsuario);
		UsuarioCriadoResponse buscaUsuario = usuarioAppplicationService.buscaUsuarioPorId(idUsuario);
		log.info("[finish] UsuarioController - buscaUsuarioPorId");
		return buscaUsuario;
	}
	@Override
	public void mudaStatusParaPausaCurta(String token, UUID idUsuario) {
		log.info("[start] UsuarioController - mudaStatusParaPausaCurta");
		String usuarioEmail = getUsuarioByToken(token);
		usuarioAppplicationService.mudaStatusParaPausaCurta(usuarioEmail, idUsuario);
		log.info("[finish] UsuarioController - mudaStatusParaPausaCurta");
	}
	
	private String getUsuarioByToken(String token) {
		log.debug("[token] {}", token);
		String usuario = tokenService.getUsuarioByBearerToken(token).orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, token));
		log.info("[usuario] {}", usuario);
		return usuario;
	}

	@Override
	public void mudaStatusPausaLonga(String token, UUID idUsuario) {
		log.info("[start] UsuarioController - mudaStatusPausaLonga");
		String usuario = getUsuarioByToken(token);
		usuarioAppplicationService.mudaStatusPausaLonga(usuario, idUsuario);
		log.info("[finish] UsuarioController - mudaStatusPausaLonga");
	}
}