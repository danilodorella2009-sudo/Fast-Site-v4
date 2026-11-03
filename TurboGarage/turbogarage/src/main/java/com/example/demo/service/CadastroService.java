package com.example.demo.service;

import com.example.demo.dto.CadastroRequest;
import com.example.demo.model.Cadastro;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CadastroRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.util.SenhaUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * SERVICE: regras de negócio dos cadastros de clientes.
 */
@Service
public class CadastroService {

    private final CadastroRepository cadastroRepository;
    private final UsuarioRepository usuarioRepository;

    public CadastroService(CadastroRepository cadastroRepository, UsuarioRepository usuarioRepository) {
        this.cadastroRepository = cadastroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Salva o cadastro E cria o login da pessoa (email + senha em BCrypt).
     * @Transactional: se uma das duas gravações falhar, nenhuma fica salva.
     */
    @Transactional
    public Cadastro salvar(CadastroRequest dados) {
        String email = dados.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este email já possui cadastro");
        }

        // 1) Login: senha CRIPTOGRAFADA, nunca em texto puro
        usuarioRepository.save(new Usuario(email, SenhaUtil.criptografar(dados.senha()), Usuario.CLIENTE));

        // 2) Dados do cliente
        Cadastro c = new Cadastro();
        c.setNome(dados.nome().trim());
        c.setEmail(email);
        c.setEndereco(dados.endereco().trim());
        c.setCep(formatarCep(dados.cep()));
        c.setNumero(dados.numero().trim());
        c.setTelefone(dados.telefone().trim());
        c.setObservacao(dados.observacao().trim());
        return cadastroRepository.insert(c);
    }

    public List<Cadastro> listar() {
        return cadastroRepository.findAtivos();
    }

    public Cadastro buscarPorId(Long id) {
        return cadastroRepository.findAtivoById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cadastro #" + id + " não encontrado"));
    }

    // Desafio 3: soft delete. O cliente excluído também perde o login.
    @Transactional
    public void inativar(Long id) {
        Cadastro c = buscarPorId(id); // dá 404 se não existir
        cadastroRepository.inativar(id);
        if (c.getEmail() != null) {
            usuarioRepository.excluirCliente(c.getEmail());
        }
    }

    // Guarda o CEP sempre no formato 00000-000
    private String formatarCep(String cep) {
        String digitos = cep.replaceAll("\\D", "");
        return digitos.substring(0, 5) + "-" + digitos.substring(5);
    }
}
