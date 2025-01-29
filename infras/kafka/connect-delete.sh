#!/bin/bash

echo "Delete connectors"
echo

curl -X DELETE http://localhost:8083/connectors/decosk-order-source-connect
curl -X DELETE http://localhost:8083/connectors/decosk-order-sink-connect

./connect-list.sh