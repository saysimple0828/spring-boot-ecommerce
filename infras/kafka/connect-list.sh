#!/bin/bash

echo
echo
echo "Connector list"
echo

curl -X GET http://localhost:8083/connectors
