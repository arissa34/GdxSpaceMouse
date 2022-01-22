package com.gdx.spacemouse.engine.camera;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class CameraHelper implements Disposable {

    private static CameraHelper instance;

    public static CameraHelper get() {
        if (instance == null) instance = new CameraHelper();
        return instance;
    }

    /*******************************/

    private GhostCamera camera;
    private AbsController defaultController;
    private SpaceMouseController spaceMouseController;

    public CameraHelper(){
        camera = new GhostCamera(47, 1400, 900);
        camera.targetPosition.set(7.5f, 5, 7.5f);
        camera.moveToLooktAt(Vector3.Zero);
        camera.snapToTarget();
    }

    public static GhostCamera getCamera(){
        return get().camera;
    }

    public void update(float delta){
        if(defaultController != null) {
            defaultController.update(delta);
        }
        getCamera().update(delta);
    }

    public GhostCamera setSpaceMouseCamera(){
        if(spaceMouseController == null){
            spaceMouseController = new SpaceMouseController(camera);
        }
        camera.far = 10000f;
        activateController(spaceMouseController);
        return camera;
    }

    private void activateController(AbsController newController){
        if(defaultController != null) {
            defaultController.disable();
        }
        defaultController = newController;
        defaultController.enable();
    }

    public void disableTouch(){
        if(defaultController != null) {
            defaultController.disable();
        }
    }

    public void enableTouch(){
        if(defaultController != null) {
            defaultController.enable();
        }
    }

    public boolean isTouchEnable(){
        if(defaultController != null) {
            return defaultController.isEnable();
        }
        return false;
    }

    public AbsController getDefaultController(){
        return defaultController;
    }

    @Override
    public void dispose() {
        spaceMouseController.dispose();
    }
}
