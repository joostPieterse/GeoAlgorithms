package decomposed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by s130604 on 22-3-2017.
 */
public class Statistic {
    SpaceTimeCube cube;

    public Statistic(SpaceTimeCube cube) {
        this.cube = cube;
    }

    private int customStatisticPerCell(SpaceTimeCube.SpaceTimeCell cell) {
        int result = cube.getNeighbours(cell).stream().map(c->c.cell.getValue()).reduce(0, Integer::sum);
        result += 25 * cell.cell.getValue();
        return result;
    }
    /*
    private int neightbourSum(int latitude, int longitude, int time) {
        int result = 0;
        AttributePlane[] planes = cube.planes;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    if (longitude + i < planes[time].getPlane()[0].length && longitude + i >= 0 &&
                            latitude + j < planes[time].getPlane().length && latitude + j >= 0 &&
                            time + k < planes.length && time + k >= 0) {
                        result += planes[time + k].getPlane()[latitude + j][longitude + i];
                    }
                }
            }
        }
        return result;
    }
    *//*
    private int neightbourSumSquared(int latitude, int longitude, int time) {
        int result = 0;
        AttributePlane[] planes = cube.planes;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    if (longitude + i < planes[time].getPlane()[0].length && longitude + i >= 0 &&
                            latitude + j < planes[time].getPlane().length && latitude + j >= 0 &&
                            time + k < planes.length && time + k >= 0) {
                        result += Math.pow(planes[time + k].getPlane()[latitude + j][longitude + i], 2);
                    }
                }
            }
        }
        return result;
    }
    */

    /**
     * @param numResults the number of cells that is returned
     * @return the greatest cells according to this statistics
     */
    public Map<SpaceTimeCube.SpaceTimeCell, Double> customStatistic(int numResults) {
        System.out.println("Start calculating statistic");
        Map<SpaceTimeCube.SpaceTimeCell, Integer> statisticValues = new HashMap<>();
        int l = -1;
        long time = System.currentTimeMillis();
        for (SpaceTimeCube.SpaceTimeCell cell : cube) {
            if (cell.getTimeLocation() > l) {
                System.out.println(++l + ": " + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();
            }
            statisticValues.put(cell, customStatisticPerCell(cell));
        }
        
        System.out.println("Retrieve sum of each plane");
        Map<Integer, Integer> maxs = new HashMap<>();
        for (Map.Entry<SpaceTimeCube.SpaceTimeCell, Integer> e : statisticValues.entrySet()) {
            maxs.merge(e.getKey().getTimeLocation(), e.getValue(), Integer::sum);
        }
        System.out.println("Sums: ");
        for (Map.Entry<Integer, Integer> e : maxs.entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }
        
        System.out.println("Divide all statistics with plane sum");
        Map<SpaceTimeCube.SpaceTimeCell, Double> statisticNormal = new HashMap<>();
        for (Map.Entry<SpaceTimeCube.SpaceTimeCell, Integer> e : statisticValues.entrySet()) {
            statisticNormal.put(e.getKey(), (double) e.getValue() / (double) maxs.get(e.getKey().getTimeLocation()));
        }
        
        //retrieve x cells with largest statistic
        System.out.println("Retrieving top " + numResults + " cells");
        Stream<Map.Entry<SpaceTimeCube.SpaceTimeCell, Double>> stream = statisticNormal.entrySet().stream().sorted((a, b)->{
            return Double.compare(b.getValue(), a.getValue());
        }).filter(e->e.getValue() > 0);
        if (numResults > 0) {
            stream = stream.limit(numResults);
        }
        return stream.collect(Collectors.toMap(Map.Entry::getKey, e->{
            return (double) e.getValue();
        }));
    }

    /*
    private HashMap<SpaceTimeCell, Double> getMaxValues(double[][][] statisticValues, int numResults) {
        System.out.println("Start calculating max values");
        HashMap<SpaceTimeCell, Double> maxValueMap = new HashMap<>();
        for (int longitude = 0; longitude < statisticValues.length; longitude++) {
            for (int latitude = 0; latitude < statisticValues[longitude].length; latitude++) {
                for (int time = 0; time < statisticValues[longitude][latitude].length; time++) {
                    double currentValue = statisticValues[longitude][latitude][time];
                    SpaceTimeCell currentCell = new SpaceTimeCell(latitude, longitude, time);
                    //add cell if the map is not yet full, check if it is larger than the smallest value in the map otherwise
                    if (maxValueMap.size() < numResults) {
                        maxValueMap.put(currentCell, (double) statisticValues[longitude][latitude][time]);
                    } else {
                        SpaceTimeCell smallestCell = null;
                        double smallestValue = Double.MAX_VALUE;
                        //check if there is a larger value in the map
                        for (SpaceTimeCell cell : maxValueMap.keySet()) {
                            double value = statisticValues[cell.longitude][cell.latitude][cell.time];
                            if (value < smallestValue) {
                                smallestCell = cell;
                                smallestValue = value;
                            }
                        }
                        if (currentValue > smallestValue) {
                            maxValueMap.remove(smallestCell);
                            maxValueMap.put(currentCell, (double) currentValue);
                        }
                    }
                }
            }
        }
        return maxValueMap;
    }
    */

    public Map<SpaceTimeCube.SpaceTimeCell, Double> getisOrdStatistic(int numResults) {
        Map<SpaceTimeCube.SpaceTimeCell, Double> Xbar = new HashMap<>();
        for (SpaceTimeCube.SpaceTimeCell cell : cube) {
            int sum = cube.getNeighbours(cell).stream().collect(Collectors.reducing(0, c->c.cell.getValue(), Integer::sum));
            Xbar.put(cell, sum / 27.0);
        }
        Map<SpaceTimeCube.SpaceTimeCell, Double> XbarSqrt = Xbar.entrySet().stream().collect(Collectors.toMap(e->e.getKey(), e->Math.pow(e.getValue(), 2)));
        Map<SpaceTimeCube.SpaceTimeCell, Double> S = new HashMap<>();
        for (SpaceTimeCube.SpaceTimeCell cell : cube) {
            int sum = cube.getNeighbours(cell).stream().collect(Collectors.reducing(0, c->(int) Math.pow(c.cell.getValue(), 2), Integer::sum));
            Xbar.put(cell, Math.sqrt((sum / 27.0) - XbarSqrt.get(cell)));
        }
        //always 0...?
        
        return new HashMap<>();
    }

    public void getJson(Map<SpaceTimeCube.SpaceTimeCell, Double> map) {
        ArrayList<CellResult> resultArray = new ArrayList<>();
        for (SpaceTimeCube.SpaceTimeCell cell : map.keySet()) {
            System.out.println("[" + cell.cell.getCenter().longitude + ", " + cell.cell.getCenter().latitude + ", " + cell.getTimeLocation() + "] " + map.get(cell));
            String lat = Double.toString(cell.cell.getCenter().latitude);
            String lng = Double.toString(cell.cell.getCenter().longitude);
            int time = cell.getTimeLocation();
            double val = map.get(cell);
            CellResult result = new CellResult(time, lat, lng, val);
            resultArray.add(result);
        }
        try {
            Writer writer = new FileWriter("custom.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(resultArray, writer);
        } catch (IOException ex) {
            System.err.println("couldnt write file");
        }
    }

    private class CellResult {
        int Time;
        String Lat;
        String Long;
        double Value;

        public CellResult(int time, String lat, String aLong, double value) {
            Time = time;
            Lat = lat;
            Long = aLong;
            Value = value;
        }
    }
}