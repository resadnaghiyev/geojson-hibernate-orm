package com.rashad.jtsgeosjonv2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rashad.jtsgeosjonv2.entity.Bina;
import com.rashad.jtsgeosjonv2.entity.Poi;
import com.rashad.jtsgeosjonv2.entity.Yol;
import com.rashad.jtsgeosjonv2.model.bina.FeatureBina;
import com.rashad.jtsgeosjonv2.model.bina.GeoJsonBina;
import com.rashad.jtsgeosjonv2.model.poi.FeaturePoint;
import com.rashad.jtsgeosjonv2.model.poi.GeoJsonPoint;
import com.rashad.jtsgeosjonv2.model.yol.FeatureYollar;
import com.rashad.jtsgeosjonv2.model.yol.GeoJsonYollar;
import com.rashad.jtsgeosjonv2.repository.BinaRepository;
import com.rashad.jtsgeosjonv2.repository.PoiRepository;
import com.rashad.jtsgeosjonv2.repository.YolRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UploadDataService {

    private final BinaRepository binaRepository;
    private final YolRepository yolRepository;
    private final PoiRepository poiRepository;

    public String loadPointsToDB(MultipartFile file) throws IOException, ParseException {
        ObjectMapper mapper = new ObjectMapper();
        GeoJsonPoint geoJson = mapper.readValue(file.getBytes(), GeoJsonPoint.class);
        FeaturePoint[] featurePoints = geoJson.getFeatures();

        GeometryFactory geometryFactory = new GeometryFactory();
        GeoJsonReader reader = new GeoJsonReader(geometryFactory);
        List<Poi> poiList = new ArrayList<>();

        for (FeaturePoint featurePoint : featurePoints) {
            Geometry point = reader.read(featurePoint.getGeometry().toString());
            Poi poi = new Poi();
            poi.setPoint(point);
            poiList.add(poi);
        }
        poiRepository.saveAll(poiList);
        return "Loading points in DB completed.";
    }

    public String loadBuildingsToDB(MultipartFile file1, MultipartFile file2) throws IOException, ParseException {

        ObjectMapper mapper = new ObjectMapper();
        GeoJsonBina geoJson1 = mapper.readValue(file1.getBytes(), GeoJsonBina.class);
        GeoJsonBina geoJson2 = mapper.readValue(file2.getBytes(), GeoJsonBina.class);

        FeatureBina[] buildings1 = geoJson1.getFeatures();
        FeatureBina[] buildings2 = geoJson2.getFeatures();

        GeometryFactory geometryFactory = new GeometryFactory();
        GeoJsonReader reader = new GeoJsonReader(geometryFactory);
        List<Bina> binaList = new ArrayList<>();

        for (FeatureBina featureBina : buildings1) {
            Geometry geometry = reader.read(featureBina.getGeometry().toString());
            Bina bina = new Bina();
            bina.setPolygon(geometry);
            binaList.add(bina);
        }
        for (FeatureBina featureBina : buildings2) {
            Geometry geometry = reader.read(featureBina.getGeometry().toString());
            Bina bina = new Bina();
            bina.setPolygon(geometry);
            if (!binaList.contains(bina)) {
                binaList.add(bina);
            }
        }
        binaRepository.saveAll(binaList);
        return "Loading buildings in DB completed.";
    }

    public String loadRoadsToDB(MultipartFile file) throws ParseException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        GeoJsonYollar geoJson = mapper.readValue(file.getBytes(), GeoJsonYollar.class);
        FeatureYollar[] featureYollars = geoJson.getFeatures();

        GeometryFactory geometryFactory = new GeometryFactory();
        GeoJsonReader reader = new GeoJsonReader(geometryFactory);
        Set<Geometry> checkedRoads = new HashSet<>();
        List<Yol> yolList = new ArrayList<>();

        for (FeatureYollar featureYollar : featureYollars) {
            boolean cross = false;
            Geometry mainRoad = reader.read(featureYollar.getGeometry().toString());
            for (Geometry road : checkedRoads) {
                if (mainRoad.crosses(road) && !mainRoad.touches(road)) {
                        cross = true;
                        break;
                }
            }
            if (!cross) {
                Yol yol = new Yol();
                yol.setLineString(mainRoad);
                yolList.add(yol);
                checkedRoads.add(mainRoad);
            }
        }
        yolRepository.saveAll(yolList);
        return "Loading roads in DB completed.";
    }
}
