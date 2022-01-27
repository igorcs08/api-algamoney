package com.algaworks.algamoney.api.service;

import java.util.Optional;

import javax.validation.Valid;

import com.algaworks.algamoney.api.model.Lancamento;
import com.algaworks.algamoney.api.model.Pessoa;
import com.algaworks.algamoney.api.repository.LancamentoRepository;
import com.algaworks.algamoney.api.repository.PessoaRepository;
import com.algaworks.algamoney.api.service.exception.PessoaInexistenteOuInativaException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LancamentoService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private LancamentoRepository lancamentoRepository;

    public Lancamento salvar(@Valid Lancamento lancamento) {
        Optional<Pessoa> pessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
        if (pessoa.get() == null || pessoa.get().isInativo()) {
            throw new PessoaInexistenteOuInativaException();
        }
        return lancamentoRepository.save(lancamento);
    }

    public Lancamento atualizar(Long codigo, Lancamento lancamento) {
      Lancamento lancamentoSalvo = this.buscarLancamentoExistente(codigo);
      if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
        validarPessoa(lancamento);
      }

      BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");

      return lancamentoRepository.save(lancamentoSalvo);
    }

    public void validarPessoa(Lancamento lancamento) {
      Optional<Pessoa> pessoa = null;
      if (lancamento.getPessoa().getCodigo() != null) {
        pessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
      }

      if (pessoa.isEmpty() || pessoa.get().isInativo()) {
        throw new PessoaInexistenteOuInativaException();
      }
    }

    private Lancamento buscarLancamentoExistente(Long codigo) {
      return lancamentoRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException());
    }

}
