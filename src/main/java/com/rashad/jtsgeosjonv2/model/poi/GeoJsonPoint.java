package com.rashad.jtsgeosjonv2.model.poi;

import lombok.Data;

@Data
public class GeoJsonPoint {

    private String type;
    private FeaturePoint[] features;
}
