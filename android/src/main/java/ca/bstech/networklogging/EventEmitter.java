package ca.bstech.networklogging;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Observable;
import java.util.Observer;

public class EventEmitter implements Observer {

    private ReactApplicationContext reactContext;
    private String eventType;

    public EventEmitter(ReactApplicationContext reactContext, String eventType) {
        this.reactContext = reactContext;
        this.eventType = eventType;
    }

    private void sendEvent(String eventName, Object params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @Override
    public void update(Observable o, Object result) {
        sendEvent(eventType, result);
    }

//    public void emitExceptionMessage(String eventName, Throwable e) {
//        WritableArray array = Arguments.createArray();
//        array.pushString(e.getMessage());
//        sendEvent(eventName, array);
//    }
//
//    public void emitStartMessage() {
//        sendEvent(Constants.DATA_USAGE_START, null);
//    }
//
//    public void emitStopMessage() {
//        sendEvent(Constants.DATA_USAGE_END, null);
//    }

}
