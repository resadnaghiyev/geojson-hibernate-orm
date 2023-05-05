package com.rashad.jtsgeosjonv2.controller;

import com.rashad.jtsgeosjonv2.entity.Bina;
import com.rashad.jtsgeosjonv2.entity.Poi;
import com.rashad.jtsgeosjonv2.repository.BinaRepository;
import com.rashad.jtsgeosjonv2.repository.PoiRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/geometry-collection/building")
@RequiredArgsConstructor
public class GeoJsonGeometryCollectionController {

    private final BinaRepository binaRepository;
    private final PoiRepository poiRepository;

    @GetMapping
    public String getAllBuildings() {
        List<Bina> geometries = binaRepository.findAll();
        List<Geometry> geometryList = geometries.stream().map(Bina::getPolygon).toList();
        GeometryCollection geometryCollection = new GeometryCollection(geometryList
                .toArray(new Geometry[0]), new GeometryFactory());
        GeoJsonWriter writer = new GeoJsonWriter();
        return writer.write(geometryCollection);
    }

    @GetMapping("/{id}")
    public String getBuildingById(@PathVariable Long id) {
        Optional<Bina> optionalBina = binaRepository.findById(id);
        if (optionalBina.isEmpty()) {
            return "Bina with id: " + id + " not found!";
        }
        GeoJsonWriter writer = new GeoJsonWriter();
        return writer.write(optionalBina.get().getPolygon());
    }

    @GetMapping("/getPoi/{id}")
    public String getPoiByBuilding(@PathVariable Long id) {
        Optional<Bina> optionalBina = binaRepository.findById(id);
        if (optionalBina.isEmpty()) {
            return "Bina with id: " + id + " not found!";
        }
        Bina bina = optionalBina.get();
        List<Poi> poiList = poiRepository.findAll();
        poiList.removeIf(poi -> !bina.getPolygon().contains(poi.getPoint()));
        List<Geometry> geometryList = poiList.stream().map(Poi::getPoint).toList();
        GeometryCollection geometryCollection = new GeometryCollection(geometryList
                .toArray(new Geometry[0]), new GeometryFactory());
        GeoJsonWriter writer = new GeoJsonWriter();
        return writer.write(geometryCollection);
    }
}
