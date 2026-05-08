package br.com.patinhas.service;

import br.com.patinhas.dto.request.EventoRequestDTO;
import br.com.patinhas.dto.response.EventoResponseDTO;
import br.com.patinhas.entity.Evento;
import br.com.patinhas.exception.ResourceNotFoundException;
import br.com.patinhas.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;

    @Transactional(readOnly = true)
    public List<EventoResponseDTO> listarProximos() {
        return eventoRepository.buscarProximosEventos(LocalDateTime.now()).stream()
                .map(EventoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoResponseDTO> listarTodosAtivos() {
        return eventoRepository.findAllByAtivoTrueOrderByDataEventoAsc().stream()
                .map(EventoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoResponseDTO> listarAdmin() {
        return eventoRepository.findAllByOrderByDataEventoDesc().stream()
                .map(EventoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventoResponseDTO buscarPorId(Long id) {
        return EventoResponseDTO.fromEntity(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Evento buscarEntidade(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", id));
    }

    @Transactional
    public EventoResponseDTO cadastrar(EventoRequestDTO dto) {
        Evento evento = Evento.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .local(dto.getLocal())
                .dataEvento(dto.getDataEvento())
                .ativo(dto.getAtivo() == null ? true : dto.getAtivo())
                .build();
        log.info("Cadastrando novo evento: {}", dto.getTitulo());
        return EventoResponseDTO.fromEntity(eventoRepository.save(evento));
    }

    @Transactional
    public EventoResponseDTO atualizar(Long id, EventoRequestDTO dto) {
        Evento evento = buscarEntidade(id);
        evento.setTitulo(dto.getTitulo());
        evento.setDescricao(dto.getDescricao());
        evento.setLocal(dto.getLocal());
        evento.setDataEvento(dto.getDataEvento());
        if (dto.getAtivo() != null) {
            evento.setAtivo(dto.getAtivo());
        }
        return EventoResponseDTO.fromEntity(eventoRepository.save(evento));
    }

    @Transactional
    public void remover(Long id) {
        Evento evento = buscarEntidade(id);
        evento.setAtivo(false);
        eventoRepository.save(evento);
    }

    @Transactional
    public void removerDefinitivo(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evento", id);
        }
        eventoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long contarAtivos() {
        return eventoRepository.countByAtivoTrue();
    }
}
