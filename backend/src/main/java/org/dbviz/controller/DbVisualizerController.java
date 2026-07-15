package org.dbviz.controller;

import org.dbviz.model.GraphDiagram;
import org.dbviz.service.DbVisualizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DbVisualizerController {

    @Autowired
    private DbVisualizeService dbVisualizeService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadSqlFile(@RequestParam("file") MultipartFile file){
        if(file.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        if(file.getOriginalFilename() == null || !file.getOriginalFilename().toLowerCase().endsWith(".sql")){ // file check
            return ResponseEntity.badRequest().body(Map.of("error", "Only SQL files are supported"));
        }

        String sqlContent = "";
        try{
            sqlContent = new String(file.getBytes(), StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(sqlContent.isBlank()){
            return ResponseEntity.badRequest().body(Map.of("error", "SQL file is empty"));
        }

        GraphDiagram graphDiagram = dbVisualizeService.getGraphStructure(sqlContent);
        return ResponseEntity.ok(graphDiagram);
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "db-visualizer"));
    }


}
