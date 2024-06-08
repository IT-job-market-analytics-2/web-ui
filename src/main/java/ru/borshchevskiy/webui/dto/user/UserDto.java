package ru.borshchevskiy.webui.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import ru.borshchevskiy.webui.dto.validation.groups.OnUpdate;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;
    @NotNull(message = "Chat id must not be null!", groups = {OnUpdate.class})
    private Long telegramChatId;

    public UserDto() {
    }

    public UserDto(String username, Long telegramChatId) {
        this.username = username;
        this.telegramChatId = telegramChatId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTelegramChatId() {
        return telegramChatId;
    }

    public void setTelegramChatId(Long telegramChatId) {
        this.telegramChatId = telegramChatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDto userDto = (UserDto) o;

        if (!Objects.equals(username, userDto.username)) return false;
        return Objects.equals(telegramChatId, userDto.telegramChatId);
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (telegramChatId != null ? telegramChatId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "username='" + username + '\'' +
                ", telegramChatId=" + telegramChatId +
                '}';
    }
}
