# DistributedSystemLogger
- Light weight
- Memory overflow protection
- No locking
- Support spans 
# Working
- Log Handler(Java utils logging ) stores the log into memory(ThreadLocal).
- Correlation ID and span details are populated into  added to logs.
- Uses seperate daemon to publish logs to kafka.
# How to use
- Add Dependency or import the JAR
```
    <dependency>
			<groupId>org.vinayakmp007</groupId>
			<artifactId>MicroServiceLoggerLib</artifactId>
			<version>1.0-SNAPSHOT</version>
		</dependency>
```
- Add log handler
```
Logger system = Logger.getLogger("");
        system.addHandler(new MicroServiceLoggerDefaultLogHandler());
```

- Add kafka details
```
  @Bean
  public KafkaProducer<String, MessageRecord> producerForLog() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"172.25.150.239:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageRecordSerializer.class);
        return new KafkaProducer<>(configProps);
    }
```
- Start the daemon .
 ```
 MicroServiceLoggerDaemon.startDaemon(new SystemPropertyReader(), (KafkaProducer<String, MessageRecord>)contextRefreshedEvent.getApplicationContext().getBean("producerForLog"));
```
- Add intercepor code to get correlation id from from upstream request and set correlation id for all downstream requests .
```
        String correlationId = request.getHeader("CORRELID");
        if(correlationId==null){
            MicroServiceLogger.createNewLoggingSession();
        }
        else {
            MicroServiceLogger.createNewLoggingSession(correlationId);
        }
```
and 
```
 request.getHeaders().set("CORRELID", MicroServiceLogger.getCurrentCorrelationId());
```
-Add system properrty before starting the application the kafka topic ame where logs will be written to.

```
-Dmicroservicelogger.topic_name=logs
```
-add normal java util log 
```
  MicroServiceLogger.startSpan("parent span fourth ");
 logger.log(Level.INFO, " This is logged inside span");
 MicroServiceLogger.endSpan();
logger.log(Level.INFO, " This logged outside span");
```

# Example project
Springboot:https://github.com/vinayakmp007/DistributedLoggerExample
