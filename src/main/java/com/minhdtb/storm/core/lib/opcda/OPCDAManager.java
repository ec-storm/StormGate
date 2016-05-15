package com.minhdtb.storm.core.lib.opcda;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Arrays;
import java.util.List;

@Component
public class OPCDAManager {

    private String host;

    @PostConstruct
    public void initialize() {
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

    private native String[] getOpcServers(String host);

    private native String[] getOpcServerTags(String host, String progId);

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<String> getAllServers() {
        return Arrays.asList(getOpcServers(host));
    }
}
