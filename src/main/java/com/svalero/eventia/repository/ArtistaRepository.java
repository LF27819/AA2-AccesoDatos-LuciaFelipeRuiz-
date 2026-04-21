package com.svalero.eventia.repository;

import com.svalero.eventia.domain.Artista;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ArtistaRepository extends CrudRepository<Artista, Long> {

    List<Artista> findAll();


    @Query("select a from Artista a where " +
            "(:nombreArtistico is null or lower(a.nombreArtistico) like lower(concat('%', :nombreArtistico, '%'))) and " +
            "(:generoMusical is null or lower(a.generoMusical) like lower(concat('%', :generoMusical, '%'))) and " +
            "(:activo is null or a.activo = :activo)")

    List<Artista> findByFilters(@Param("nombreArtistico") String nombreArtistico,
                                @Param("generoMusical") String generoMusical,
                                @Param("activo") Boolean activo);


    @Query("select a from Artista a where a.activo = true")
    List<Artista> findActiveArtistas();
}