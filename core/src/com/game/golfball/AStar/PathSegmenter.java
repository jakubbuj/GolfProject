package com.game.golfball.AStar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathSegmenter {
    private List<Node> path;
    private int segments;

    /**
     * Constructs a PathSegmenter with the specified path and number of segments
     * @param path the list of nodes representing the path
     * @param segments the number of segments to divide the path into
     */
    public PathSegmenter(List<Node> path, int segments) {
        this.path = path;
        this.segments = segments;
    }

    /**
     * Segments the path into a specified number of segments
     * @return a list of nodes representing the segmented path
     */
    public List<Node> segmentPath() {
        if (path == null || path.isEmpty()) {
            return Collections.emptyList();
        }

        List<Node> segmentedPath = new ArrayList<>();
        int step = Math.max(1, path.size() / segments);

        for (int i = 0; i < path.size(); i += step) {
            segmentedPath.add(path.get(i));
        }

        if (!segmentedPath.contains(path.get(path.size() - 1))) {
            segmentedPath.add(path.get(path.size() - 1));
        }

        return segmentedPath;
    }
}
