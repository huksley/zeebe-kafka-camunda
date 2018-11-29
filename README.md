# Zeebe Kafka project

The goal of this project is to create private environment to explore, test and assess Zeebe.

## Components

  * Zookeeper for Kafka
  * Kafka
  * Kibana
  * Zeebe-simple-monitor
  * Kotlin workers (WIP)

## Installing and running

```bash
git clone https://github.com/huksley/zeebe-kafka-camunda
cd zeebe-kafka-camunda
git clone https://github.com/huksley/zeebe-simple-monitor
cd zeebe-simple-monitor
./gradlew build
cd ..
./fix-perms
docker-compose up -d
cd workers
./gradlew run
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

**Start H2 console**

```
> docker exec -ti zeebe-mon-h2 java -jar bin/h2-1.4.197.jar -web -webAllowOthers
Web Console server running at http://172.24.0.X:8082 (others can connect)
```

To browse H2 database open in browser the printed URL and enter connection URL: `jdbc:h2:tcp://monitor-h2:1521/zeebe`

## Known links

  * Launchpad http://localhost:8180
  * Simple Monitor http://localhost:8181
  * Kibana http://localhost:5601

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

