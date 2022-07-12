package com.guflimc.brick.holograms.common.domain;

import com.guflimc.brick.orm.converters.ComponentConverter;
import jakarta.persistence.*;
import net.kyori.adventure.text.Component;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "hologram_lines")
public class DHologramLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = DHologram.class, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DHologram hologram;

    @Convert(converter = ComponentConverter.class)
    @Column(nullable = false)
    private Component text;

    //

    private DHologramLine() {
    }

    public DHologramLine(DHologram hologram, Component text) {
        this.hologram = hologram;
        this.text = text;
    }

    public int id() {
        return id;
    }

    public Component text() {
        return text;
    }

}
