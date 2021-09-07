package io.deephaven.examples;

import io.deephaven.api.ColumnName;
import io.deephaven.client.impl.DaggerDeephavenFlightRoot;
import io.deephaven.client.impl.FlightSession;
import io.deephaven.client.impl.FlightSubcomponent;
import io.deephaven.client.impl.TableHandle;
import io.deephaven.qst.table.TableSpec;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.arrow.flight.FlightStream;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static io.deephaven.api.agg.Aggregation.AggAvg;
import static io.deephaven.api.agg.Aggregation.AggCount;
import static io.deephaven.api.agg.Aggregation.AggMax;
import static io.deephaven.api.agg.Aggregation.AggMed;
import static io.deephaven.api.agg.Aggregation.AggMin;
import static io.deephaven.api.agg.Aggregation.AggSum;

public class SimpleQuery {
    public static void main(String[] args) throws Exception {

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        final ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:10000").usePlaintext().build();
        final BufferAllocator bufferAllocator = new RootAllocator();

        final FlightSubcomponent factory = DaggerDeephavenFlightRoot.create().factoryBuilder()
                .scheduler(scheduler)
                .managedChannel(managedChannel)
                .allocator(bufferAllocator)
                .build();

        final TableSpec spec = TableSpec.empty(1_000_000L)
                .view("I=ii", "K=(int)(I / 100_000)")
                .by(Collections.singleton(ColumnName.of("K")), Arrays.asList(
                        AggCount("Count"),
                        AggSum("Sum=I"),
                        AggMin("Min=I"),
                        AggMax("Max=I"),
                        AggAvg("Avg=I"),
                        AggMed("Median=I")))
                .sort("K");

        try (final FlightSession session = factory.newFlightSession();
             final TableHandle handle = session.session().execute(spec);
             final FlightStream stream = session.getStream(handle.export())) {
            System.out.println(stream.getSchema());
            while (stream.next()) {
                System.out.println(stream.getRoot().contentToTSVString());
            }
        } finally {
            managedChannel.shutdownNow();
            scheduler.shutdownNow();
        }
    }
}
