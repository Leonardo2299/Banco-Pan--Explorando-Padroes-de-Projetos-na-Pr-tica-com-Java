package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.wakandaacademy.produdoro.config.security.service.TokenService;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.service.TarefaService;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequiredArgsConstructor
public class TarefaRestController implements TarefaAPI {
	private final TarefaService tarefaService;
	private final TokenService tokenService;

	@Override
	public TarefaIdResponse postNovaTarefa(TarefaRequest tarefaRequest) {
		log.info("[inicia]  TarefaRestController - postNovaTarefa  ");
		TarefaIdResponse tarefaCriada = tarefaService.criaNovaTarefa(tarefaRequest);
		log.info("[finaliza]  TarefaRestController - postNovaTarefa");
		return tarefaCriada;
	}

	@Override
	public TarefaDetalhadoResponse detalhaTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - detalhaTarefa");
		String usuario = getUsuarioByToken(token);
		Tarefa tarefa = tarefaService.detalhaTarefa(usuario, idTarefa);
		log.info("[finaliza] TarefaRestController - detalhaTarefa");
		return new TarefaDetalhadoResponse(tarefa);
	}

	@Override
	public void incrementaPomodoro(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - incrementaPomodoro");
		String usuario = getUsuarioByToken(token);
		Tarefa tarefa = tarefaService.detalhaTarefa(usuario,idTarefa);
		tarefaService.incrementaPomodoro(tarefa);
		log.info("[finaliza] TarefaRestController - incrementaPomodoro");
	}

	private String getUsuarioByToken(String token) {
		log.debug("[token] {}", token);
		String usuario = tokenService.getUsuarioByBearerToken(token)
				.orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, token));
		log.info("[usuario] {}", usuario);
		return usuario;
	}

	@Override
	public void deletaTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - deletaTarefa");
		String usuario = getUsuarioByToken(token);
		tarefaService.deletaTarefa(usuario, idTarefa);
		log.info("[finaliza] TarefaRestController - deletaTarefa");
	}

	@Override
	public void editaTarefa(String token, @Valid UUID idTarefa, TarefaRequest tarefaRequestEditada) {
		log.info("[inicia] TarefaRestController - editaTarefa");
		String usuario = getUsuarioByToken(token);
		tarefaService.editaDescricaoDaTarefa(usuario, idTarefa, tarefaRequestEditada);
		log.info("[finaliza] TarefaRestController - editaTarefa");
	}
	
	@Override
	public void ativaTarefa(String token, UUID idUsuario, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - ativaTarefa");
		String usuario = getUsuarioByToken(token);
		tarefaService.ativaTarefa(idUsuario, idTarefa, usuario);
		log.info("[finaliza] TarefaRestController - ativaTarefa");
	}

	@Override
	public List<TarefaResponse> buscaTarefasPorUsuario(String token, UUID idUsuario) {
		log.info("[inicia] TarefaRestController - buscaTarefasPorUsuario");
		String usuario = getUsuarioByToken(token);
		List<TarefaResponse> tarefas = tarefaService.buscaTarefasPorUsuario(usuario, idUsuario);
		log.info("[finaliza] TarefaRestController - buscaTarefasPorUsuario");
		return tarefas;
	}
}