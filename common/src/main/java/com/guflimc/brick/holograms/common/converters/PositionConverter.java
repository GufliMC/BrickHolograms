package com.guflimc.brick.holograms.common.converters;

import com.google.gson.Gson;
import com.guflimc.brick.holograms.api.meta.Position;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PositionConverter implements AttributeConverter<Position, String> {

    private static final Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(Position attribute) {
        return gson.toJson(attribute);
    }

    @Override
    public Position convertToEntityAttribute(String dbData) {
        return gson.fromJson(dbData, Position.class);
    }
}