package com.movieMania.backend.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    private String name;

    public roles(){

    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "movie", foreignKey = @ForeignKey(name = "movie-actor_fk2"))
    @JsonBackReference(value = "movie-roles")
    @ToString.Exclude
    private movie movie;
}
