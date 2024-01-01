# DistributedSystemLogger
- Light weight
- Memory overflow protection
- No locking
- Support spans 
# Working
- Log Handler(Java utls logging ) stores the log into memory(ThreadLocal).
- Correlation ID and span details are populated into  added to logs.
- Uses seperate daemon to publish logs to kafka.
