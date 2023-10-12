package com.example.spacer.spacerbackend.services;

import lombok.Getter;

import java.lang.reflect.Field;
import java.util.HashMap;

@Getter
public class FilterImg {
  private final HashMap<String, Object> filteredObject;

  public FilterImg(Object object) throws IllegalAccessException {
    this.filteredObject = new HashMap<>();

    Field[] fields = object.getClass().getDeclaredFields();
    for (Field field : fields) {
      field.setAccessible(true);
      if (!field.getName().equals("img")) {
        String fieldName = field.getName();
        Object value = field.get(object);
        filteredObject.put(fieldName, value);
      }
    }
  }

}
