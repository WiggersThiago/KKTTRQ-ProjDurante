package br.com.patinhas.repository;

import br.com.patinhas.entity.Usuario;
import br.com.patinhas.entity.enums.RoleUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailAndAtivoTrue(String email);

    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    List<Usuario> findAllByRoleAndAtivoTrue(RoleUsuario role);
}
