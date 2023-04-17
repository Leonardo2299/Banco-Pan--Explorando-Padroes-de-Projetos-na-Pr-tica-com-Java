package dev.wakandaacademy.produdoro.tarefa.infra;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TarefaInfraRepository implements TarefaRepository {

	private final TarefaSpringMongoDBRepository tarefaSpringMongoDBRepository;
	private final MongoTemplate mongoTemplate;

	@Override
	public Tarefa salva(Tarefa tarefa) {
		try {
			tarefaSpringMongoDBRepository.save(tarefa);
		} catch (DataIntegrityViolationException e) {
			throw APIException.build(HttpStatus.BAD_REQUEST, "Tarefa já cadastrada", e);
		}
		log.info("[finaliza] TarefaInfraRepository - salva");
		return tarefa;
	}

	@Override
	public Optional<Tarefa> buscaTarefaPorId(UUID idTarefa) {
		log.info("[start] TarefaRepositoryMongoDB - buscaTarefaPorId");
		Optional<Tarefa> tarefaPorId = tarefaSpringMongoDBRepository.findByIdTarefa(idTarefa);
		log.info("[finish] TarefaRepositoryMongoDB - buscaTarefaPorId");
		return tarefaPorId;
	}

	@Override
	public void deletaTarefa(UUID idTarefa) {
		log.info("[start] TarefaRepositoryMongoDB - deletaTarefa");
		tarefaSpringMongoDBRepository.deleteById(idTarefa);
		log.info("[finaliza] TarefaRepositoryMongoDB - deletaTarefa");
	}

	@Override
	public void desativaTarefas(UUID idUsuario) {
		log.info("[start] TarefaRepositoryMongoDB - desativaTarefas");
		Query query = new Query();
		query.addCriteria(Criteria.where("idUsuario").is(idUsuario));
		Update update = new Update();
		update.set("statusAtivacao", "INATIVA");
		mongoTemplate.updateMulti(query, update, Tarefa.class);
		log.info("[finish] TarefaRepositoryMongoDB - desativaTarefas");
	}
    
    @Override
	public List<Tarefa> buscaTarefasPorUsuario(UUID idUsuario) {
		log.info("[inicia] TarefaRepositoryMongoDB - buscaTarefasPorUsuario");
		List<Tarefa> listaDeTarefas = tarefaSpringMongoDBRepository.findAllByIdUsuario(idUsuario);
		log.info("[finaliza] TarefaRepositoryMongoDB - buscaTarefasPorUsuario");
		return listaDeTarefas;
	}
}
