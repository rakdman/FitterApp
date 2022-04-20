package com.fittr.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.context.annotation.Configuration;

import javax.persistence.*;

@Entity
@Table(name = "USERDATA_AGGREGATOR")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(value = { "CREATED_AT", "UPDATED_AT" }, allowGetters = true)
public class UserDataAggregator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID")
    @Getter
    @Setter
    private Integer userId;


    @Column(name = "NAME")
    @Getter
    @Setter
    private String name;

    @Column(name="MODE_TYPE")
    @Getter
    @Setter
    private String mode;

    @Column(name = "DISTANCE_TRAVELLED")
    @Getter
    @Setter
    private Double distanceTravelled;

    @Column(name = "LEVEL")
    @Getter
    @Setter
    private Integer level;
}
