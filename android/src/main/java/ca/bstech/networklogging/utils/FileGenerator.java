package ca.bstech.networklogging.utils;

import com.facebook.react.bridge.ReactApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class FileGenerator {
    private ReactApplicationContext reactContext;

    public FileGenerator(ReactApplicationContext reactContext) {
        this.reactContext = reactContext;
    }

    public String generateTestFile(String fileName, int sizeKB) throws IOException {
        File file = new File(reactContext.getCacheDir(), fileName);
        String filePath = file.getCanonicalPath();
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            for ( int i=0; i<sizeKB; i++) {
                write1KBData(fos);
            }
        } finally {
            if ( fos != null ) {
                fos.close();
            }
        }
        return filePath;
    }

    private void write1KBData(OutputStream fos) throws IOException {
        byte[] buffer = new byte[1024];
        new Random().nextBytes(buffer);
        fos.write(buffer);
    }
}
