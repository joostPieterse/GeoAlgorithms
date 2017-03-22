
package decomposed;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author s132054
 */
public class TimedRoute extends Route{
    
    LocalDateTime startTime;
    LocalDateTime endTime;

    public TimedRoute(Location start, Location end, LocalDateTime startTime, LocalDateTime endTime) {
        super(start, end);
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public Duration getPeriod() {
        return Duration.between(endTime, startTime);
    }
    
    public boolean timeContained(LocalDateTime time) {
        return (time.isAfter(startTime) && time.isBefore(endTime)) || time.isEqual(startTime) || time.isEqual(endTime);
    }
    
    public Location interpolateTime(LocalDateTime time, ChronoUnit precision) {
        if (!timeContained(time))
            throw new ContainmentException("Queried time not contained in route time");
        double startToTime = startTime.until(time, precision);
        double timeToEnd = time.until(endTime, precision);
        double interpolation = startToTime / (startToTime + timeToEnd);
        System.out.println("interpolation times: start=" + startToTime + " end=" + timeToEnd + " interpolation value=" + interpolation);
        return interpolateFraction(interpolation);
    }
    
    public Route subRouteInTimespan(LocalDateTime start, LocalDateTime end, ChronoUnit precision) {
        System.out.println("Subroute of TimedRoute " + this + " in time interval " 
                + start.format(SpaceTimeCube.dateTimeFormat) + "~" + end.format(SpaceTimeCube.dateTimeFormat));
        if (!end.isAfter(start))
            throw new RangeException("Invalid time range given");
        if (start.isBefore(this.startTime)) {
            start = this.startTime;
        }
        if (end.isAfter(this.endTime)) {
            end = this.endTime;
        }
        Route result = new Route(
                interpolateTime(start, precision),
                interpolateTime(end, precision)
        );
        System.out.println("Resulting route: " + result);
        return result;
    }
    
    public static TimedRoute parseLine(String line) throws NumberFormatException, java.time.format.DateTimeParseException, IllegalArgumentException {
        System.out.println("Parsing line: " + line);
        String[] columns = line.split(",");
        System.out.println("Important values: " + columns[1] + ", " + columns[2] + ", " + columns[5] + ", " + columns[6] + ", " + columns[9] + ", " + columns[10]);
        LocalDateTime startTime = LocalDateTime.parse(columns[1], SpaceTimeCube.dateTimeFormat);
        LocalDateTime endTime = LocalDateTime.parse(columns[2], SpaceTimeCube.dateTimeFormat);
        if (startTime.until(endTime, ChronoUnit.SECONDS) == 0) 
            throw new IllegalArgumentException("No time passes during route");
        Location startLocation = new Location(Double.parseDouble(columns[6]), Double.parseDouble(columns[5]));
        Location endLocation = new Location(Double.parseDouble(columns[10]), Double.parseDouble(columns[9]));
        if (startLocation.latitude == 0 || startLocation.longitude == 0 ||
                endLocation.latitude == 0 || endLocation.longitude == 0)
            throw new IllegalArgumentException("Invalid location found during parsing");
        System.out.println("Created variables: startTime=" + startTime.format(SpaceTimeCube.dateTimeFormat) 
                + " endTime=" + endTime.format(SpaceTimeCube.dateTimeFormat)
                + " startLocation=" + startLocation
                + " endLocation=" + endLocation);
        return new TimedRoute(startLocation, endLocation, startTime, endTime);
    }
    
    
    class RangeException extends RuntimeException {

        public RangeException(String message) {
            super(message);
        }
    }

    @Override
    public String toString() {
        return "{" + startTime.format(SpaceTimeCube.dateTimeFormat) + ": " + start 
                + ", " + endTime.format(SpaceTimeCube.dateTimeFormat) + ": " + end + "}";
    }
    
}
