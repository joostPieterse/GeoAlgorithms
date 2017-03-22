
package decomposed;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;

/**
 *
 * @author s132054
 */
public class Main {
    
    private static final double MAX_LONG = -73.7;
    private static final double MIN_LONG = -74.25;
    private static final double MIN_LAT = 40.5;
    private static final double MAX_LAT = 40.9;
    private static final double STEP_SIZE = 0.001;

    public static void main(String[] args) {
        try {
            Area boundery = new Area(
                    new Location(MAX_LAT, MAX_LONG),
                    new Location(MIN_LAT, MIN_LONG)
            );
            System.out.println("Building space-time cube");
            SpaceTimeCube cube = new SpaceTimeCube(boundery, new RelativeLocation(STEP_SIZE, STEP_SIZE), 
                    LocalDateTime.of(2016, Month.JANUARY, 1, 0, 0),  
                    LocalDateTime.of(2016, Month.FEBRUARY, 1, 0, 0),
                    Duration.ofHours(1));
            System.out.println("Loading file into space-time cube");
            SpaceTimeCube.loadFromFile(cube, new File("data/yellow_tripdata_2016-01.csv"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
}
