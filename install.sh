#!/bin/bash

# Install Kafka Connect JDBC
./infras/kafka/connect-download.sh

# Start the services
docker-compose down && docker-compose up -d

# Install Kafka Source Connector
./infras/kafka/connect-install.sh
