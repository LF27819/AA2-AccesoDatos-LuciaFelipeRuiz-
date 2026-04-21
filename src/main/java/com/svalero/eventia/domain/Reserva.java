package com.svalero.eventia.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull(message = "La fecha no puede estar vacía")
    @Column(name = "fecha_reserva")
    private LocalDateTime fechaReserva;
    @Min(value = 0, message = "La cantidad de entradas no puede ser negativa")
    @Column(name = "cantidad_entradas")
    private int cantidadEntradas;
    @NotNull(message = "El precio no puede estar vacío")
    @Min(value = 0, message = "El valor no puede ser negativo" )
    @Column(name = "precio_total")
    private float precioTotal;
    @Column(name = "metodo_pago")
    private String metodoPago;
    @NotNull(message = "El codigo no puede estar vacío")
    @Column(name= "codigo_reserva", unique = true)
    private String codigoReserva;
    @Column
    private boolean confirmada;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;
}