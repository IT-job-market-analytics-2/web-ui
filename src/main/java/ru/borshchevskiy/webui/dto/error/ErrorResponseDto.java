package ru.borshchevskiy.webui.dto.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponseDto {
    private String message;
    private List<Violation> violations;

    public ErrorResponseDto() {
    }

    public ErrorResponseDto(String message, List<Violation> violations) {
        this.message = message;
        this.violations = violations;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ErrorResponseDto that = (ErrorResponseDto) o;

        if (!Objects.equals(message, that.message)) return false;
        return Objects.equals(violations, that.violations);
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (violations != null ? violations.hashCode() : 0);
        return result;
    }
}
