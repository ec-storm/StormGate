package com.minhdtb.storm.core.lib.opcda;

import java.io.*;
import java.util.Date;
import java.util.HashMap;

class OPCDaManager {

    private static OPCDaManager instance;

    static OPCDaManager getInstance() {
        if (instance == null) {
            instance = new OPCDaManager();
        }

        return instance;
    }

    private static final long FILETIME_EPOCH_DIFF = 11644473600000L;

    private static final long FILETIME_ONE_MILLISECOND = 10 * 1000;

    private static HashMap<Long, IOPCDaEvent> eventMap = new HashMap<>();

    private static long filetimeToMillis(final long filetime) {
        return (filetime / FILETIME_ONE_MILLISECOND) - FILETIME_EPOCH_DIFF;
    }

    private static void onChangeCallback(long client, String name, Object value, long time, int quality) {
        IOPCDaEvent event = eventMap.get(client);
        if (event != null) {
            event.onChange(name, value, new Date(filetimeToMillis(time)), quality);
        }
    }

    private OPCDaManager() {
        loadJarDll("OPCDALib.dll");
    }

    private void loadJarDll(String name) {
        InputStream inputStream = getClass().getResourceAsStream("/native/" + name);
        try {
            File tempFile = new File(new File(System.getProperty("java.io.tmpdir")), name);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(tempFile);

                int byteRead;
                byte[] buffer = new byte[1024];

                while ((byteRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, byteRead);
                }

                fileOutputStream.close();
                inputStream.close();

                System.load(tempFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public native long create();

    public native void destroy(long client);

    public native void connect(long client, String host, String progId);

    public native void disconnect(long client);

    public native String[] getOpcServers(long client, String host);

    public native String[] getOpcServerTags(long client);

    public native int addTag(long client, String tagName);

    public native void removeTag(long client, int tagHandle);

    public native Object readTag(long client, int tagHandle);

    public native void writeTag(long client, int tagHandle, Object value, String clazz);

    public void setEvent(long client, IOPCDaEvent event) {
        eventMap.put(client, event);
    }
}
