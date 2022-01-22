package com.gdx.spacemouse.engine.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.rm.spacemousewrapper.SpaceMouse;
import com.rm.spacemousewrapper.SpaceMouseButtonState;
import com.rm.spacemousewrapper.SpaceMouseDeviceListener;

public class SpaceMouseController extends AbsController implements SpaceMouseDeviceListener {

    private final SpaceMouse spaceMouse;

    public Vector3 rightCam = new Vector3();

    public SpaceMouseController(GhostCamera camera) {
        super(camera);

        spaceMouse = new SpaceMouse();
        spaceMouse.addDeviceListeners(this);
        try {
            spaceMouse.start("LibgdxTest", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(float delta) {
        rightCam.set(tmp.set(camera.targetUp).crs(camera.targetDirection).nor());
    }

    @Override
    public void deviceAdded(int deviceId) {
        Gdx.app.log(this.getClass().getSimpleName(), "===> deviceAdded : "+deviceId);
    }

    @Override
    public void deviceRemoved(int deviceId) {
        Gdx.app.log(this.getClass().getSimpleName(), "===> deviceRemoved : "+deviceId);
    }

    float factorT = 600;
    float factorR = 100;
    @Override
    public void axisChanged(int[] values) {

        camera.targetPosition.mulAdd(camera.targetUp, (-values[2]/factorT));
        camera.targetPosition.mulAdd(camera.targetDirection, (values[1]/factorT));
        camera.targetPosition.mulAdd(rightCam, (values[0]/factorT));

        rotateAround(camera.targetPosition, camera.targetUp, -(values[5]/factorR));
        rotateAround(camera.targetPosition, rightCam, (values[3]/factorR));
        rotateAround(camera.targetPosition, camera.targetDirection, (values[4]/factorR));

        camera.normalizeTargetUp();
    }

    public void rotateAround (Vector3 point, Vector3 axis, float angle) {
        tmp2.set(point);
        tmp2.sub(camera.targetPosition);
        translateTarget(tmp2);
        rotateTarget(axis, angle);
        tmp2.rotate(axis, angle);
        camera.translate(-tmp2.x, -tmp2.y, -tmp2.z);
    }
    public void translateTarget (Vector3 vec) {
        camera.targetPosition.add(vec);
    }
    public void rotateTarget (Vector3 axis, float angle) {
        camera.targetDirection.rotate(axis, angle);
        camera.targetUp.rotate(axis, angle);
    }

    @Override
    public void buttonChanged(SpaceMouseButtonState event) {

    }

    @Override
    public void dispose() {
        spaceMouse.close();
    }
}
