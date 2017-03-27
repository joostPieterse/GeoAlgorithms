
package decomposed;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

/**
 * @author s132054
 */
public class Main {

    private static final double MAX_LONG = -73.7;
    private static final double MIN_LONG = -74.25;
    private static final double MIN_LAT = 40.5;
    private static final double MAX_LAT = 40.9;
    public static final double STEP_SIZE = 0.001;

    public static void main(String[] args) {
        try {
            Area boundery = new Area(
                    new Location(MAX_LAT, MAX_LONG),
                    new Location(MIN_LAT, MIN_LONG)
            );
            System.out.println("Building space-time cube");
            /*
            SpaceTimeCube cube = new SpaceTimeCube(boundery, new RelativeLocation(STEP_SIZE, STEP_SIZE),
                    LocalDateTime.of(2016, Month.JANUARY, 1, 0, 0),
                    LocalDateTime.of(2016, Month.FEBRUARY, 1, 0, 0),
                    Duration.ofHours(1));
            System.out.println("Loading file into space-time cube");
            SpaceTimeCube.loadFromFile(cube, new File("data/yellow_tripdata_2016-01.csv"));
            //*/
            //*
            SpaceTimeCube cube = SpaceTimeCube.loadFromFileParallel(new File("data/yellow_tripdata_2016-01.csv"), boundery, 
                    new RelativeLocation(STEP_SIZE, STEP_SIZE),
                    Duration.ofHours(1));
            System.out.println("Calculating statistic for cube");
            Statistic stat = new Statistic(cube);
            System.out.println(stat.getJson(stat.customStatistic(50)));
            //*/
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        //new Main().testDecomposedStatistic();
    }

    private void testDecomposedStatistic() {
        Area area = new Area(new Location(40.9, 74.25), new Location(40.5, 73.7));
        RelativeLocation relativeLocation = new RelativeLocation(0.001, 0.001);
        LocalDateTime startTime = LocalDateTime.of(2017, 3, 22, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2017, 3, 22, 23, 59);
        Duration timeStep = Duration.ofMinutes(60);
        decomposed.SpaceTimeCube cube = new decomposed.SpaceTimeCube(area, relativeLocation, timeStep);
        TimedRoute route = new TimedRoute(new Location(40.78, 74.0), new Location(40.78, 74.1), LocalDateTime.of(2017, 3, 22, 10, 5), LocalDateTime.of(2017, 3, 22, 11, 5));
        cube.addTimedRoute(route);
        route = new TimedRoute(new Location(40.78, 74.1), new Location(40.78, 7421), LocalDateTime.of(2017, 3, 22, 10, 5), LocalDateTime.of(2017, 3, 22, 13, 5));
        cube.addTimedRoute(route);
        Statistic statistic = new Statistic(cube);
        Map<SpaceTimeCube.SpaceTimeCell, Double> map = statistic.customStatistic(20);
        String json = statistic.getJson(map);
        System.out.println(json);
    }
}




