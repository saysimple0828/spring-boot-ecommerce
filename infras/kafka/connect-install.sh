# Wait for Kafka Connect to start
echo "Waiting for Kafka Connect to start..."
while [ $(curl -s -o /dev/null -w %{http_code} http://localhost:8083/connectors) -eq 000 ]
do
  echo -e $(date) " Kafka Connect listener HTTP state: " $(curl -s -o /dev/null -w %{http_code} http://localhost:8083/connectors) " (waiting for 200)"
  sleep 5
done

ORDER_SOURCE_CONNECT=decosk-order-source-connect
ORDER_SINK_CONNECT=decosk-order-sink-connect

# Delete the connectors
./connect-delete.sh

# echo '
# {
#   "name" : "decosk-order-source-connect",
#   "config" : {
#     "connector.class" : "io.confluent.connect.jdbc.JdbcSourceConnector",
#     "connection.url":"jdbc:mysql://mariadb:3306/mydb",
#     "connection.user":"test",
#     "connection.password":"test1234",
#     "mode": "incrementing",
#     "incrementing.column.name" : "id",
#     "table.whitelist":"users",
#     "topic.prefix" : "order",
#     "tasks.max" : "1"
#   }
# }
# ' | curl -X POST -d @- http://localhost:8083/connectors --header "content-Type:application/json"

echo '
{
  "name":"decosk-order-sink-connect",
      "config": {
      "connector.class":"io.confluent.connect.jdbc.JdbcSinkConnector",
      "connection.url":"jdbc:mysql://mariadb:3306/mydb",
      "connection.user":"root",
      "connection.password":"mariadb",
      "auto.create":"true",
      "auto.evolve":"true",
      "delete.enabled":"false",
      "tasks.max":"1",
      "topics":"order"
  }
}
'| curl -X POST -d @- http://localhost:8083/connectors --header "content-Type:application/json"

./kafka/connect-list.sh
