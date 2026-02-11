package br.gov.inep.censo.service;

import br.gov.inep.censo.model.Usuario;
import br.gov.inep.censo.repository.UsuarioRepository;
import br.gov.inep.censo.spring.SpringBridge;
import br.gov.inep.censo.util.PasswordUtil;

import java.sql.SQLException;

/**
 * Servico de autenticacao para manter a regra fora do servlet.
 */
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService() {
        this(SpringBridge.getBean(UsuarioRepository.class));
    }

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Autentica usuario por login/senha.
     *
     * @param login login informado
     * @param senha senha em texto plano
     * @return usuario quando autenticado; {@code null} caso contrario
     * @throws SQLException erro de acesso a dados
     */
    public Usuario autenticar(String login, String senha) throws SQLException {
        String loginLimpo = login == null ? null : login.trim();
        if (loginLimpo == null || loginLimpo.length() == 0 || senha == null || senha.trim().length() == 0) {
            return null;
        }

        if (usuarioRepository != null) {
            return autenticarComRepository(loginLimpo, senha);
        }

        throw new SQLException("UsuarioRepository indisponivel para autenticacao.");
    }

    private Usuario autenticarComRepository(String login, String senha) throws SQLException {
        try {
            Usuario usuario = usuarioRepository.findByLogin(login);
            if (usuario == null || !usuario.isAtivo()) {
                return null;
            }

            String senhaHash = usuario.getSenhaHash();
            if (senhaHash == null || !PasswordUtil.verifyPassword(senha, senhaHash)) {
                return null;
            }

            if (PasswordUtil.needsRehash(senhaHash)) {
                usuario.setSenhaHash(PasswordUtil.hashPassword(senha));
                usuarioRepository.save(usuario);
            }

            Usuario autenticado = new Usuario();
            autenticado.setId(usuario.getId());
            autenticado.setLogin(usuario.getLogin());
            autenticado.setNome(usuario.getNome());
            autenticado.setAtivo(usuario.isAtivo());
            return autenticado;
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SQLException) {
                throw (SQLException) cause;
            }
            throw new SQLException("Falha ao autenticar usuario via repository.", e);
        }
    }

}
