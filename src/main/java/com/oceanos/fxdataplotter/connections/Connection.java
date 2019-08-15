package com.oceanos.fxdataplotter.connections;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @autor slonikmak on 17.05.2019.
 */
public interface Connection {
    void setOnReceived(Consumer<String> consumer);
    CompletableFuture<String> getSampleData();
    String getData();
    void close();
}
