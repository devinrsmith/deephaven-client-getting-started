# Java Client Getting Started Guide

Deephaven publishes four Java client libraries into Maven Central:

 * `deephaven-client-session`
 * `deephaven-client-session-dagger`
 * `deephaven-client-flight`
 * `deephaven-client-flight-dagger`

`deephaven-client-session` is the library for the Deephaven specific APIs around creating and executing queries.
`deephaven-client-flight` is the Arrow integration library for fetching data; it extends the session library.
The `-dagger` versions are [Dagger](https://dagger.dev/) libraries to aid in construction of sessions.

For this guide, we will use `deephaven-client-flight-dagger` since it contains the most functionality.

### Gradle

To use Deephaven from a gradle project, add the appropriate dependency into the dependencies stanza of your build.gradle file:

```groovy
dependencies {
    implementation 'io.deephaven:deephaven-client-flight-dagger:{version}'
}
```

### Maven

To use Deephaven from a maven project, add the appropriate dependency into the dependency section of your pom.xml file:

```xml
<dependency>
    <groupId>io.deephaven</groupId>
    <artifactId>deephaven-client-flight-dagger</artifactId>
    <version>{version}</version>
</dependency>
```

### Creating a session

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:10000").usePlaintext().build();
BufferAllocator bufferAllocator = new RootAllocator();

FlightSession session = DaggerDeephavenFlightRoot.create().factoryBuilder()
    .scheduler(scheduler)
    .managedChannel(managedChannel)
    .allocator(bufferAllocator)
    .build()
    .newFlightSession();
```

### Executing a query

```java
TableHandle handle = session.session().execute(query);
```

### Fetching data

```java
FlightStream stream = session.getStream(handle.export())
```

### Printing data

```java
System.out.println(stream.getSchema());
while (stream.next()) {
    System.out.println(stream.getRoot().contentToTSVString());
}
```

For more details on interacting with the data, see the [Arrow documentation](https://arrow.apache.org/docs/java/index.html).

### Source

For the complete source for the above code, see [SimpleQuery.java](simple-query/src/main/java/io/deephaven/examples/SimpleQuery.java).

### Examples

There are two full examples:

* [simple-query](simple-query)
* [send-script](send-script)

These can both be built via:

```
./gradlew installDist
```

To execute the simple-query locally:

```
./simple-query/build/install/simple-query/bin/simple-query
```

To execute the send-script locally:

```
./send-script/build/install/send-script/bin/send-script example.py
```

### Javadocs

For reference documentation, see [Java client javadocs](todo - https://github.com/deephaven/deephaven-core/pull/1257).
