package com.MonitoramentoEspacial.aplicacao;

import com.MonitoramentoEspacial.interfaceExterna.AtualizarUsuarioAcessoRequest;
import com.MonitoramentoEspacial.interfaceExterna.CriarUsuarioAcessoRequest;
import com.MonitoramentoEspacial.interfaceExterna.UsuarioAcessoDTO;

import java.util.List;

public interface UsuarioAcessoServiceInterface {
    UsuarioAcessoDTO criarUsuario(CriarUsuarioAcessoRequest request);
    UsuarioAcessoDTO atualizarUsuario(Long id, AtualizarUsuarioAcessoRequest request);
    List<UsuarioAcessoDTO> listarUsuarios();
    UsuarioAcessoDTO buscarPorId(Long id);
    void deletarUsuario(Long id);
}
