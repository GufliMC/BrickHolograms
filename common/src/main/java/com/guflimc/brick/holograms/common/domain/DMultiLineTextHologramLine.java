package com.guflimc.brick.holograms.common.domain;

import com.guflimc.brick.holograms.common.converters.ComponentConverter;
import jakarta.persistence.*;
import net.kyori.adventure.text.Component;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "multi_line_text_hologram_line")
public class DMultiLineTextHologramLine {

    @Id
    @Basic
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @ManyToOne(targetEntity = DMultiLineTextHologram.class)
    private DMultiLineTextHologram hologram;

    @Convert(converter = ComponentConverter.class)
    private Component text;

    //

    private DMultiLineTextHologramLine() {
    }

    public DMultiLineTextHologramLine(DMultiLineTextHologram hologram, Component text) {
        this.hologram = hologram;
        this.text = text;
    }

    public Component text() {
        return text;
    }

}
