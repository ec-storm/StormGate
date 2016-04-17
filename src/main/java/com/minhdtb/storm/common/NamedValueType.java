package com.minhdtb.storm.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public final class NamedValueType {
    private String name;
    private int value;

    @Override
    public String toString() {
        return getName();
    }
}