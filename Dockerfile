# Use the official Playwright Java image
FROM mcr.microsoft.com/playwright/java:v1.56.0-noble

# Set the working directory
WORKDIR /app

# Copy the pom.xml to download dependencies first (optimizes caching)
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy the rest of the application
COPY . .

# Default command to run tests
CMD ["mvn", "test"]
