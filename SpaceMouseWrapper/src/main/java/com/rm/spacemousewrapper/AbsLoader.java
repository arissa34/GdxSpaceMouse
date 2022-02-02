package com.rm.spacemousewrapper;

import com.badlogic.gdx.jnigen.BuildTarget;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import java.io.IOException;

public abstract class AbsLoader implements AutoCloseable {

    protected int clientId = 0;

    static {
        try {
            
            BuildTarget.TargetOs os;
            if(SharedLibraryLoader.isMac){
                os = BuildTarget.TargetOs.MacOsX;
            }else if(SharedLibraryLoader.isWindows){
                os = BuildTarget.TargetOs.Windows;
            }else if(SharedLibraryLoader.isLinux){
                os = BuildTarget.TargetOs.Linux;
            }else if(SharedLibraryLoader.isAndroid){
                os = BuildTarget.TargetOs.Android;
            }else{
                os = BuildTarget.TargetOs.IOS;
            }

            String osFolder = os.toString().toLowerCase() + (SharedLibraryLoader.isARM ? "arm" : "") + (SharedLibraryLoader.is64Bit ? "64" : "32");
            NativeUtils.loadLibraryFromJar("/libs/"+ osFolder +"/"+new SharedLibraryLoader().mapLibraryName("3Dx"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
