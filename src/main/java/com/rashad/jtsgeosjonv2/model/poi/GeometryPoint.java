package com.rashad.jtsgeosjonv2.model.poi;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
public class GeometryPoint {

    private String type;
    private double[] coordinates;

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
