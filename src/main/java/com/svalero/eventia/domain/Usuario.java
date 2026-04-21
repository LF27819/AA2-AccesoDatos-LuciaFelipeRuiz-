package com.svalero.eventia.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull(message = "El nombre es obligatorio")
    @Column
    private String nombre;
    @NotNull(message = "El apellido es obligatorio")
    @Column
    private String apellidos;
    @NotNull(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    @Column(unique = true)
    private String email;
    @NotNull(message = "La contraseña es obligatoria")
    @Column
    @Size(min = 6, max = 20, message = "El nombre debe tener entre 6 y 20 caracteres")
    private String password;
    @Column
    private String telefono;
    @Column
    private boolean activo;
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    @Column(name = "eventos_asistidos")
    private int eventosAsistidos;
    @Column
    private String rol;
    @Min(value = 0, message = "El saldo no puede ser negativo" )
    @Column(name = "saldo_cuenta")
    private float saldoCuenta;
}