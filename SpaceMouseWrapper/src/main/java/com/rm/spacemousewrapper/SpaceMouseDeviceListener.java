package com.rm.spacemousewrapper;

public interface SpaceMouseDeviceListener {
    void deviceAdded(int deviceId);
    void deviceRemoved(int deviceId);
    void axisChanged(int[] values);
    //void buttonChanged(int deviceID, SpaceMouseButton button, SpaceMouseButtonEvent event);
    void buttonChanged(SpaceMouseButtonState event);
}
