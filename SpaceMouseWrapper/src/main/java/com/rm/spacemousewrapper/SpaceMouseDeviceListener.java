package com.rm.spacemousewrapper;

public interface SpaceMouseDeviceListener {
    void deviceAdded(int deviceId);
    void deviceRemoved(int deviceId);
    void axisChanged(int deviceId, int[] values);
    void buttonChanged(int deviceId, SpaceMouseButtonState event);
}
