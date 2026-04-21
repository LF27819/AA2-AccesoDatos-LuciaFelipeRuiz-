package com.svalero.eventia.repository;

import com.svalero.eventia.domain.UsuarioV2;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositoryV2 extends CrudRepository<UsuarioV2, Long> {
}