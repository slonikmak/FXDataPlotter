package com.oceanos.fxdataplotter.logger;

import com.oceanos.fxdataplotter.data_source.DataSource;
import com.oceanos.fxdataplotter.model.DataField;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;

/**
 * @autor slonikmak on 14.08.2019.
 */
public class DataLogger {

    private final String fileExtension = ".csv";
    private ScheduledExecutorService executorService;
    private List<DataSource> dataSources;
    private BufferedWriter writer;
    private String delimiter;

    public DataLogger(){
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void startLogging(List<DataSource> dataSources, String dirToSave, String fileName, String delimiter) throws IOException {

        this.dataSources = dataSources;
        this.delimiter = delimiter;

        Path pathToDir = Paths.get(dirToSave);
        if (!Files.exists(pathToDir)){
            Files.createDirectories(pathToDir);
        }
        Path pathToFile = pathToDir.resolve(fileName+fileExtension);
        if (Files.exists(pathToFile)){
            Files.delete(pathToFile);
        }

        Files.createFile(pathToFile);

        System.out.println("Path to file: "+pathToFile.toString());
        String header = getHeader(dataSources, delimiter);
        System.out.println("Header: "+header);

        //Files.write(pathToFile, header.getBytes());

        writer = Files.newBufferedWriter(pathToFile);

        writeString(header);

        executorService.scheduleWithFixedDelay(saveTask, 0, 100, TimeUnit.MILLISECONDS);


    }

    public void stopLogging() throws IOException, InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
        executorService.shutdownNow();
        writer.flush();
        writer.close();

    }

    private String getHeader(List<DataSource> dataSources, String delimiter){
        StringJoiner joiner = new StringJoiner(delimiter);
        dataSources.forEach(d->d.getFields().forEach(f->joiner.add(d.getName()+"_"+f.getName())));
        return joiner.toString();
    }

    private void writeString(String data) throws IOException {
        writer.write(data+"\r\n");
    }

    private Runnable saveTask = ()->{
        StringJoiner joiner = new StringJoiner(delimiter);
        dataSources.forEach(d->d.getFields().forEach(f->joiner.add(String.valueOf(f.getValue()))));
        try {
            writeString(joiner.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    };



}
