package com.rashad.jtsgeosjonv2.model.yol;

import lombok.Data;

import java.util.Map;

@Data
public class FeatureYollar {

    private String type;
    private GeometryYollar geometry;
    private Map<String, Object> properties;
    private String id;
}
