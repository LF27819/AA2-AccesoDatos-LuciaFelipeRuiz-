package com.svalero.eventia.repository;

import com.svalero.eventia.domain.Reserva;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ReservaRepository extends CrudRepository<Reserva, Long> {

    List<Reserva> findAll();

    @Query("select r from Reserva r where " +
            "(:metodoPago is null or lower(r.metodoPago) like lower(concat('%', :metodoPago, '%'))) and " +
            "(:codigoReserva is null or lower(r.codigoReserva) like lower(concat('%', :codigoReserva, '%'))) and " +
            "(:confirmada is null or r.confirmada = :confirmada)")

    List<Reserva> findByFilters(@Param("metodoPago") String metodoPago,
                                     @Param("codigoReserva") String codigoReserva,
                                     @Param("confirmada") Boolean confirmada);


    @Query("select r from Reserva r where r.confirmada = true")
    List<Reserva> findConfirmedReservas();

}
