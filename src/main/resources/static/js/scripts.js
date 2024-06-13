document.addEventListener("DOMContentLoaded", function (event) {
    let averageSalaryHistoryChartInstance;
    let recentAverageSalaryChartInstance;
    let numberOfVacanciesChartInstance;

    function getRecentAnalytics() {
        const url = `/analytics/recent`;
        const queries = [];
        const vacancyCount = [];
        const averageSalaryData = [];
        fetch(url)
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Network response was not ok");
                }
                return response.json();
            })
            .then((data) => {
                data.forEach((element) => {
                    queries.push(element.query);
                    vacancyCount.push(element.vacancyCount);
                    averageSalaryData.push(element.averageSalary);
                })
                buildRecentAverageSalaryChart(queries, averageSalaryData);
                buildnumberOfVacanciesChart(queries, vacancyCount);
            })
            .catch((error) => {
                console.error("Error fetching data:", error);
            });
    }

    function getAnalyticsHistory() {
        const url = `/analytics/queries`;

        fetch(url)
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Network response was not ok");
                }
                return response.json();
            })
            .then((data) => {
                const selectElement = document.createElement("select");
                selectElement.setAttribute("id", "querySelect");

                data.forEach((item) => {
                    const optionElement = document.createElement("option");
                    optionElement.setAttribute("value", item.query);
                    optionElement.textContent = item.query;
                    selectElement.appendChild(optionElement);
                });

                const container = document.getElementById("allAvailableContainer");
                container.appendChild(selectElement);

                addSelectEventHandler(selectElement);
                if (data.length > 0) {
                    const firstItem = data[0];
                    getAnalyticsHistoryForQuery(firstItem.query);
                }
            })
            .catch((error) => {
                console.error("Error fetching data:", error);
            });
    }

    function addSelectEventHandler(selectElement) {
        selectElement.addEventListener("change", function () {
            const selectedValue = selectElement.value;
            getAnalyticsHistoryForQuery(selectedValue);
        });
    }

    function getAnalyticsHistoryForQuery(query) {
        const url = `/analytics/${query}`;

        fetch(url)
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Network response was not ok");
                }
                return response.json();
            })
            .then((data) => {
                const dates = data.map((item) => item.date)
                const salaries = data.map((item) => item.averageSalary)
                buildAverageSalaryHistoryChart(dates, salaries)
            })
            .catch((error) => {
                console.error("Error fetching data:", error);
            });
    }

    function buildAverageSalaryHistoryChart(averageSalaryDates, averageSalaryArray) {
        const averageSalaryByQuery = document.getElementById('averageSalaryByQuery');

        if (averageSalaryHistoryChartInstance) {
            averageSalaryHistoryChartInstance.destroy();
        }

        averageSalaryHistoryChartInstance = new Chart(averageSalaryByQuery, {
            type: 'bar',
            data: {
                labels: averageSalaryDates,
                datasets: [{
                    label: 'Average Salary',
                    data: averageSalaryArray,
                    borderWidth: 1,
                    backgroundColor: ['rgba(29, 207, 32, 0.2)']
                }]
            },
            options: {
                scales: {
                    y: {
                        min: 50000,
                        grace: '50%'
                    }
                }
            }
        });
    }

    function buildRecentAverageSalaryChart(queries, averageSalaryData) {
        const averageSalary = document.getElementById('averageSalary');

        if (recentAverageSalaryChartInstance) {
            recentAverageSalaryChartInstance.destroy();
        }

        recentAverageSalaryChartInstance = new Chart(averageSalary, {
            type: 'bar',
            data: {
                labels: queries,
                datasets: [{
                    label: 'Average Salary',
                    data: averageSalaryData,
                    borderWidth: 1,
                    backgroundColor: ['rgba(75, 192, 192, 0.2)']
                }]
            },
            options: {
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    function buildnumberOfVacanciesChart(queries, vacancyCount) {
        const numberOfVacancies = document.getElementById('numberOfVacancy');

        if (numberOfVacanciesChartInstance) {
            numberOfVacanciesChartInstance.destroy();
        }

        numberOfVacanciesChartInstance = new Chart(numberOfVacancies, {
            type: 'bar',
            data: {
                labels: queries,
                datasets: [{
                    label: 'Number of vacancies',
                    data: vacancyCount,
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    getRecentAnalytics();
    getAnalyticsHistory();
});

