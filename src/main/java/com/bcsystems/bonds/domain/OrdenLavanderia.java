package com.bcsystems.bonds.domain;

import com.bcsystems.bonds.domain.en.EstadoOrdenLavanderia;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Audited
@Entity
@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
@Table(name = "orden_lavanderia")
public class OrdenLavanderia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOrden;

    @Column(nullable = false, unique = true, length = 30)
    private String folio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private Integer tipoPrecio;

    @Column(nullable = false)
    private LocalDate fechaRecepcion;

    @Column(nullable = false)
    private LocalDate fechaEntrega;

    @Column(nullable = false)
    private LocalTime horaEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private EstadoOrdenLavanderia estado;

    @Column(nullable = false)
    private Double total;

    @Column(length = 500)
    private String observaciones;

    private Integer idVenta;
    private LocalDateTime fechaEnvioCobro;
    private LocalDateTime fechaCobro;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleOrdenLavanderia> prendas = new ArrayList<>();
}
