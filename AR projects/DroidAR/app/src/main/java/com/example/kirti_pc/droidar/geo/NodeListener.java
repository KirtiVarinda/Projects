package com.example.kirti_pc.droidar.geo;

public interface NodeListener {

	boolean addFirstNodeToGraph(GeoGraph graph, GeoObj objectToAdd);

	boolean addNodeToGraph(GeoGraph graph, GeoObj objectToAdd);

	boolean addLastNodeToGraph(GeoGraph graph, GeoObj objectToAdd);
}