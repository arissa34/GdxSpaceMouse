package com.gdx.spacemouse.engine.utils;

import com.badlogic.gdx.math.Vector3;

public class Vector3Utils {

    public static Vector3 lerp(Vector3 start, Vector3 end, Vector3 out, float pourcent){
        float x = start.x + pourcent * (end.x - start.x);
        float y = start.y + pourcent * (end.y - start.y);
        float z = start.z + pourcent * (end.z - start.z);
        out.set(x, y, z);
        return out;
    }

    public static float lerp(float start, float end, float pourcent){
        float v = start + pourcent * (end - start);
        return v;
    }

    public static Vector3 lerp(Vector3 vec, Vector3 target, float speed, float deltaTime) {
        deltaTime *= 0.01f;
        vec.x -= (vec.x - target.x)
                * Math.min(1.0f / 30f, Math.max(1.0f / 120f, deltaTime))
                * speed;
        vec.y -= (vec.y - target.y)
                * Math.min(1.0f / 30f, Math.max(1.0f / 120f, deltaTime))
                * speed;
        vec.z -= (vec.z - target.z)
                * Math.min(1.0f / 30f, Math.max(1.0f / 120f, deltaTime))
                * speed;
        return vec;
    }

    public static float lerp(float vec, float target, float speed, float deltaTime) {
        deltaTime *= 0.01f;
        return vec - (vec - target)
                * Math.min(1.0f / 30f, Math.max(1.0f / 120f, deltaTime))
                * speed;
    }
}
