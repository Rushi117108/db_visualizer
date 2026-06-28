package org.dbviz.service;

import org.dbviz.model.GraphDiagram;
import org.springframework.stereotype.Service;

@Service
public class DbVisualizeService {


    public GraphDiagram getGraphStructure(String sqlContent) {

        GraphDiagram graphDiagram = new GraphDiagram();
        return graphDiagram;
    }
}
