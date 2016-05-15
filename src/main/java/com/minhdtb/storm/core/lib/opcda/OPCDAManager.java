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
        InputStream in = getClass().getResourceAsStream("/native/" + name);
        try {
            File temp = new File(new File(System.getProperty("java.io.tmpdir")), name);
            FileOutputStream fos;

            try {
                fos = new FileOutputStream(temp);
                int read;
                byte[] buffer = new byte[1024];
                while ((read = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
                fos.close();
                in.close();

                System.load(temp.getAbsolutePath());
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

    public List<String> getAllServers(String host) {
        return Arrays.asList(getOpcServers(host));
    }
}
