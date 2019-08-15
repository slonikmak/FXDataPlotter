package com.oceanos.fxdataplotter.tests.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @autor slonikmak on 20.05.2019.
 */
public class TestServer {
    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper();

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(()->{try (ZContext context = new ZContext()){
            ZMQ.Socket pub = context.createSocket(SocketType.PUB);
            pub.bind("tcp://*:8000");

            while (!Thread.currentThread().isInterrupted()){

                SampleClass sampleClass = new SampleClass();
                sampleClass.val1 = Math.random();
                sampleClass.val2 = Math.random()+1;
                sampleClass.val3 = Math.random()+2;

                pub.send("topic/"+mapper.writeValueAsString(sampleClass));
                Thread.sleep(100);
            }

        } catch (InterruptedException | JsonProcessingException e) {
            e.printStackTrace();
        }});


        executorService.submit(()->{
            try (ZContext context = new ZContext()){
            ZMQ.Socket pub = context.createSocket(SocketType.PUB);
            pub.bind("tcp://*:8001");

            while (!Thread.currentThread().isInterrupted()){

                String data = Math.random() +";"+Math.random()*10;

                pub.send("topic/"+data);
                Thread.sleep(100);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }});


    }
}
