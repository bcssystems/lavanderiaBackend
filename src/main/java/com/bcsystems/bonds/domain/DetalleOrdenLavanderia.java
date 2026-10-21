package com.bcsystems.bonds.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
@Table(name = "detalle_orden_lavanderia")
public class DetalleOrdenLavanderia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPrenda;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orden", nullable = false)
    private OrdenLavanderia orden;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_servicio", nullable = false)
    private ServicioLavanderia servicio;

    @Column(nullable = false, length = 100)
    private String tipoPrenda;

    @Column(length = 300)
    private String descripcion;

    @Column(length = 80)
    private String color;

    @Column(nullable = false)
    private Double precio;

    @Column(length = 300)
    private String observaciones;
}
