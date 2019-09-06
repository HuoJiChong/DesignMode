package com.derek.framework.Principle;

import java.io.Closeable;
import java.io.IOException;

/**
 * 接口隔离原则
 */

public final class CloseUtils {
    private CloseUtils(){

    }

    public static void closeQuietly(Closeable closeable){
        if (closeable != null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
