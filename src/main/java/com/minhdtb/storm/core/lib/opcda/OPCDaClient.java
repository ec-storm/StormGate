package com.minhdtb.storm.core.lib.opcda;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OPCDaClient {

    private HashMap<String, Integer> mapTag = new HashMap<>();
    private OPCDaManager manager = OPCDaManager.getInstance();
    private long client;
    private String host;

    public OPCDaClient() {
        client = manager.create();
    }

    public void destroy() {
        manager.destroy(client);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void connect(String progId) {
        manager.connect(client, host, progId);
    }

    public List<String> getServers() {
        return Arrays.asList(manager.getOpcServers(client, host));
    }

    public List<String> getTags() {
        return Arrays.asList(manager.getOpcServerTags(client));
    }

    public void addTag(String tagName) {
        int handle = manager.addTag(client, tagName);
        mapTag.put(tagName, handle);
    }

    public void removeTag(String tagName) {
        int handle = mapTag.get(tagName);
        manager.removeTag(client, handle);
        mapTag.remove(tagName);
    }

    public Object readTag(String tagName) {
        int handle = mapTag.get(tagName);
        return manager.readTag(client, handle);
    }

    public void writeTag(String tagName, Object value) {
        int handle = mapTag.get(tagName);
        manager.writeTag(client, handle, value, value.getClass().getName());
    }

    public void setEvent(IOPCDaEvent event) {
        manager.setEvent(client, event);
    }
}
