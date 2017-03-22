
package decomposed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author s132054
 */
public class SpaceTimeCube {
    
    static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    Area area;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Duration timeStep;
    
    AttributePlane[] planes;

    public SpaceTimeCube(Area area, RelativeLocation steps, LocalDateTime startTime, LocalDateTime endTime, Duration timeStep) {
        this.area = area;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeStep = timeStep;
        
        int height = 0;
        System.out.println("Constructing space-time cube from " + startTime.format(dateTimeFormat)
            + " to " + endTime.format(dateTimeFormat));
        LocalDateTime checkTime = startTime;
        while (checkTime.isBefore(endTime)) {
            checkTime = checkTime.plus(timeStep);
            height++;
        }
        System.out.println("Height of cube: " + height);
        planes = new AttributePlane[height];
        for (int i = 0; i < height; i++) {
            planes[i] = new AttributePlane(area, steps);
        }
    }
    
    public AttributePlane getPlaneForTime(LocalDateTime time) {
        return planes[getPlaneIDForTime(time)];
    }
    
    private int getPlaneIDForTime(LocalDateTime time) {
        //System.out.println("Getting plane for time " + time.format(dateTimeFormat));
        if (time.isBefore(startTime) || time.isAfter(endTime)) 
            throw new ContainmentException("Time is not contained in Spacetime cube");
        int requiredPlane = 0;
        LocalDateTime checkTime = startTime.plus(timeStep);
        while (checkTime.isBefore(time)) {
            checkTime = checkTime.plus(timeStep);
            requiredPlane++;
        }
        //System.out.println("Plane=" + requiredPlane);
        return requiredPlane;
    }
    
    private LocalDateTime getStartTimeForPlaneID(int plane) {
        return startTime.plus(timeStep.multipliedBy(plane));
    }
    
    public void addTimedRoute(TimedRoute route) {
        if (area.contains(route.start) && area.contains(route.end)) {
            int startPlaneID = getPlaneIDForTime(route.startTime);
            int endPlaneID = getPlaneIDForTime(route.endTime);
            //System.out.println("TimedRoute crosses " + (endPlaneID - startPlaneID) + " planes");
            for (int plane = startPlaneID; plane <= endPlaneID; plane++) {
                LocalDateTime planeStartTime = getStartTimeForPlaneID(plane);
                planes[plane].incrementContainingCells(route.subRouteInTimespan(planeStartTime, planeStartTime.plus(timeStep), ChronoUnit.SECONDS));
            }
        }
    }
    
    public static SpaceTimeCube loadFromFile(SpaceTimeCube cube, File file) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int linenr = 0;
        reader.lines().forEach((line)->{
            try {
                TimedRoute route = TimedRoute.parseLine(line);
                if ((cube.startTime.isEqual(route.startTime) || cube.startTime.isBefore(route.startTime)) && 
                    (cube.endTime.isEqual(route.endTime) || cube.endTime.isAfter(route.endTime))) {
                    cube.addTimedRoute(route);
                }
            } catch (java.time.format.DateTimeParseException | IllegalArgumentException ex) {
                System.out.println("skipping unparsable line: " + ex.getMessage());
            } finally {
                linenr++;
            }
        });
        return null;
    }
}
