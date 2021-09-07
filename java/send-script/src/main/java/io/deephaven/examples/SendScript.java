package io.deephaven.examples;

import io.deephaven.client.DaggerDeephavenSessionRoot;
import io.deephaven.client.SessionSubcomponent;
import io.deephaven.client.impl.ConsoleSession;
import io.deephaven.client.impl.Session;
import io.deephaven.client.impl.script.Changes;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static io.deephaven.examples.Helper.toPrettyString;

public class SendScript {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Please specify the python script to send");
            System.exit(1);
        }
        final Path path = Paths.get(args[0]);
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        final ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:10000").usePlaintext().build();
        final SessionSubcomponent factory = DaggerDeephavenSessionRoot.create()
                .factoryBuilder()
                .scheduler(scheduler)
                .managedChannel(managedChannel)
                .build();
        try (final Session session = factory.newSession();
             final ConsoleSession console = session.console("python").get()) {
            final Changes changes = console.executeScript(path);
            System.out.println(toPrettyString(changes));
        } finally {
            managedChannel.shutdownNow();
            scheduler.shutdownNow();
        }
    }
}
