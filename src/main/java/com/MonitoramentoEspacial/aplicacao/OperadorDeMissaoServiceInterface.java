package com.MonitoramentoEspacial.aplicacao;

import com.MonitoramentoEspacial.interfaceExterna.CriarOperadorRequest;
import com.MonitoramentoEspacial.interfaceExterna.OperadorDeMissaoDTO;

import java.util.List;

public interface OperadorDeMissaoServiceInterface {
    OperadorDeMissaoDTO criarOperador(CriarOperadorRequest request);
    OperadorDeMissaoDTO atualizarOperador(Long id, CriarOperadorRequest request);
    OperadorDeMissaoDTO buscarPorId(Long id);
    List<OperadorDeMissaoDTO> listarOperadores(String nome);
    void deletarOperador(Long id);
}
