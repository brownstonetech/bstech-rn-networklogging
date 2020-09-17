package ca.bstech.networklogging;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Observable;
import java.util.Observer;

public class PingEventEmitter implements Observer {

    private ReactApplicationContext reactContext;
    private String eventType;

    public PingEventEmitter(ReactApplicationContext reactContext, String eventType) {
        this.reactContext = reactContext;
        this.eventType = eventType;
    }

    private void sendEvent(Object params) {
        Log.d(Constants.MODULE_NAME, "Emitting JS event:"+eventType+":"+ params.toString());
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventType, params);
    }

    @Override
    public void update(Observable o, Object result) {
        sendEvent(result);
    }

}
