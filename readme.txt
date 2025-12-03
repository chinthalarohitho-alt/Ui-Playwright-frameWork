# Playwright Test Automation Framework

Automated testing framework using Playwright-Java, Cucumber BDD, and Maven.

---

## Prerequisites

- Java 17+
- Maven 3.8+

---

## Setup

Clone repository
git clone https://github.com/chinthalarohitho-alt/Playwright-frameWork.git
cd playwright-framework

Install dependencies and browsers
mvn clean install
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"

## Run Tests
mvn clean test -Dcucumber.filter.tags="@Tag" -Denv=EnvironmentName

## Project Structure

src/main/java/
├── config/ → Configuration
├── pages/ → Page Objects
└── utilze/ → Utilities

src/test/
├── java/steps/ → Step Definitions
└── resources/ → Feature Files

---

## Tech Stack

Java | Maven | Playwright | Cucumber | TestNG | Allure

---