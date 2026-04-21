package com.svalero.eventia.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recintos")
public class Recinto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull(message = "El nombre no puede estar vacío")
    @Column
    private String nombre;
    @NotNull(message = "La dirección no puede estar vacía")
    @Column
    private String direccion;
    @NotNull(message = "La ubicación no puede estar vacía")
    @Column
    private String ciudad;
    @Min(value = 0, message = "La capacidad no puede ser negativa")
    @Column
    private int capacidad;
    @Column
    private boolean cubierto;
    @Min(value = 0, message = "El precio no puede ser negativo" )
    @Column(name = "precio_alquiler")
    private float precioAlquiler;
    @Min(value = 0, message = "El nuemro de eventos no puede ser negativo" )
    @Column(name = "eventos_celebrados")
    private int eventosCelebrados;
    @Column(name = "fecha_inauguracion")
    private LocalDate fechaInauguracion;
}