package com.oceanos.fxdataplotter.chartview;

import com.oceanos.fxdataplotter.exceptions.DataParseException;
import com.oceanos.fxdataplotter.model.DataField;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.fx.ChartCanvas;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChartView extends AnchorPane {

    //"Robo Data Chart"
    private final String chartName;
    private XYLineAndShapeRenderer renderer;

    private Map<DataFieldViewModel, Integer> dataSourceMap = new HashMap<>();

    private ArrayList<ChartData> chartDataArrayList = new ArrayList<>();
    private JFreeChart chart;
    private XYPlot plot;

    private ScheduledExecutorService executorService;

    public ChartView(String name){

        /*setPrefWidth(width);
        setPrefHeight(height);*/

        this.chartName = name;

    }

    public void init(double timeRange){
        chart = ChartFactory.createTimeSeriesChart(chartName, "Время", "", chartDataArrayList.get(0).dataset, true, true, false);
        final XYPlot plot = chart.getXYPlot();
        ValueAxis domAxis = plot.getDomainAxis();
        domAxis.setAutoRange(true);
        domAxis.setFixedAutoRange(timeRange);  // 60 seconds
        domAxis = plot.getRangeAxis();
        ChartCanvas canvas =new ChartCanvas(chart);
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        getChildren().add(canvas);

        for (int i = 1; i < chartDataArrayList.size(); i++) {
            plot.setDataset(i, chartDataArrayList.get(i).dataset);
        }

        for (int i = 0; i < chartDataArrayList.size(); i++) {
            NumberAxis axis = chartDataArrayList.get(i).axis;

            // Расположение шкал. Право/лево
                plot.setRangeAxisLocation(i, AxisLocation.BOTTOM_OR_LEFT);


            // Настройка отображения данных на графике
            // 0 - это индекc серии в dataset. Серий в dataset может быть несколько
            // но здесь по одной серии на каждый dataset
            XYLineAndShapeRenderer render = new XYLineAndShapeRenderer(true, false);
            render.setSeriesPaint(0, chartDataArrayList.get(i).paint);
            render.setSeriesStroke(0, new BasicStroke(3));
            plot.setRenderer(i, render);
            // Цвет названия оси
            // Цвет значений градуировки

            //render.setSeriesShape(i, shape);
            plot.setRangeAxis(i, axis);
            // Связка между осями и данными
            plot.mapDatasetToRangeAxis(i, i);
        }
    }

    public void addDataToSeries(long time, double data, int index){
        TimeSeries series = chartDataArrayList.get(index).series;
        Platform.runLater(()->{
            series.add(new TimeSeriesDataItem(new Millisecond(new Date(time)), data));
            //series.add(new Millisecond(new Date(time)), data);
        });
    }



    public int addChartDataSeries(String name, Paint paint, double min, double max){
        chartDataArrayList.add(new ChartData(name, paint, min, max));
        return chartDataArrayList.size()-1;
    }

    public void addDataSource(DataSourceViewModel dataSourceViewModel){
        dataSourceViewModel.getDataFieldViewModels().stream().filter(DataFieldViewModel::isEnabled).forEach(f->{
            int index = addChartDataSeries(f.getName(), fxToAwtColor(f.getColor()),f.getMin(), f.getMax());
            dataSourceMap.put(f, index);
            //System.out.println(f.getName()+ " "+ index);
        });

    }

    public void startPlotting(){
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(this::update, 0, 50, TimeUnit.MILLISECONDS);
    }

    public void stopPlotting() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(200, TimeUnit.MILLISECONDS);
        executorService.shutdownNow();
        System.out.println("Stop plotting!");
    }

    public void update(){
        dataSourceMap.keySet().forEach(k->{
            addDataToSeries(System.currentTimeMillis(), k.getCurrentValue(), dataSourceMap.get(k));
        });
    }


    static Color fxToAwtColor(javafx.scene.paint.Color fx){
        return new Color((float) fx.getRed(),
                (float) fx.getGreen(),
                (float) fx.getBlue(),
                (float) fx.getOpacity());
    }


    class ChartData{
        String name;
        Paint paint;
        double min;
        double max;
        XYDataset dataset;
        TimeSeries series;
        NumberAxis axis;

        public ChartData(String name, Paint paint, double min, double max) {
            this.name = name;
            this.paint = paint;
            this.min = min;
            this.max = max;

            dataset = new TimeSeriesCollection();
            series = new TimeSeries(name);
            ((TimeSeriesCollection)dataset).addSeries(series);
            axis = new NumberAxis(name);
            axis.setLowerBound(min);
            axis.setUpperBound(max);
        }
    }

}
