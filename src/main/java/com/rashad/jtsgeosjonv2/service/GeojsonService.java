package com.rashad.jtsgeosjonv2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rashad.jtsgeosjonv2.entity.Bina;
import com.rashad.jtsgeosjonv2.entity.Poi;
import com.rashad.jtsgeosjonv2.model.bina.FeatureBina;
import com.rashad.jtsgeosjonv2.repository.BinaRepository;
import com.rashad.jtsgeosjonv2.repository.PoiRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeojsonService {

    private final BinaRepository binaRepository;
    private final PoiRepository poiRepository;

    public String getAllBuildings() {
        List<Bina> binaList = binaRepository.findAll();
        GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"type\": \"FeatureCollection\", \"features\": [");
        for (Bina bina : binaList) {
            String s = geoJsonWriter.write(bina.getPolygon());
            String geo = s.substring(0, s.indexOf("crs")-2) + "}";
            stringBuilder.append("{\"type\": \"Feature\", \"geometry\": ")
                    .append(geo).append(", \"properties\": {\"id\": ")
                    .append(bina.getId()).append("}},");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1).append("]}");
        return stringBuilder.toString();
    }

    public String getBuildingById(Long id) {
        Optional<Bina> optionalBina = binaRepository.findById(id);
        if (optionalBina.isEmpty()) {
            return "Bina with id: " + id + " not found!";
        }
        Bina bina = optionalBina.get();
        GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
        String s = geoJsonWriter.write(bina.getPolygon());
        String geo = s.substring(0, s.indexOf("crs")-2) + "}";
        return "{\"type\": \"Feature\", \"geometry\": " +
                geo + ", \"properties\": {\"id\": " +
                bina.getId() + "}}";
    }

    public String updateBuildingById(Long id, MultipartFile file) throws IOException, ParseException {
        Optional<Bina> optionalBina = binaRepository.findById(id);
        if (optionalBina.isEmpty()) {
            return "Bina with id: " + id + " not found!";
        }
        Bina bina = optionalBina.get();
        bina.setPolygon(getGeometryFromFile(file));
        binaRepository.save(bina);
        return "Bina id: " + id + " updated";
    }

    public String deleteBuildingById(Long id) {
        Optional<Bina> optionalBina = binaRepository.findById(id);
        if (optionalBina.isEmpty()) {
            return "Bina with id: " + id + " not found!";
        }
        binaRepository.deleteById(id);
        return "Bina id: " + id + " deleted";
    }

    public String addBuilding(MultipartFile file) throws IOException, ParseException {
        Bina bina = new Bina();
        bina.setPolygon(getGeometryFromFile(file));
        binaRepository.save(bina);
        return "New bina added to DB";
    }

    public String getPoiByBuildingId(Long id) {
        Optional<Bina> optionalBina = binaRepository.findById(id);
        if (optionalBina.isEmpty()) {
            return "Bina with id: " + id + " not found!";
        }
        Bina bina = optionalBina.get();
        List<Poi> poiList = poiRepository.findAll();
        poiList.removeIf(poi -> !bina.getPolygon().contains(poi.getPoint()));
        GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"type\": \"FeatureCollection\", \"features\": [");
        for (Poi poi : poiList) {
            String s = geoJsonWriter.write(poi.getPoint());
            String geo = s.substring(0, s.indexOf("crs")-2) + "}";
            stringBuilder.append("{\"type\": \"Feature\", \"geometry\": ")
                    .append(geo).append(", \"properties\": {\"id\": ")
                    .append(poi.getId()).append("}},");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1).append("]}");
        return stringBuilder.toString();
    }

    private Geometry getGeometryFromFile(MultipartFile file) throws IOException, ParseException {
        ObjectMapper mapper = new ObjectMapper();
        FeatureBina featureBina = mapper.readValue(file.getBytes(), FeatureBina.class);
        GeometryFactory geometryFactory = new GeometryFactory();
        GeoJsonReader reader = new GeoJsonReader(geometryFactory);
        return reader.read(featureBina.getGeometry().toString());
    }
}