package com.svalero.eventia.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "artistas")
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "nombre_artistico")
    private String nombreArtistico;
    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "nombre_real")
    private String nombreReal;
    @Column(name = "genero_musical")
    private String generoMusical;
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    @Column
    private boolean activo;
    @Min(value = 0, message = "El valor no puede ser negativo" )
    @Column
    private float cache;
    @Min(value = 0, message = "El valor no puede ser negativo" )
    @Column(name = "eventos_realizados")
    private int eventosRealizados;
}