package com.gdx.spacemouse;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gdx.spacemouse.engine.camera.CameraHelper;
import com.gdx.spacemouse.engine.renderable.AxeModel;
import com.gdx.spacemouse.engine.renderable.G3DRenderManager;
import com.rm.spacemousewrapper.SpaceMouse;
import com.rm.spacemousewrapper.SpaceMouseButtonState;
import com.rm.spacemousewrapper.SpaceMouseDeviceListener;

public class MyGdxGame extends ApplicationAdapter {

	AxeModel axeModel;
	ModelInstance objInstance;

	@Override
	public void create () {

		axeModel = new AxeModel().registerForRender();
		G3DRenderManager.get().addLight(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, -0.8f, -1f));

		CameraHelper.get().setSpaceMouseCamera();

		ObjLoader loader = new ObjLoader();
		Model model = loader.loadModel(Gdx.files.local("data/3DxHouse.obj"));
		objInstance = new ModelInstance(model);
		objInstance.transform.translate(0, 1.4f, 0);
		G3DRenderManager.get().addModelToCache(objInstance);
	}



	@Override
	public void render () {
		CameraHelper.get().update(Gdx.graphics.getDeltaTime());

		ScreenUtils.clear(0, 0, 0, 1, true);

		G3DRenderManager.get().renderCache();
	}
	
	@Override
	public void dispose () {
		axeModel.dispose();
		CameraHelper.get().dispose();
		G3DRenderManager.get().dispose();
	}
}
