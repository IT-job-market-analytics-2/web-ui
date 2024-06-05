package ru.borshchevskiy.webui.dto.subscription;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.borshchevskiy.webui.dto.validation.groups.OnUpdate;

import java.util.Objects;

public class SubscriptionDto {
    @NotBlank(message = "Subscription can not be null!", groups = {OnUpdate.class})
    private String query;

    public SubscriptionDto() {
    }
    public SubscriptionDto(String subscription) {
        this.query = subscription;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubscriptionDto that = (SubscriptionDto) o;

        return Objects.equals(query, that.query);
    }

    @Override
    public int hashCode() {
        return query != null ? query.hashCode() : 0;
    }
}
