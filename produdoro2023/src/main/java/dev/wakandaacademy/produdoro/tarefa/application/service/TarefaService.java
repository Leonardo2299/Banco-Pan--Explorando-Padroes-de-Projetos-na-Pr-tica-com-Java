package dev.wakandaacademy.produdoro.tarefa.application.service;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaResponse;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
public interface TarefaService {

    TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);
    Tarefa detalhaTarefa(String usuario, UUID idTarefa);
	void deletaTarefa(String usuario, UUID idTarefa);
	void editaDescricaoDaTarefa(String usuario, @Valid UUID idTarefa, TarefaRequest tarefaRequestEditada);
	Tarefa ativaTarefa(UUID idUsuario, UUID idTarefa, String usuario);
	List<TarefaResponse> buscaTarefasPorUsuario(String usuario, UUID idUsuario);
    void incrementaPomodoro(Tarefa tarefa);
}
