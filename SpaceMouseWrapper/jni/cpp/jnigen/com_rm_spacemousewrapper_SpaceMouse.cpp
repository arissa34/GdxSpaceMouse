#include <com_rm_spacemousewrapper_SpaceMouse.h>

//@line:59

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
     JNIEXPORT jboolean JNICALL Java_com_rm_spacemousewrapper_SpaceMouse_isDriverInstalled(JNIEnv* env, jclass clazz) {


//@line:80

        return SetConnexionHandlers != NULL;
    

}

JNIEXPORT jint JNICALL Java_com_rm_spacemousewrapper_SpaceMouse_setConnexionHandlers(JNIEnv* env, jclass clazz, jobject spaceMouseOjb, jboolean useSeparateThread) {


//@line:84

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
    

}

static inline jint wrapped_Java_com_rm_spacemousewrapper_SpaceMouse_registerConnexionClient
(JNIEnv* env, jclass clazz, jint bundleSignature, jstring obj_applicationName, jint modeTakeOver, jlong mask, char* applicationName) {

//@line:111

        clientId = RegisterConnexionClient(bundleSignature, (uint8_t *) obj_applicationName, modeTakeOver, mask);
        return clientId;
    
}

JNIEXPORT jint JNICALL Java_com_rm_spacemousewrapper_SpaceMouse_registerConnexionClient(JNIEnv* env, jclass clazz, jint bundleSignature, jstring obj_applicationName, jint modeTakeOver, jlong mask) {
	char* applicationName = (char*)env->GetStringUTFChars(obj_applicationName, 0);

	jint JNI_returnValue = wrapped_Java_com_rm_spacemousewrapper_SpaceMouse_registerConnexionClient(env, clazz, bundleSignature, obj_applicationName, modeTakeOver, mask, applicationName);

	env->ReleaseStringUTFChars(obj_applicationName, applicationName);

	return JNI_returnValue;
}

JNIEXPORT void JNICALL Java_com_rm_spacemousewrapper_SpaceMouse_setConnexionClientMask(JNIEnv* env, jclass clazz, jint clientId, jlong mask) {


//@line:116

	    SetConnexionClientButtonMask(clientId, mask);
    

}

JNIEXPORT void JNICALL Java_com_rm_spacemousewrapper_SpaceMouse_unregisterConnexionClient(JNIEnv* env, jclass clazz, jint clientId) {


//@line:120

        UnregisterConnexionClient(clientId);
        env->DeleteGlobalRef(spaceMouseObject);
	    clientId = 0;
    

}

JNIEXPORT void JNICALL Java_com_rm_spacemousewrapper_SpaceMouse_cleanUpConnexionHandler(JNIEnv* env, jclass clazz) {


//@line:126

        CleanupConnexionHandlers();
    

}


//@line:130

        JNIEnv * getEnvAndCheckVersion(){
            JNIEnv *env;
            // double check it's all ok
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

        void cstr2pstr(const char *cstr, char *pstr) {
        	int i;
        	for (i = 0; cstr[i]; i++) {
        		pstr[i + 1] = cstr[i];
        	}
        	pstr[0] = i;
        }
     