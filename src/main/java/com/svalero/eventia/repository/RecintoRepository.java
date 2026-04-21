package com.svalero.eventia.repository;

import com.svalero.eventia.domain.Recinto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface RecintoRepository extends CrudRepository<Recinto, Long> {

    List<Recinto> findAll();


    @Query("select r from Recinto r where " +
            "(:nombre is null or lower(r.nombre) like lower(concat('%', :nombre, '%'))) and " +
            "(:ciudad is null or lower(r.ciudad) like lower(concat('%', :ciudad, '%'))) and " +
            "(:cubierto is null or r.cubierto = :cubierto)")

    List<Recinto> findByFilters(@Param("nombre") String nombre,
                                     @Param("ciudad") String ciudad,
                                     @Param("cubierto") Boolean cubierto);
}