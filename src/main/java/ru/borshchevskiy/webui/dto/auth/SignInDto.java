package ru.borshchevskiy.webui.dto.auth;

import java.util.Objects;

public class SignInDto {
    private String username;
    private String password;

    public SignInDto() {
    }

    public SignInDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SignInDto signInDto = (SignInDto) o;

        if (!Objects.equals(username, signInDto.username)) return false;
        return Objects.equals(password, signInDto.password);
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
