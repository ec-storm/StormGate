package com.minhdtb.storm.core.lib.opcda;


import java.util.Date;

public interface IOpcDaEvent {

    void onChange(String tagName, Object value, Date time, int quality);
}
