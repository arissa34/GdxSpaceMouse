package com.rm.spacemousewrapper;

import java.util.ArrayList;
import java.util.List;

public class SpaceMouse extends AbsLoader{

    private List<SpaceMouseDeviceListener> deviceListeners = new ArrayList<>();

    public SpaceMouse(){

    }

    public void addDeviceListeners(SpaceMouseDeviceListener listener) {
        deviceListeners.add(listener);
    }

    public void removeDeviceListener(SpaceMouseDeviceListener listener) {
        deviceListeners.remove(listener);
    }

    public void onDeviceAdded(int deviceID) {
        deviceListeners.forEach(l -> l.deviceAdded(deviceID));
    }

    public void onDeviceRemoved(int deviceID) {
        deviceListeners.forEach(l -> l.deviceRemoved(deviceID));
    }

    public void onDeviceAxisChanged(int[] data) {
        deviceListeners.forEach(l -> l.axisChanged(data));
    }

    public void onDeviceButtonChanged(long button) {
        deviceListeners.forEach(l -> l.buttonChanged(SpaceMouseButtonState.values()[(int)button]));
    }

    public void start(String appName, boolean useSeparateThread) throws Exception {
        if(isDriverInstalled()){
            int error = setConnexionHandlers(this, useSeparateThread);
            if(error != 0){
                throw new SpaceMouseException("setConnexionHandlers failed with error: " + error);
            }
            clientId = registerConnexionClient( Constant.SIGNATURE, appName, Constant.CLIENT_MODE_TAKE_OVER, Constant.MASK_ALL);
            setConnexionClientMask(clientId, Constant.CLIENT_MASK_ALL_BUTTONS);
        }else{
            throw new SpaceMouseException("Driver not installed !");
        }
    }

    @Override
    public void close(){
        unregisterConnexionClient(clientId);
        cleanUpConnexionHandler();
    }

    /*JNI
        #include <jni.h>
        #include <iostream>
        #include <ConnexionClientAPI.h>

        #define NBR_OF_AXIS 6  // x, y, z, rx, ry, rz

        int clientId = 0;
        JavaVM *javaVM;
        jobject spaceMouseObject;
        jmethodID onDeviceAddedJavaCallback;
        jmethodID onDeviceRemovedJavaCallback;
        jmethodID onDeviceAxisChangedJavaCallback;
        jmethodID onDeviceButtonChangedJavaCallback;

        static void MyDeviceMessageHandler(unsigned int connection, unsigned int messageType, void *messageArgument);
        static void MyDeviceAddedHandler(unsigned int connection);
        static void MyDeviceRemovedHandler(unsigned int connection);
        JNIEnv * getEnvAndCheckVersion();
     */

    private static native boolean isDriverInstalled();/*
        return SetConnexionHandlers != NULL;
    */

    public static native int setConnexionHandlers(SpaceMouse spaceMouseOjb, boolean useSeparateThread);/*
	    int error = env->GetJavaVM(&javaVM);
	    if (error != 0) {
		    return error;
	    }
	    spaceMouseObject = env->NewGlobalRef(spaceMouseOjb);
	    jclass cls = env->GetObjectClass(spaceMouseOjb);
	    onDeviceAddedJavaCallback = env->GetMethodID(cls, "onDeviceAdded", "(I)V");
	    onDeviceRemovedJavaCallback = env->GetMethodID(cls, "onDeviceRemoved", "(I)V");
	    onDeviceAxisChangedJavaCallback = env->GetMethodID(cls, "onDeviceAxisChanged", "([I)V");
	    onDeviceAxisChangedJavaCallback = env->GetMethodID(cls, "onDeviceAxisChanged", "([I)V");
	    onDeviceButtonChangedJavaCallback = env->GetMethodID(cls, "onDeviceButtonChanged", "(J)V");
	    error = ConnexionControl(kConnexionCtlGetDeviceID, 0, &clientId);
        return SetConnexionHandlers(MyDeviceMessageHandler, MyDeviceAddedHandler, MyDeviceRemovedHandler, useSeparateThread);
    */

    /**
     *
     * RegisterConnexionClient()
     * register your application with the driver to start the flow of events.
     *
     * @param bundleSignature Is the application’s CFBundleSignature code.
     * @param applicationName Application’s executable name.
     * @param modeTakeOver Reserved. Must be this constant.
     * @param mask Tells the driver what type of events you’re interested in receiving. Using kConnexionMaskAll means give me button and all axis events.
     * @return a unique ID to identify your application to the driver. You will need to save this ID and pass it back to driver when calling certain SDK functions.
     */
    private static native int registerConnexionClient(int bundleSignature, String applicationName, int modeTakeOver, long mask);/*
        clientId = RegisterConnexionClient(bundleSignature, (uint8_t *) obj_applicationName, modeTakeOver, mask);
        return clientId;
    */

    private static native void setConnexionClientMask(int clientId, long mask);/*
	    SetConnexionClientButtonMask(clientId, mask);
    */

    private static native void unregisterConnexionClient(int clientId);/*
        UnregisterConnexionClient(clientId);
        env->DeleteGlobalRef(spaceMouseObject);
	    clientId = 0;
    */

    private static native void cleanUpConnexionHandler();/*
        CleanupConnexionHandlers();
    */

    /*JNI

        JNIEnv * getEnvAndCheckVersion(){
            JNIEnv *env;
            int getEnvStat = javaVM->GetEnv((void **)&env, JNI_VERSION_1_8);
            if (getEnvStat == JNI_EDETACHED) {
                std::cout << "GetEnv: not attached" << std::endl;
                if (javaVM->AttachCurrentThread((void **) &env, NULL) != 0) {
                    std::cout << "Failed to attach" << std::endl;
                }
            } else if (getEnvStat == JNI_OK) {
                //
            } else if (getEnvStat == JNI_EVERSION) {
                std::cout << "GetEnv: version not supported" << std::endl;
            }
	        return env;
        }

        static void MyDeviceMessageHandler(unsigned int connection, unsigned int messageType, void *messageArgument) {
		    ConnexionDeviceState *state;

		    JNIEnv *env;
	        env = getEnvAndCheckVersion();
	        if (NULL == env) {
                return;
	        }

            switch (messageType) {
                case kConnexionMsgDeviceState:
                    state = (ConnexionDeviceState*)messageArgument;
                    if (state->client == clientId)
                    {
                        // decipher what command/event is being reported by the driver
                        switch (state->command) {

				            case kConnexionCmdHandleButtons:
				            	env->CallVoidMethod(spaceMouseObject, onDeviceButtonChangedJavaCallback, state->buttons);
				            	break;

                            case kConnexionCmdHandleAxis:
                                jintArray intarray = env->NewIntArray(NBR_OF_AXIS);
                                int data[NBR_OF_AXIS];
	                            for (int i = 0; i < NBR_OF_AXIS; i++) {
	                            	data[i] = state->axis[i];
	                            }
	                            env->SetIntArrayRegion(intarray, 0, NBR_OF_AXIS, data);
                                env->CallVoidMethod(spaceMouseObject, onDeviceAxisChangedJavaCallback,  intarray);
                                break;
                         }
                    }
                    break;
                default:
                    // other messageTypes can happen and should be ignored
                    break;
            }
        }

        static void MyDeviceAddedHandler(unsigned int connection) {
		    JNIEnv *env;
	        env = getEnvAndCheckVersion();
	        if (env != NULL) {
	        	env->CallVoidMethod(spaceMouseObject, onDeviceAddedJavaCallback, connection);
	        }
        }

        static void MyDeviceRemovedHandler(unsigned int connection) {
		    JNIEnv *env;
	        env = getEnvAndCheckVersion();
	        if (env != NULL) {
	        	env->CallVoidMethod(spaceMouseObject, onDeviceRemovedJavaCallback, connection);
	        }
        }
     */
}