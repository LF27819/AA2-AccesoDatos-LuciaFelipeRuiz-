package com.svalero.eventia.repository;

import com.svalero.eventia.domain.Evento;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface EventoRepository extends CrudRepository<Evento, Long> {

    List<Evento> findAll();


    @Query("select e from Evento e where " +
            "(:nombre is null or lower(e.nombre) like lower(concat('%', :nombre, '%'))) and " +
            "(:categoria is null or lower(e.categoria) like lower(concat('%', :categoria, '%'))) and " +
            "(:cancelado is null or e.cancelado = :cancelado)")

    List<Evento> findByFilters(@Param("nombre") String nombre,
                                    @Param("categoria") String categoria,
                                    @Param("cancelado") Boolean cancelado);


    @Query("select e from Evento e where e.cancelado = true")
    List<Evento> findCancelledEventos();
}