#!/bin/bash

echo "🚀 Starting APC Parking Management System Backend..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 14 or higher."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven."
    exit 1
fi

# Check if PostgreSQL is running
if ! pg_isready -q; then
    echo "❌ PostgreSQL is not running. Please start PostgreSQL service."
    exit 1
fi

echo "✅ Prerequisites check passed!"

# Clean and compile
echo "🔨 Building the project..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "❌ Build failed. Please check the errors above."
    exit 1
fi

echo "✅ Build successful!"

# Start the Spring Boot application
echo "🚀 Starting Spring Boot application..."
mvn spring-boot:run

echo "🎉 Backend started successfully!"
echo "📡 API available at: http://localhost:8080"
echo "📚 API Documentation: http://localhost:8080/api"
