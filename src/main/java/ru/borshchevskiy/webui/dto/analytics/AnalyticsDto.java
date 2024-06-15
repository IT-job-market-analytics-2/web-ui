package ru.borshchevskiy.webui.dto.analytics;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Objects;

public class AnalyticsDto {

    private String query;
    private Long vacancyCount;
    @JsonProperty("averageSalary")
    @JsonAlias("salary")
    private Double averageSalary;
    private LocalDate date;

    public AnalyticsDto() {
    }

    public AnalyticsDto(String query, Long vacancyCount, Double averageSalary, LocalDate date) {
        this.query = query;
        this.vacancyCount = vacancyCount;
        this.averageSalary = averageSalary;
        this.date = date;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Long getVacancyCount() {
        return vacancyCount;
    }

    public void setVacancyCount(Long vacancyCount) {
        this.vacancyCount = vacancyCount;
    }

    public Double getAverageSalary() {
        return averageSalary;
    }

    public void setAverageSalary(Double averageSalary) {
        this.averageSalary = averageSalary;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalyticsDto that = (AnalyticsDto) o;

        if (!Objects.equals(query, that.query)) return false;
        if (!Objects.equals(vacancyCount, that.vacancyCount)) return false;
        if (!Objects.equals(averageSalary, that.averageSalary)) return false;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        int result = query != null ? query.hashCode() : 0;
        result = 31 * result + (vacancyCount != null ? vacancyCount.hashCode() : 0);
        result = 31 * result + (averageSalary != null ? averageSalary.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AnalyticsDto{" +
                "query='" + query + '\'' +
                ", vacancyCount=" + vacancyCount +
                ", averageSalary=" + averageSalary +
                ", date=" + date +
                '}';
    }
}

