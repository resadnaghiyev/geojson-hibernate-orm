package com.rashad.jtsgeosjonv2.controller;

import com.rashad.jtsgeosjonv2.service.GeojsonService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/buildings")
@RequiredArgsConstructor
public class GeoJsonFeatureCollectionController {

    private final GeojsonService geojsonService;

    @GetMapping
    public String getAllBuildings() {
        return geojsonService.getAllBuildings();
    }

    @GetMapping("/{id}")
    public String getBuildingById(@PathVariable Long id) {
        return geojsonService.getBuildingById(id);
    }

    @PutMapping("/{id}")
    public String updateBuildingById(@PathVariable Long id,
                                     @RequestParam("file") MultipartFile multipartFile) throws IOException, ParseException {
        return geojsonService.updateBuildingById(id, multipartFile);
    }

    @DeleteMapping("/{id}")
    public String deleteBuildingById(@PathVariable Long id) {
        return geojsonService.deleteBuildingById(id);
    }

    @PostMapping
    public String addBuilding(@RequestParam("file")MultipartFile multipartFile) throws IOException, ParseException {
        return geojsonService.addBuilding(multipartFile);
    }

    @GetMapping("/getPoi/{id}")
    public String getPoiByBuildingId(@PathVariable Long id) {
        return geojsonService.getPoiByBuildingId(id);
    }
}
