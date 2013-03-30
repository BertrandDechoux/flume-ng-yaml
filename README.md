flume-ng-yaml
=================

Tired of flume-ng properties configuration? Use YAML!

Properties are a great standard way of configuring but sometimes having something more readable is clearly wanted.

flume-ng-yaml allows you to convert your properties configuration back and forth from YAML. It also provides a custom configuration provider, that way the YAML configuration can be read directly by flume-ng.

More information?
* look at org.apache.flume.cli.Properties2Yaml.java
* look at org.apache.flume.cli.Yaml2Properties.java
* look at the flume website : http://flume.apache.org/

You could use

```yaml
host1:
    sources:
        avroSource:
            type: org.apache.flume.source.AvroSource
            runner:
                type: avro
                port: 11001
            channels: jdbcChannel
            selector.type: replicating

        thriftSource:
            type: org.apache.flume.source.ThriftSource
            runner:
                type: thrift
                port: 12001
            channels: jdbcChannel

    channels:
        jdbcChannel:
            type: jdbc
            jdbc:
                driver: com.mysql.jdbc.Driver
                connect.url: http://localhost/flumedb
                username: flume
                password: flume

    sinks:
        hdfsSink:
            type: hdfs
            hdfs.path: hdfs://localhost/
            batchsize: 1000
            runner:
                type: polling
                polling.interval: 60
```

instead of 

```properties
host1.sources = avroSource thriftSource
host1.channels = jdbcChannel
host1.sinks = hdfsSink

# avroSource configuration
host1.sources.avroSource.type = org.apache.flume.source.AvroSource
host1.sources.avroSource.runner.type = avro
host1.sources.avroSource.runner.port = 11001
host1.sources.avroSource.channels = jdbcChannel
host1.sources.avroSource.selector.type = replicating

# thriftSource configuration
host1.sources.thriftSource.type = org.apache.flume.source.ThriftSource
host1.sources.thriftSource.runner.type = thrift
host1.sources.thriftSource.runner.port = 12001
host1.sources.thriftSource.channels = jdbcChannel

# jdbcChannel configuration
host1.channels.jdbcChannel.type = jdbc
host1.channels.jdbcChannel.jdbc.driver = com.mysql.jdbc.Driver
host1.channels.jdbcChannel.jdbc.connect.url = http://localhost/flumedb
host1.channels.jdbcChannel.jdbc.username = flume
host1.channels.jdbcChannel.jdbc.password = flume

# hdfsSink configuration
host1.sinks.hdfsSink.type = hdfs
host1.sinks.hdfsSink.hdfs.path = hdfs://localhost/
host1.sinks.hdfsSink.batchsize = 1000
host1.sinks.hdfsSink.runner.type = polling
host1.sinks.hdfsSink.runner.polling.interval = 60
```