package com.rm.spacemousewrapper;

import com.badlogic.gdx.utils.SharedLibraryLoader;

public abstract class AbsLoader implements AutoCloseable {

    protected int clientId = 0;

    static {
        new SharedLibraryLoader().load("3Dx");
    }

}
