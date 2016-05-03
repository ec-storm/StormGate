package com.minhdtb.storm.core.data;

import com.minhdtb.storm.core.engine.StormEngine;
import com.minhdtb.storm.entities.Variable;
import com.minhdtb.storm.entities.VariableAttribute;

import java.util.Objects;

class StormVariable implements IStormVariable {

    private StormEngine engine;

    private Variable variable;

    private Object value;

    private IStormChannel channel;

    String getAttribute(String name) {
        for (VariableAttribute attribute : variable.getAttributes()) {
            if (Objects.equals(attribute.getName(), name)) {
                return attribute.getValue();
            }
        }

        return null;
    }

    void setAttribute(String name, String value) {
        boolean found = false;

        for (VariableAttribute attribute : variable.getAttributes()) {
            if (Objects.equals(attribute.getName(), name)) {
                found = true;
                attribute.setValue(value);
                break;
            }
        }

        if (!found) {
            VariableAttribute attribute = new VariableAttribute();
            attribute.setVariable(variable);
            attribute.setName(name);
            attribute.setValue(value);

            variable.getAttributes().add(attribute);
        }
    }

    public Long getId() {
        return variable.getId();
    }

    StormVariable() {
        variable = new Variable();
    }

    StormVariable(Variable variableNew) {
        variable = variableNew;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        if (!this.value.equals(value)) {
            getEngine().invokeOnChange(getFullName(), this.value, value);
            this.value = value;
        }
    }

    @Override
    public void writeValue(Object value) {
        this.value = value;
    }

    @Override
    public IStormChannel getChannel() {
        return channel;
    }

    @Override
    public void setChannel(IStormChannel channel) {
        this.channel = channel;
    }

    @Override
    public String getName() {
        return variable.getName();
    }

    @Override
    public void setName(String name) {
        variable.setName(name);
    }

    @Override
    public String getFullName() {
        return getChannel().getName() + "." + getName();
    }

    @Override
    public Variable getRaw() {
        return variable;
    }

    @Override
    public StormEngine getEngine() {
        return engine;
    }

    @Override
    public void setEngine(StormEngine engine) {
        this.engine = engine;
    }
}
