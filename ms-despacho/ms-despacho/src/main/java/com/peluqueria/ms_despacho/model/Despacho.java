package com.peluqueria.ms_despacho.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "despachos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long pedidoId;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String ciudad;

    private String region;

    @Column(nullable = false)
    private String codigoPostal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDespacho estado;

    private LocalDate fechaEstimadaEntrega;

    private LocalDateTime fechaEntregaReal;

    private String trackingCode;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
}
