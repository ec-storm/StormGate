package com.minhdtb.storm.core;

import com.minhdtb.storm.entities.Variable;
import com.minhdtb.storm.entities.VariableAttribute;
import lombok.Data;

import java.util.Objects;

@Data
class CoreVariable {

    protected Variable variable;

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

    CoreVariable() {
        variable = new Variable();
    }

    protected CoreVariable(Variable variableNew) {
        variable = variableNew;
    }
}
