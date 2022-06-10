package com.guflimc.brick.holograms.common.domain;

import com.guflimc.brick.holograms.api.domain.MultiLineTextHologram;
import jakarta.persistence.*;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Entity
@Table(name = "multi_line_text_holograms")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class DMultiLineTextHologram extends DHologram implements MultiLineTextHologram {

    @OneToMany(targetEntity = DMultiLineTextHologramLine.class, cascade = CascadeType.ALL)
    private List<DMultiLineTextHologramLine> lines = new ArrayList<>();

    //

    public DMultiLineTextHologram() {
        super();
    }

    public DMultiLineTextHologram(String name) {
        super(name);
    }

    @Override
    public List<Component> lines() {
        return lines.stream().map(DMultiLineTextHologramLine::text)
                .collect(Collectors.toList());
    }

    @Override
    public void setLines(Collection<Component> lines) {
        this.lines.clear();
        this.lines.addAll(lines.stream().map(line -> new DMultiLineTextHologramLine(this, line)).toList());
    }

    protected final void mutate(Consumer<List<DMultiLineTextHologramLine>> mutator) {
        List<DMultiLineTextHologramLine> newLines = new ArrayList<>(this.lines);
        mutator.accept(newLines);
        this.lines.clear();
        this.lines.addAll(newLines);
    }

    @Override
    public void addLine(Component component) {
        mutate(newLines -> newLines.add(new DMultiLineTextHologramLine(this, component)));
    }

    @Override
    public void setLine(int index, Component component) {
        mutate(newLines -> newLines.set(index, new DMultiLineTextHologramLine(this, component)));
    }

    @Override
    public void removeLine(int index) {
        mutate(newLines -> newLines.remove(index));
    }

}
