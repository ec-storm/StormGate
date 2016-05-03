package com.minhdtb.storm.core.data;

import com.minhdtb.storm.entities.Variable;
import com.minhdtb.storm.entities.VariableAttribute;
import lombok.Data;

import java.util.Objects;

@Data
class StormVariable implements IStormVariable {

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
    public Object read() {
        return value;
    }

    @Override
    public void write(Object value) {
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
    public String getFullName() {
        return getChannel().getName() + "." + getName();
    }
}
