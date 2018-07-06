package com.example.titomi.workertrackerloginmodule.supervisor.util;

import android.content.Context;
import android.os.Environment;

import com.example.titomi.workertrackerloginmodule.supervisor.Entity;

import java.io.File;

/**
 * Created by Titomi on 3/12/2018.
 */

public class AudioUtils extends ImageUtils {

    public static class AudioStorage {
        private static Entity entity;
        private static Context cxt;

        public AudioStorage(Entity entity) {
            this.entity = entity;
        }

        public static <T extends Entity> File getStrorgaeDirectory(T t) {
            File directory = new File(Environment
                    .getExternalStorageDirectory()
                    .getPath(),
                    String.format(".FieldMonitor/Audio/%s/",
                            t.getClass().getSimpleName()).toLowerCase());
            if (!directory.exists()) directory.mkdirs();

            return directory;
        }
    }
}
