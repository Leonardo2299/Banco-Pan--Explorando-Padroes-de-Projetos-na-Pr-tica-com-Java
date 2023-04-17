package dev.wakandaacademy.produdoro.tarefa.application.service;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaResponse;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class TarefaApplicationService implements TarefaService {
	private final TarefaRepository tarefaRepository;
	private final UsuarioRepository usuarioRepository;

	@Override
	public TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest) {
		log.info("[start] TarefaApplicationService - criaNovaTarefa");
		Tarefa tarefaCriada = tarefaRepository.salva(new Tarefa(tarefaRequest));
		log.info("[finish] TarefaApplicationService - criaNovaTarefa");
		return TarefaIdResponse.builder().idTarefa(tarefaCriada.getIdTarefa()).build();
	}

	@Override
	public Tarefa detalhaTarefa(String usuario, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - detalhaTarefa");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		log.info("[usuarioPorEmail] {}", usuarioPorEmail);
		Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
				.orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
		tarefa.pertenceAoUsuario(usuarioPorEmail);
		log.info("[finaliza] TarefaApplicationService - detalhaTarefa");
		return tarefa;
	}

	@Override
	public void deletaTarefa(String usuario, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - deletaTarefa");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
	    Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
		  .orElseThrow(() -> APIException.build(HttpStatus.BAD_REQUEST, "Tarefa não encontrada!"));
	    tarefa.pertenceAoUsuario(usuarioPorEmail);
		tarefaRepository.deletaTarefa(idTarefa);
		log.info("[finaliza] TarefaApplicationService - deletaTarefa");
	}

	@Override
	public void editaDescricaoDaTarefa(String usuario, @Valid UUID idTarefa, TarefaRequest tarefaRequestEditada) {
		log.info("[inicia] TarefaApplicationService - editaDescricaoDaTarefa");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		log.info("[usuarioPorEmail]{}", usuarioPorEmail);
		Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
				.orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
		tarefa.pertenceAoUsuario(usuarioPorEmail);
		tarefa.edita(tarefaRequestEditada);
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - editaDescricaoDaTarefa");
	}
	
	@Override
	public Tarefa ativaTarefa(UUID idUsuario, UUID idTarefa, String usuario) {
		log.info("[inicia] TarefaApplicationService - ativaTarefa");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
				.orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
		tarefa.pertenceAoUsuario(usuarioPorEmail);
		tarefa.validaIdUsuario(idUsuario);
		tarefaRepository.desativaTarefas(idUsuario);
		tarefa.ativaTarefa();
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - ativaTarefa");
		return tarefa;
	}

	@Override
	public List<TarefaResponse> buscaTarefasPorUsuario(String usuario, UUID idUsuario) {
		log.info("[inicia] TarefaApplicationService - buscaTarefasPorUsuario");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		validaIdUsuario(usuarioPorEmail.getIdUsuario(), idUsuario);
		List<Tarefa> tarefas = tarefaRepository.buscaTarefasPorUsuario(idUsuario);
		log.info("[finaliza] TarefaApplicationService - buscaTarefasPorUsuario");
		return TarefaResponse.parseToList(tarefas);
	}
	
	private void validaIdUsuario(UUID idUsuario1, UUID idUsuario2) {
		log.info("[inicia] TarefaApplicationService - validaIdUsuario");
		if (!idUsuario1.equals(idUsuario2)) {
			throw APIException.build(HttpStatus.UNAUTHORIZED, "ID Usuario incorreto!!!!");
		}
		log.info("[finaliza] TarefaApplicationService - validaIdUsuario");
	}

    @Override
    public void incrementaPomodoro(Tarefa tarefa) {
        log.info("[inicia] TarefaApplicationService - incrementaPomodoro");
        tarefa.contagemPomodoro();
        tarefaRepository.salva(tarefa);
        log.info("[finaliza] TarefaApplicationService - incrementaPomodoro");
    }
}
