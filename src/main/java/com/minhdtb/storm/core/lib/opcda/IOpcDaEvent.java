package com.minhdtb.storm.core.lib.opcda;


import java.util.Date;

public interface IOPCDaEvent {

    void onChange(String tagName, Object value, Date time, int quality);
}
