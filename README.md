# Zeebe Kafka project

The goal of this project is to create private environment to explore, test and assess Zeebe.

## Components

  * Zookeeper for Kafka
  * Kafka
  * Kibana
  * Zeebe-simple-monitor
  * Kotlin workers (WIP)

## Installing

```bash
git clone https://github.com/huksley/zeebe-kafka-camunda
cd zeebe-kafka-camunda
git clone https://github.com/huksley/zeebe-simple-monitor
cd zeebe-simple-monitor
./gradlew build
cd ..
docker-compose up -d
```

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

