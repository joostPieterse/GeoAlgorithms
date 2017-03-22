
package decomposed;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 *
 * @author s132054
 */
public class SpaceTimeCube {
    
    LocalDateTime startTime;
    LocalDateTime endTime;
    Duration timeStep;
    
    AttributePlane[] planes;

    public SpaceTimeCube(Area area, RelativeLocation steps, LocalDateTime startTime, LocalDateTime endTime, Duration timeStep) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeStep = timeStep;
        
        int height = 0;
        LocalDateTime checkTime = startTime;
        while (startTime.isBefore(endTime)) {
            checkTime = checkTime.plus(timeStep);
            height++;
        }
        planes = new AttributePlane[height];
        for (int i = 0; i < height; i++) {
            planes[i] = new AttributePlane(area, steps);
        }
    }
    
    public AttributePlane getPlaneForTime(LocalDateTime time) {
        return planes[getPlaneIDForTime(time)];
    }
    
    private int getPlaneIDForTime(LocalDateTime time) {
        if (time.isBefore(startTime) || time.isAfter(endTime)) 
            throw new ContainmentException("Time is not contained in Spacetime cube");
        int requiredPlane = 0;
        LocalDateTime checkTime = startTime.plus(timeStep);
        while (checkTime.isBefore(time)) {
            checkTime = checkTime.plus(timeStep);
            requiredPlane++;
        }
        return requiredPlane;
    }
    
    private LocalDateTime getStartTimeForPlaneID(int plane) {
        return startTime.plus(timeStep.multipliedBy(plane));
    }
    
    public void addTimedRoute(TimedRoute route) {
        int startPlaneID = getPlaneIDForTime(route.startTime);
        int endPlaneID = getPlaneIDForTime(route.endTime);
        for (int plane = startPlaneID; plane <= endPlaneID; plane++) {
            LocalDateTime planeStartTime = getStartTimeForPlaneID(plane);
            planes[plane].incrementContainingCells(route.subRouteInTimespan(planeStartTime, planeStartTime.plus(timeStep), ChronoUnit.SECONDS));
        }
    }
    
    public static SpaceTimeCube loadFromFile(File file) {
        //TODO implement csv parser for line->TimedRoute conversion (LocalDateTime)
        return null;
    }
}
