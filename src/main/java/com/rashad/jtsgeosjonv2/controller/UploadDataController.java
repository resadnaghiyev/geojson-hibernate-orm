package com.rashad.jtsgeosjonv2.controller;

import com.rashad.jtsgeosjonv2.service.UploadDataService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class UploadDataController {

    private final UploadDataService uploadDataService;

    @PostMapping("/points")
    public String loadPointsToDB(@RequestParam("file") MultipartFile file) throws ParseException, IOException {
        return uploadDataService.loadPointsToDB(file);
    }

    @PostMapping("/buildings")
    public String loadBuildingsToDB(@RequestParam("file1") MultipartFile file1,
                                    @RequestParam("file2") MultipartFile file2) throws IOException, ParseException {
        return uploadDataService.loadBuildingsToDB(file1, file2);
    }

    @PostMapping("/roads")
    public String loadRoadsToDB(@RequestParam("file") MultipartFile file) throws ParseException, IOException {
        return uploadDataService.loadRoadsToDB(file);
    }
}
