package com.example.spacer.spacerbackend.utils;

import com.example.spacer.spacerbackend.models.CategoryModel;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class CategoryModelDeserializer extends JsonDeserializer<CategoryModel> {
  @Override
  public CategoryModel deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
    String value = jsonParser.getValueAsString();
    try {
      if (value != null && !value.isEmpty()) {
        var category = new CategoryModel();
        category.setId((long) Integer.parseInt(value));
        return category;
      } else {
        throw new JsonParseException(jsonParser, "El valor no puede ser nulo o vacío");
      }
    } catch (NumberFormatException e) {
      throw new JsonParseException(jsonParser, "No se pudo parsear el valor: " + value + " a Integer", e);
    }
  }
}
