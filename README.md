# Zeebe Kafka project

The goal of this project is to create private environment to explore, test and assess Zeebe.

## Components

  * Zookeeper for Kafka
  * Kafka
  * Kibana
  * Zeebe-simple-monitor
  * Kotlin workers

## Installing and running

```bash
git clone https://github.com/huksley/zeebe-kafka-camunda
cd zeebe-kafka-camunda
git clone https://github.com/huksley/zeebe-simple-monitor
cd zeebe-simple-monitor
git checkout custom-schema-mysql
mvn clean package
cd ..
cd workers
./gradlew run
cd ..
./gradle download
./fix-perms
docker-compose up -d
```

Open http://localhost:8080/status to see all processed orders.

## Commands

**Show all available kafka commands in container**

`./kafka`

**Create kafka topic**

`./kafka kafka-topics.sh --zookeeper zookeeper:2181 --create  --topic test --partitions 1 --replication-factor 1`

**Show information about created topic**

`./kafka kafka-topics.sh --zookeeper zookeeper:2181 --describe --topic test`

**Start test console producer**

Type messages on console (one line - one payload) or pipe them

`./kafka kafka-console-producer.sh --broker-list localhost:9200 --topic test`

**Start test console consumer**

Will output incoming messages - one message per line.

`./kafka kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test`

**Open monitor MySQL database**

`mysql -h 127.0.0.1 -P 8106 -u zeebe -p123 zeebe`


## Known links

  * Launchpad http://localhost:8181
  * Simple Monitor http://localhost:8182
  * Kibana http://localhost:8184
  * Metabase http://localhost:8183

## Running and trying out

  * Bring everything
  * Open simple monitor
  * Upload BPMN process test-data/order-process.bpmn using simple monitor
  * Follow Zeebe quick start instructions https://docs.zeebe.io/introduction/quickstart.html
  * Create kibana source using zeebe-record-* pattern, timestamp column
  * Explore current tasks using Kibana, metadata.intent is a status of the job
  * Run endless loop of tasks 
  
`while true; do  ./zbctl create instance order-process --payload '{"orderId": 1234}'; done;`

## Links

  * https://docs.zeebe.io/basics/exporters.html
  * https://forum.zeebe.io/t/simple-monitor-ui/186
  * http://www.searchkit.co/
  * https://github.com/zeebe-io/zeebe-get-started-java-client
  * https://github.com/yaronn/blessed-contrib
  * ?

