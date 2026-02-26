package com.seveneleven.devicedetector.plugins;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@CapacitorPlugin(name = "TCPDetector")
public class TCPDetectorPlugin extends Plugin {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @PluginMethod
    public void checkPort(PluginCall call) {
        String host = call.getString("host");
        int port = call.getInt("port", 80);
        int timeout = call.getInt("timeout", 3000);

        if (host == null || host.isEmpty()) {
            call.reject("Host is required");
            return;
        }

        executor.execute(() -> {
            try {
                long startTime = System.currentTimeMillis();
                
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(host, port), timeout);
                socket.close();
                
                long responseTime = System.currentTimeMillis() - startTime;
                
                JSObject result = new JSObject();
                result.put("online", true);
                result.put("responseTime", responseTime);
                call.resolve(result);
            } catch (Exception e) {
                JSObject result = new JSObject();
                result.put("online", false);
                result.put("error", e.getMessage());
                call.resolve(result);
            }
        });
    }

    @PluginMethod
    public void checkPorts(PluginCall call) {
        String networkSegment = call.getString("networkSegment");
        JSObject portsObj = call.getObject("ports");
        int timeout = call.getInt("timeout", 3000);

        if (networkSegment == null || networkSegment.isEmpty() || portsObj == null) {
            call.reject("Network segment and ports are required");
            return;
        }

        executor.execute(() -> {
            JSObject results = new JSObject();
            
            try {
                // 解析设备列表
                String[] deviceKeys = portsObj.keys().next();
                
                while (portsObj.keys().hasNext()) {
                    String deviceId = portsObj.keys().next();
                    JSObject deviceInfo = portsObj.getJSObject(deviceId);
                    
                    if (deviceInfo != null) {
                        int ipSuffix = deviceInfo.getInt("ipSuffix");
                        int port = deviceInfo.getInt("port");
                        String host = networkSegment + "." + ipSuffix;
                        
                        try {
                            long startTime = System.currentTimeMillis();
                            Socket socket = new Socket();
                            socket.connect(new InetSocketAddress(host, port), timeout);
                            socket.close();
                            long responseTime = System.currentTimeMillis() - startTime;
                            
                            JSObject deviceResult = new JSObject();
                            deviceResult.put("online", true);
                            deviceResult.put("responseTime", responseTime);
                            results.put(deviceId, deviceResult);
                        } catch (Exception e) {
                            JSObject deviceResult = new JSObject();
                            deviceResult.put("online", false);
                            deviceResult.put("error", e.getMessage());
                            results.put(deviceId, deviceResult);
                        }
                    }
                }
                
                JSObject finalResult = new JSObject();
                finalResult.put("results", results);
                call.resolve(finalResult);
            } catch (Exception e) {
                call.reject("Error checking ports: " + e.getMessage());
            }
        });
    }

    @PluginMethod
    public void getWifiInfo(PluginCall call) {
        // 获取WiFi信息需要额外权限，这里返回模拟数据
        // 实际实现需要android.permission.ACCESS_WIFI_STATE权限
        JSObject result = new JSObject();
        result.put("ssid", "");
        result.put("ip", "");
        result.put("gateway", "");
        call.resolve(result);
    }
}
