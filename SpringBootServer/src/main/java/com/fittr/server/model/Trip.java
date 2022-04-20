package com.fittr.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "TRIP")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(value = { "CREATED_AT", "UPDATED_AT" }, allowGetters = true)
public class Trip{

    @Getter
    @Column(name = "USER_ID")
    private Integer userId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Column(name = "ID")
    private Long id;

    @Column(name = "DISTANCE_TRAVELLED")
    @Getter
    @Setter
    private Double distanceTravelled;


    @Column(name = "COINS")
    @Getter
    @Setter
    private Integer coins;

    // @Enumerated(value=EnumType.STRING)
    @Column(name="MODE_TYPE")
    @Getter
    @Setter
    private String mode;

}
