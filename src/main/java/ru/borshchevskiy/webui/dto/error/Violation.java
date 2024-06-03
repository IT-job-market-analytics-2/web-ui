package ru.borshchevskiy.webui.dto.error;

import java.util.Objects;

public class Violation {
    private String fieldName;
    private String message;

    public Violation() {
    }

    public Violation(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Violation violation = (Violation) o;

        if (!Objects.equals(fieldName, violation.fieldName)) return false;
        return Objects.equals(message, violation.message);
    }

    @Override
    public int hashCode() {
        int result = fieldName != null ? fieldName.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
