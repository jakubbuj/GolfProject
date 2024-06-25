package com.game.terrain;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

public class MaterialFactory {
    private final Material grassMaterial = new Material(ColorAttribute.createDiffuse(Color.valueOf("228B22")));
    private final Material sandMaterial = new Material(ColorAttribute.createDiffuse(Color.valueOf("E1C16E")));

    /**
     * Determines the material to be used based on the height and other parameters
     * @param height the height of the terrain at the given coordinates
     * @param waterLevel the water level of the terrain
     * @param x the x-coordinate in the grid
     * @param y the y-coordinate in the grid
     * @param grassTiles a boolean array to keep track of grass tiles
     * @return the determined material for the terrain
     */
    public Material determineMaterial(float height, float waterLevel, int x, int y, boolean[][] grassTiles) {
        Material material;
        float sandHeight = SandHeightCalculator.getSandHeight(x, y);

        if (sandHeight > 0.5) {
            material = sandMaterial;
        } else {
            material = grassMaterial;
            grassTiles[x][y] = true;
        }
        return material;
    }
}
