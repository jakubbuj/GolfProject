package com.game.terrain;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

public class MaterialFactory {
    private final Material grassMaterial = new Material(ColorAttribute.createDiffuse(Color.valueOf("228B22")));
    private final Material sandMaterial = new Material(ColorAttribute.createDiffuse(Color.valueOf("E1C16E")));

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
