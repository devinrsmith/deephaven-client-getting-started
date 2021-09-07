package io.deephaven.examples;

import io.deephaven.client.impl.script.Changes;
import io.deephaven.client.impl.script.VariableDefinition;

class Helper {
    static String toPrettyString(Changes changes) {
        final StringBuilder sb = new StringBuilder();
        if (changes.errorMessage().isPresent()) {
            sb.append("Error: ").append(changes.errorMessage().get()).append(System.lineSeparator());
        }
        if (changes.isEmpty()) {
            sb.append("No displayable variables updated").append(System.lineSeparator());
        } else {
            for (VariableDefinition variableDefinition : changes.created()) {
                sb
                    .append(variableDefinition.type())
                    .append(' ')
                    .append(variableDefinition.title())
                    .append(" = <new>")
                    .append(System.lineSeparator());
            }
            for (VariableDefinition variableDefinition : changes.updated()) {
                sb
                    .append(variableDefinition.type())
                    .append(' ')
                    .append(variableDefinition.title())
                    .append(" = <updated>")
                    .append(System.lineSeparator());
            }
            for (VariableDefinition variableDefinition : changes.removed()) {
                sb
                    .append(variableDefinition.type())
                    .append(' ')
                    .append(variableDefinition.title())
                    .append(" <removed>")
                    .append(System.lineSeparator());
            }
        }
        return sb.toString();
    }
}
