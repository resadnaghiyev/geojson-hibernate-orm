package com.rashad.jtsgeosjonv2.model.poi;

import lombok.Data;

import java.util.Map;

@Data
public class FeaturePoint {

    private String type;
    private GeometryPoint geometry;
    private Map<String, Object> properties;
    private String id;
}
