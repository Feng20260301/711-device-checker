package com.seveneleven.devicedetector;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;
import com.seveneleven.devicedetector.plugins.TCPDetectorPlugin;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(TCPDetectorPlugin.class);
        super.onCreate(savedInstanceState);
    }
}
