package com.guflimc.brick.holograms.common.domain;

import com.guflimc.brick.orm.converters.ComponentConverter;
import jakarta.persistence.*;
import net.kyori.adventure.text.Component;

@Entity
@Table(name = "hologram_lines")
public class DHologramLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = DHologram.class)
    private DHologram hologram;

    @Convert(converter = ComponentConverter.class)
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
