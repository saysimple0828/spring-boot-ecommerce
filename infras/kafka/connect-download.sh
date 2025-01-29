#!/bin/bash

# Kafka Connect JDBC plugin

SCRIPT_PATH=$(dirname $0)
CONNECT_URL="https://d1i4a15mxbxib1.cloudfront.net/api/plugins/confluentinc/kafka-connect-jdbc/versions/10.7.6/confluentinc-kafka-connect-jdbc-10.7.6.zip"
JDBC_FILE_NAME="confluentinc-kafka-connect-jdbc-10.7.6.zip"
TARGET_DIR="$SCRIPT_PATH/jars"

# MySQL Java client
MYSQL_FILE_NAME=mysql-connector-java-8.0.26
MYSQL_ZIP_NAME=$MYSQL_FILE_NAME.zip
MYSQL_JAR_NAME=$MYSQL_FILE_NAME.jar
MYSQL_JAR_URL="https://dev.mysql.com/get/Downloads/Connector-J/$MYSQL_ZIP_NAME"

rm -rf "$TARGET_DIR"
mkdir -p "$TARGET_DIR"

echo "Install Kafka Connect JDBC plugin"
curl -L $CONNECT_URL -o $JDBC_FILE_NAME

unzip $JDBC_FILE_NAME
cp -r confluentinc-kafka-connect-jdbc-10.7.6/lib/* $TARGET_DIR

rm $JDBC_FILE_NAME
rm -rf confluentinc-kafka-connect-jdbc-10.7.6

echo "Install MariaDB Java client"

mkdir -p "$TARGET_DIR"
curl -L $MYSQL_JAR_URL -o $MYSQL_ZIP_NAME

unzip $MYSQL_ZIP_NAME
cp -r $MYSQL_FILE_NAME/$MYSQL_JAR_NAME "$TARGET_DIR"

rm $MYSQL_ZIP_NAME
rm -rf $MYSQL_FILE_NAME

sleep 3
