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
        int result = cube.getNeighbours(cell).stream().map(c->c.getValue()).reduce(0, Integer::sum);
        result += 25 * cell.getValue();
        return result;
    }

    /**
     * @param numResults the number of cells that is returned
     * @return the greatest cells according to this statistics
     */
    public Map<SpaceTimeCube.SpaceTimeCell, Double> customStatistic(int numResults) {
        System.out.println("Starting calculation of statistic");
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
        
        System.out.println("Retrieving sum of each plane");
        Map<Integer, Integer> maxs = new HashMap<>();
        for (Map.Entry<SpaceTimeCube.SpaceTimeCell, Integer> e : statisticValues.entrySet()) {
            maxs.merge(e.getKey().getTimeLocation(), e.getValue(), Integer::sum);
        }
        
        System.out.println("Dividing all statistics with plane sum");
        Map<SpaceTimeCube.SpaceTimeCell, Double> statisticNormal = new HashMap<>();
        for (Map.Entry<SpaceTimeCube.SpaceTimeCell, Integer> e : statisticValues.entrySet()) {
            statisticNormal.put(e.getKey(), (double) e.getValue() / (double) maxs.get(e.getKey().getTimeLocation()));
        }
        
        return statisticTopX(statisticNormal, numResults);
    }
    
    public Map<SpaceTimeCube.SpaceTimeCell, Double> getisOrdStatistic(int numResults) {
        System.out.println("Starting calculation of Getis Ord statistic");
        int n = cube.getSize();
        int Xbar = 0;
        int XsqrtBar = 0;
        for (SpaceTimeCube.SpaceTimeCell cell : cube) {
            Xbar += cell.getValue();
            XsqrtBar += cell.getValue() * cell.getValue();
        }
        Xbar /= n;
        XsqrtBar /= n;
        int XbarSqrt = Xbar * Xbar;
        double S = Math.sqrt(XsqrtBar - XbarSqrt);
        double rightS = Math.sqrt((n*27 - 729)/(n - 1));
        double bottom = S*rightS;
        
        Map<SpaceTimeCube.SpaceTimeCell, Double> statisticValues = new HashMap<>();
        for (SpaceTimeCube.SpaceTimeCell cell : cube) {
            double neighbourSum = cube.getNeighbours(cell).stream().collect(Collectors.summingDouble(e->(double) e.getValue()));
            statisticValues.put(cell, (neighbourSum - (Xbar * 27))/bottom);
        }
        
        return statisticTopX(statisticValues, numResults);
    }

    private Map<SpaceTimeCube.SpaceTimeCell, Double> statisticTopX(Map<SpaceTimeCube.SpaceTimeCell, Double> statisticNormal, int numResults) {
        //group by planes
        System.out.println("Grouping statistics by plane");
        Map<Integer, Map<SpaceTimeCube.SpaceTimeCell, Double>> statisticByPlane = statisticNormal.entrySet().stream()
                .collect(Collectors.groupingBy(e->e.getKey().getTimeLocation(), Collectors.toMap(e->e.getKey(), e->e.getValue())));
        
        //get top numResults from each plane
        System.out.println("Getting top " + numResults + " for each plane");
        Map<Integer, Map<SpaceTimeCube.SpaceTimeCell, Double>> statisticByPlaneTop = new HashMap<>();
        for (Map.Entry<Integer, Map<SpaceTimeCube.SpaceTimeCell, Double>> planeStats : statisticByPlane.entrySet()) {
            statisticByPlane.put(planeStats.getKey(), planeStats.getValue().entrySet().stream()
                    .sorted((a, b)->Double.compare(b.getValue(), a.getValue()))
                    .limit(numResults)
                    .collect(Collectors.toMap(e->e.getKey(), e->e.getValue())));
        }
        //flatten top results and return
        System.out.println("Flattening result for output");
        return statisticByPlane.entrySet().stream()
                .flatMap(e->e.getValue().entrySet().stream()).collect(Collectors.toMap(e->e.getKey(), e->e.getValue()));
    }

    public void getJson(Map<SpaceTimeCube.SpaceTimeCell, Double> map, String filename) {
        ArrayList<CellResult> resultArray = new ArrayList<>();
        for (SpaceTimeCube.SpaceTimeCell cell : map.keySet()) {
            //System.out.println("[" + cell.cell.getCenter().longitude + ", " + cell.cell.getCenter().latitude + ", " + cell.getTimeLocation() + "] " + map.get(cell));
            String lat = Double.toString(cell.cell.getCenter().latitude);
            String lng = Double.toString(cell.cell.getCenter().longitude);
            int time = cell.getTimeLocation();
            double val = map.get(cell);
            CellResult result = new CellResult(time, lat, lng, val);
            resultArray.add(result);
        }
        try (Writer writer = new FileWriter(filename, false)) {
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