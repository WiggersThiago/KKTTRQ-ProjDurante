package br.com.patinhas.repository;

import br.com.patinhas.entity.Usuario;
import br.com.patinhas.entity.enums.RoleUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailAndAtivoTrue(String email);

    boolean existsByEmail(String email);

    List<Usuario> findAllByRoleAndAtivoTrue(RoleUsuario role);
}
