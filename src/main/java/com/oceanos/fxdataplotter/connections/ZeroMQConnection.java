package com.oceanos.fxdataplotter.connections;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @autor slonikmak on 17.05.2019.
 */
public class ZeroMQConnection implements Connection{

    private String data;
    private String address;
    private String topic;
    private CompletableFuture<String> sampleData;
    private boolean working = true;
    private ExecutorService executorService;
    private List<Consumer<String>> consumers;

    public ZeroMQConnection(String address, String topic, ExecutorService executorService){
        this.address = address;
        this.topic = topic;
        this.executorService = executorService;
        sampleData = new CompletableFuture<>();
        consumers = new ArrayList<>();
        start();
    }

    public ZeroMQConnection(String address, String topic){
        this(address, topic, Executors.newSingleThreadExecutor());
    }

    private void start(){
        this.executorService.submit(task);
    }


    @Override
    public void setOnReceived(Consumer<String> consumer) {
        this.consumers.add(consumer);
    }

    @Override
    public CompletableFuture<String> getSampleData() {
        return sampleData;
    }

    @Override
    public void close(){
        working = false;
        executorService.shutdownNow();
    }

    private Runnable task = ()-> {
        try (ZContext context = new ZContext()){
            ZMQ.Socket sub = context.createSocket(SocketType.SUB);
            System.out.println("address "+address +" topic "+topic);
            sub.connect(address);
            sub.subscribe(topic);

            String sampleDataString = sub.recvStr().replace(topic,"");
            sampleData.complete(sampleDataString);

            while (!Thread.currentThread().isInterrupted() && working){
                data = sub.recvStr().replace(topic,"");
                consumers.forEach(c->c.accept(data));
            }
        }
    };

    public String getData(){
        return data;
    }
}
