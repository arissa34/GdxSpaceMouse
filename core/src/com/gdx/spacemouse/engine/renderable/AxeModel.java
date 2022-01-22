package com.gdx.spacemouse.engine.renderable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Disposable;

public class AxeModel implements Disposable {

    public static float GRID_MIN_X = -20f;
    public static float GRID_MIN_Y = -20f;
    public static float GRID_MIN_Z = -20f;
    public static float GRID_MAX_X = 20f;
    public static float GRID_MAX_Z = 20f;
    public static float GRID_MAX_Y = 0f;
    public static float GRID_STEP = 1f;
    public Model axesModel, gridModel;

    public AxeModel(){

        ModelBuilder modelBuilderGrid = new ModelBuilder();
        modelBuilderGrid.begin();
        MeshPartBuilder builder = modelBuilderGrid.part("grid", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.LIGHT_GRAY);
        for(float x = GRID_MIN_X; x <= GRID_MAX_X; x += GRID_STEP){
            builder.line(x, 0, GRID_MIN_Z, x, 0, GRID_MAX_Z);
        }
        for(float z = GRID_MIN_Z; z <= GRID_MAX_Z; z += GRID_STEP){
            builder.line(GRID_MIN_Z, 0, z, GRID_MAX_X, 0, z);
        }
        //for(float x = GRID_MIN_X; x <= GRID_MAX_X; x += GRID_STEP){
        //    builder.line(x, GRID_MIN_Y, 0, x, GRID_MAX_Y, 0);
        //}
        //for(float y = GRID_MIN_Z; y <= GRID_MAX_Z; y += GRID_STEP){
        //    builder.line(GRID_MIN_X, y, 0, GRID_MAX_X, y, 0);
        //}

        gridModel = modelBuilderGrid.end();

        ModelBuilder modelBuilder2 = new ModelBuilder();
        modelBuilder2.begin();
        MeshPartBuilder builder2 = modelBuilder2.part("axes", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder2.setColor(Color.RED);
        builder2.line(0, 0, 0, 10, 0, 0);
        builder2.setColor(Color.GREEN);
        builder2.line(0, 0, 0, 0, 10, 0);
        builder2.setColor(Color.BLUE);
        builder2.line(0, 0, 0, 0, 0, 10);
        axesModel = modelBuilder2.end();

    }

    public AxeModel registerForRender(){
        G3DRenderManager.get().addModelToCacheNoEnv(new ModelInstance(gridModel));
        G3DRenderManager.get().addModelToCacheNoEnv(new ModelInstance(axesModel));
        return this;
    }

    @Override
    public void dispose() {
        axesModel.dispose();
        gridModel.dispose();
    }
}
