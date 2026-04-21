package com.svalero.eventia.repository;

import com.svalero.eventia.domain.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    List<Usuario> findAll();


    @Query("select u from Usuario u where " +
            "(:nombre is null or lower(u.nombre) like lower(concat('%', :nombre, '%'))) and " +
            "(:email is null or lower(u.email) like lower(concat('%', :email, '%'))) and " +
            "(:rol is null or lower(u.rol) like lower(concat('%', :rol, '%')))")

    List<Usuario> findByFilters(@Param("nombre") String nombre,
                              @Param("email") String email,
                              @Param("rol") String rol);

}