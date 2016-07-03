package com.minhdtb.storm.core.lib.opcda;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OPCDaClient {

    private HashMap<String, Integer> tagMap = new HashMap<>();
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

    public void disconnect() {
        manager.disconnect(client);
    }

    public List<String> getServers() {
        return Arrays.asList(manager.getOpcServers(client, host));
    }

    public List<String> getTags() {
        return Arrays.asList(manager.getOpcServerTags(client));
    }

    public List<String> getTagBranches(String input) {
        return Arrays.asList(manager.getOpcServerTagBranches(client, input));
    }

    public List<String> getTagLeafs(String input) {
        return Arrays.asList(manager.getOpcServerTagLeafs(client, input));
    }

    public void addTag(String tagName) {
        int handle = manager.addTag(client, tagName);
        tagMap.put(tagName, handle);
    }

    public void removeTag(String tagName) {
        int handle = tagMap.get(tagName);
        manager.removeTag(client, handle);
        tagMap.remove(tagName);
    }

    public void clearTags() {
        tagMap.forEach((tagName, tagHandle) -> {
            manager.removeTag(client, tagHandle);
        });

        tagMap.clear();
    }

    public Object readTag(String tagName) {
        int handle = tagMap.get(tagName);
        return manager.readTag(client, handle);
    }

    public void writeTag(String tagName, Object value) {
        int handle = tagMap.get(tagName);
        manager.writeTag(client, handle, value, value.getClass().getName());
    }

    public void setEvent(IOPCDaEvent event) {
        manager.setEvent(client, event);
    }
}
