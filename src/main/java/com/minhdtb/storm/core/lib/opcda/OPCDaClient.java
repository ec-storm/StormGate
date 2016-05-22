package com.minhdtb.storm.core.lib.opcda;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OPCDaClient {

    private static final long FILETIME_EPOCH_DIFF = 11644473600000L;

    private static final long FILETIME_ONE_MILLISECOND = 10 * 1000;

    private static IOPCDaEvent event;

    private String host;

    private HashMap<String, Integer> hashMap = new HashMap<>();

    private static long filetimeToMillis(final long filetime) {
        return (filetime / FILETIME_ONE_MILLISECOND) - FILETIME_EPOCH_DIFF;
    }

    private static void onChangeCallback(String name, int value, long time, int quality) {
        if (event != null) {
            event.onChange(name, value, new Date(filetimeToMillis(time)), quality);
        }
    }

    private static String objectToString(Object value) {
        if (value instanceof String)
            return (String) value;

        return null;
    }

    private static int objectToInt(Object value) {
        if (value instanceof Integer)
            return (Integer) value;

        return -1;
    }

    private static double objectToDouble(Object value) {
        if (value instanceof Double)
            return (Double) value;

        return -1;
    }

    private static boolean objectToBoolean(Object value) {
        if (value instanceof Boolean)
            return (Boolean) value;

        return false;
    }

    public OPCDaClient() {
        loadJarDll("OPCDALib.dll");
        initialize();
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

    private native void initialize();

    private native void connect(String host, String progId);

    private native String[] getOpcServers(String host);

    private native String[] getOpcServerTags();

    private native int addTag(String tagName);

    private native void removeTag(int tagHandle);

    private native Object readTag(int tagHandle);

    private native void writeTag(int tagHandle, Object value, String clazz);

    private void writeTag(int tagHandle, Object value) {
        writeTag(tagHandle, value, value.getClass().getName());
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<String> getAllServers() {
        return Arrays.asList(getOpcServers(host));
    }

    public List<String> getAllTags() {
        return Arrays.asList(getOpcServerTags());
    }

    public void setEventHandler(IOPCDaEvent event) {
        OPCDaClient.event = event;
    }

    public void connect(String progId) {
        this.connect(this.host, progId);
    }

    public void disconnect() {

    }

    public void add(String tagName) {
        int handle = addTag(tagName);
        hashMap.put(tagName, handle);
    }

    public void remove(String tagName) {
        int handle = hashMap.get(tagName);
        removeTag(handle);
        hashMap.remove(tagName);
    }

    public Object read(String tagName) {
        int handle = hashMap.get(tagName);
        return readTag(handle);
    }

    public void write(String tagName, Object value) {
        int handle = hashMap.get(tagName);
        writeTag(handle, value);
    }
}
