package com.guflimc.brick.holograms.common.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

@Converter
public class ComponentConverter implements AttributeConverter<Component, String> {

    @Override
    public String convertToDatabaseColumn(Component attribute) {
        if ( attribute == null ) return null;
        return GsonComponentSerializer.gson().serialize(attribute);
    }

    @Override
    public Component convertToEntityAttribute(String dbData) {
        if ( dbData == null ) return null;
        return GsonComponentSerializer.gson().deserialize(dbData);
    }
}