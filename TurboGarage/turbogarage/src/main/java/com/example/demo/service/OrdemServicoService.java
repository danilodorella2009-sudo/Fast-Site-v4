package com.example.demo.service;

import com.example.demo.dto.OrdemServicoRequest;
import com.example.demo.model.OrdemServico;
import com.example.demo.repository.OrdemServicoRepository;
import com.example.demo.repository.VeiculoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.RoundingMode;
import java.util.List;

/**
 * SERVICE: regras de negócio das ordens de serviço.
 */
@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemRepository;
    private final VeiculoRepository veiculoRepository;

    public OrdemServicoService(OrdemServicoRepository ordemRepository, VeiculoRepository veiculoRepository) {
        this.ordemRepository = ordemRepository;
        this.veiculoRepository = veiculoRepository;
    }

    public List<OrdemServico> listar() {
        return ordemRepository.findAll();
    }

    public OrdemServico buscar(Long id) {
        return ordemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de serviço #" + id + " não encontrada"));
    }

    public OrdemServico criar(OrdemServicoRequest dados) {
        Long id = ordemRepository.insert(montar(dados));
        return buscar(id);
    }

    public OrdemServico atualizar(Long id, OrdemServicoRequest dados) {
        buscar(id); // 404 se não existir
        ordemRepository.update(id, montar(dados));
        return buscar(id);
    }

    public void excluir(Long id) {
        buscar(id);
        ordemRepository.delete(id);
    }

    private OrdemServico montar(OrdemServicoRequest dados) {
        if (veiculoRepository.findById(dados.veiculoId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veículo não encontrado");
        }
        OrdemServico o = new OrdemServico();
        o.setVeiculoId(dados.veiculoId());
        o.setDefeito(dados.defeito().trim());
        o.setValor(dados.valor().setScale(2, RoundingMode.HALF_UP));
        o.setStatus(dados.status());
        return o;
    }
}
