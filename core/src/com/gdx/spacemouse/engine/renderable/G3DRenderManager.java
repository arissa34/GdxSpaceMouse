package com.gdx.spacemouse.engine.renderable;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gdx.spacemouse.engine.camera.CameraHelper;

public class G3DRenderManager implements Disposable {

    private static G3DRenderManager instance;

    public static G3DRenderManager get() {
        if (instance == null) instance = new G3DRenderManager();
        return instance;
    }

    /*******************************/

    private Array<ModelInstance> listModelsDynamic;
    private Array<ModelInstance> listModelsDynamicNoEnv;
    private Array<ModelInstance> listModelsCache;
    private Array<ModelInstance> listModelsCacheNoEnv;
    private ModelInstance skybox;
    private ModelCache modelCache;
    private ModelCache modelCacheNoEnv;
    private ModelBatch modelBatch;
    private SpriteBatch spriteBatch;
    private DefaultShaderProvider shaderProvider;

    private PolygonSpriteBatch polygonBatch;

    private boolean modelCacheDirty = true;
    private boolean modelCacheNoEnvDirty = true;

    private Environment environment;
    public Array<BaseLight> lights;
    private ColorAttribute ambientColor;

    public G3DRenderManager() {
        modelBatch = new ModelBatch();

        //modelBatch = new ModelBatch(shaderProvider = new DefaultShaderProvider() {
        //    @Override
        //    protected Shader createShader(final Renderable renderable) {
        //        ShaderLoader loader = ShaderHelper.get().getShaderLoader();
        //        MyDefaultShader.Config config = new MyDefaultShader.Config();
        //        config.vertexShader = loader.load("main_shader.glsl:VS");
        //        config.fragmentShader = loader.load("main_shader.glsl:FS");
        //        config.numBones = 3;
        //        config.defaultCullFace = -1;
        //        return new MyDefaultShader(renderable, config);
        //    }
        //});
        modelCache = new ModelCache();
        modelCacheNoEnv = new ModelCache();
        spriteBatch = new SpriteBatch();
        polygonBatch = new PolygonSpriteBatch();
        polygonBatch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_ONE);
        environment = new Environment();
        environment.set(ambientColor = new ColorAttribute(ColorAttribute.AmbientLight, .35f, .35f, .35f, 1f));
        lights = new Array<>();
        listModelsDynamic = new Array<>();
        listModelsDynamicNoEnv = new Array<>();
        listModelsCache = new Array<>();
        listModelsCacheNoEnv = new Array<>();
    }

    private int i;

    public void render() {
        render(true);
    }

    public void render(boolean renderSkyBox) {

        if (skybox != null && renderSkyBox) {
            modelBatch.begin(CameraHelper.getCamera());
            modelBatch.render(skybox);
            modelBatch.end();
        }

        modelBatch.begin(CameraHelper.getCamera());
        {
            modelBatch.render(getModelCacheNoEnv());
            modelBatch.render(getModelCache(), environment);

            for (i = 0; i < listModelsDynamicNoEnv.size; i++) {
                modelBatch.render(listModelsDynamicNoEnv.get(i));
            }
            for (i = 0; i < listModelsDynamic.size; i++) {
                modelBatch.render(listModelsDynamic.get(i), environment);
            }
        }
        modelBatch.end();
    }

    public void renderCache() {

        modelBatch.begin(CameraHelper.getCamera());
        {
            modelBatch.render(getModelCacheNoEnv());
            modelBatch.render(getModelCache(), environment);
        }
        modelBatch.end();
    }

    public void renderDynamic() {
        modelBatch.begin(CameraHelper.getCamera());
        {
            for (i = 0; i < listModelsDynamicNoEnv.size; i++) {
                modelBatch.render(listModelsDynamicNoEnv.get(i));
            }
            for (i = 0; i < listModelsDynamic.size; i++) {
                modelBatch.render(listModelsDynamic.get(i), environment);
            }
        }
        modelBatch.end();
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public ModelCache getModelCache() {
        if (modelCacheDirty) {
            updateModelCache();
        }
        return modelCache;
    }

    public ModelCache getModelCacheNoEnv() {
        if (modelCacheNoEnvDirty) {
            updateModelCacheNoEnv();
        }
        return modelCacheNoEnv;
    }

    public synchronized void addModelDynamic(ModelInstance instance) {
        if (!listModelsDynamic.contains(instance, true))
            listModelsDynamic.add(instance);
    }

    public synchronized void removeModelDynamic(ModelInstance instance) {
        listModelsDynamic.removeValue(instance, true);
    }

    public synchronized void clearModelDynamic() {
        listModelsDynamic.clear();
    }

    public synchronized void addModelNoEnvDynamic(ModelInstance instance) {
        if (!listModelsDynamicNoEnv.contains(instance, true))
            listModelsDynamicNoEnv.add(instance);
    }

    public synchronized void removeModelNoEnvDynamic(ModelInstance instance) {
        listModelsDynamicNoEnv.removeValue(instance, true);
    }

    public synchronized void clearModelNoenvDynamic() {
        listModelsDynamicNoEnv.clear();
    }

    public synchronized void addModelToCache(ModelInstance instance) {
        if (!listModelsCache.contains(instance, true)) {
            listModelsCache.add(instance);
            modelCacheDirty = true;
        }
    }

    public synchronized void removeModelToCache(ModelInstance instance) {
        listModelsCache.removeValue(instance, true);
        modelCacheDirty = true;
    }

    public synchronized void clearModelToCache() {
        listModelsCache.clear();
        modelCacheDirty = true;
    }

    private void updateModelCache() {
        modelCache.begin();
        {
            modelCache.add(listModelsCache);
        }
        modelCache.end();
        modelCacheDirty = false;
    }

    public void addModelToCacheNoEnv(ModelInstance instance) {
        if (!listModelsCacheNoEnv.contains(instance, true)) {
            listModelsCacheNoEnv.add(instance);
            modelCacheNoEnvDirty = true;
        }
    }

    public void removeModelToCacheNoEnv(ModelInstance instance) {
        listModelsCacheNoEnv.removeValue(instance, true);
        modelCacheNoEnvDirty = true;
    }

    public void clearModelToCacheNoEnv() {
        listModelsCacheNoEnv.clear();
        modelCacheNoEnvDirty = true;
    }

    private void updateModelCacheNoEnv() {
        modelCacheNoEnv.begin();
        {
            modelCacheNoEnv.add(listModelsCacheNoEnv);
        }
        modelCacheNoEnv.end();
        modelCacheNoEnvDirty = false;
    }

    public void setSkybox(ModelInstance skybox) {
        this.skybox = skybox;
    }

    public void clearSkybox() {
        this.skybox = null;
    }

    public ModelInstance getSkybox() {
        return skybox;
    }

    public ModelBatch getModelBatch() {
        return modelBatch;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public ColorAttribute getAmbientColor() {
        return ambientColor;
    }

    public void addLight(BaseLight light) {
        if (!lights.contains(light, true)) {
            lights.add(light);
            environment.add(light);
        }
    }

    public void removeLight(BaseLight light) {
        lights.removeValue(light, true);
        environment.remove(light);
    }

    public void clearAllLights() {
        environment.remove(lights);
        lights.clear();
    }

    public void clearAll() {
        clearAllLights();
        clearModelToCacheNoEnv();
        clearModelToCache();
        clearModelNoenvDynamic();
        clearModelDynamic();
        clearSkybox();
    }

    @Override
    public void dispose() {
        modelCache.dispose();
        modelCacheNoEnv.dispose();
        modelBatch.dispose();
        spriteBatch.dispose();
        //shaderProvider.dispose();
        polygonBatch.dispose();
    }
}
