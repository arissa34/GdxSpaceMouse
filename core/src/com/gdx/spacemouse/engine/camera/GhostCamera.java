package com.gdx.spacemouse.engine.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gdx.spacemouse.engine.utils.Vector3Utils;

public class GhostCamera extends PerspectiveCamera {
    /** the field of view of the height, in degrees **/
    public float fieldOfView;

    protected static Vector3 tmp = new Vector3();
    public Vector3 targetPosition;
    public Vector3 targetDirection;
    public Vector3 targetUp;
    public final Matrix4 targetView;
    public float targetFieldOfView;
    public float speed = 10f;
    private boolean isLerping;

    public GhostCamera(float fieldOfView, float viewportWidth, float viewportHeight) {
        this.fieldOfView = fieldOfView;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;

        targetPosition = new Vector3(0, 0, 0);
        targetDirection = new Vector3(0, 0, -1);
        targetUp = new Vector3(0, 1, 0);
        targetView = new Matrix4();

        targetFieldOfView = fieldOfView;
        isLerping = true;

        near = 0.1f;
        far = 10000f;

        update();
    }

    public void update(float delta) {
        if(isLerping) {
            Vector3Utils.lerp(direction, targetDirection, speed, delta);
            Vector3Utils.lerp(position, targetPosition, speed, delta);
            Vector3Utils.lerp(up, targetUp, speed, delta);
            Vector3Utils.lerp(fieldOfView, targetFieldOfView, speed, delta);
        }
        update();
    }

    @Override
    public void update() {
        update(true);
        updateTargetView();
    }

   //public void update(boolean updateFrustum){
//
   //    float aspect = viewportWidth / viewportHeight;
   //    projection.setToProjection(Math.abs(near), Math.abs(far), fieldOfView, aspect);
   //    view.setToLookAt(position, direction, up);
   //    combined.set(projection);
   //    Matrix4.mul(combined.val, view.val);
   //    if(updateFrustum) {
   //        invProjectionView.set(combined);
   //        Matrix4.inv(invProjectionView.val);
   //        frustum.update(invProjectionView);
   //    }
   //}

    public void updateTargetView(){
        targetView.setToLookAt(targetPosition, targetDirection, targetUp);
    }

    public void snapToTarget(){
        position.set(targetPosition);
        lookAt(targetDirection);
        up.set(targetUp);
    }

    public void snapToTarget(Vector3 targetPosition) {
        this.targetPosition = this.targetDirection.set(targetPosition);
        position.set(targetPosition);
    }

    public void snapToTarget(Vector3 targetPosition, Vector3 targetDirection) {
        this.targetPosition = this.targetDirection.set(targetPosition);
        this.targetDirection = this.targetDirection.set(targetDirection);
        position.set(targetPosition);
        lookAt(targetDirection);
    }

    public void snapToTarget(Vector3 targetPosition, Vector3 targetDirection, Vector3 targetUp) {
        this.targetPosition = this.targetDirection.set(targetPosition);
        this.targetDirection = this.targetDirection.set(targetDirection);
        this.targetUp = this.targetUp.set(targetUp);
        position.set(targetPosition);
        lookAt(targetDirection);
        up.set(targetUp);
    }

    public void moveTo(float x, float y, float z) {
        targetPosition.set(x, y, z);
    }

    public GhostCamera moveTo(Vector3 targetPosition) {
        this.targetPosition.set(targetPosition);
        return this;
    }

    public GhostCamera moveTo(Vector3 targetPosition, Vector3 targetDirection) {
        this.targetPosition.set(targetPosition);
        this.targetDirection.set(targetDirection);
        return this;
    }

    public GhostCamera moveTo(Vector3 targetPosition, Vector3 targetDirection, Vector3 targetUp) {
        this.targetPosition.set(targetPosition);
        this.targetDirection.set(targetDirection);
        this.targetUp.set(targetUp);
        return this;
    }

    public GhostCamera moveToLooktAt(Vector3 targetDirection, Vector3 targetUp){
        this.targetDirection.set(targetDirection);
        this.targetUp.set(targetUp);
        return this;
    }

    public GhostCamera moveToLooktAt(Vector3 targetDirection){
        return moveToLooktAt(targetDirection.x, targetDirection.y, targetDirection.z);
    }

    public GhostCamera moveUp(Vector3 targetUp){
        this.targetUp.set(targetUp);
        return this;
    }

    public GhostCamera setTargetFieldOfView(float targetFieldOfView) {
        this.targetFieldOfView = targetFieldOfView;
        return this;
    }

    public void forceToCurrentTarget(){
        position.set(targetPosition);
        direction.set(targetDirection);
        up.set(targetUp);
        targetFieldOfView = fieldOfView;
    }

    public GhostCamera moveToLooktAt (float x, float y, float z) {
        tmp.set(x, y, z).sub(targetPosition).nor();
        if (!tmp.isZero()) {
            float dot = tmp.dot(targetUp); // up and direction must ALWAYS be orthonormal vectors
            if (Math.abs(dot - 1) < 0.000000001f) {
                // Collinear
                targetUp.set(targetDirection).scl(-1);
            } else if (Math.abs(dot + 1) < 0.000000001f) {
                // Collinear opposite
                targetUp.set(targetDirection);
            }
            targetDirection.set(tmp);
            normalizeTargetUp();
        }
        return this;
    }

    public void normalizeTargetUp () {
        tmp.set(targetDirection).crs(targetUp);
        targetUp.set(tmp).crs(targetDirection).nor();
    }

    public void startLerp() {
        isLerping = true;
    }

    public void stopLerp() {
        isLerping = false;
    }

    protected static void addInputAndGestureListener(GestureDetector.GestureListener gestureListener, InputProcessor inputProcessor){
        InputMultiplexer inputMultiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
        inputMultiplexer.addProcessor(inputProcessor);
    }
}
