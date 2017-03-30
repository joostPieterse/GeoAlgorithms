
package decomposed;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author s132054
 */
public class TimedRoute extends Route{
    
    LocalDateTime startTime;
    LocalDateTime endTime;
    double tripDistance;

    public TimedRoute(Location start, Location end, double tripDistance, LocalDateTime startTime, LocalDateTime endTime) {
        super(start, end);
        this.startTime = startTime;
        this.endTime = endTime;
        this.tripDistance = tripDistance;
    }
    
    public TimedRoute(Location start, Location end, LocalDateTime startTime, LocalDateTime endTime) {
        super(start, end);
        this.startTime = startTime;
        this.endTime = endTime;
        this.tripDistance = getDistanceInMiles();
    }
    
    public Duration getPeriod() {
        return Duration.between(endTime, startTime);
    }
    
    public boolean overlapsTimeRange(LocalDateTime begin, LocalDateTime end) {
        return end.isAfter(startTime) && begin.isBefore(endTime);
    }
    
    public boolean timeContained(LocalDateTime time) {
        return (time.isAfter(startTime) && time.isBefore(endTime)) || time.isEqual(startTime) || time.isEqual(endTime);
    }
    
    public Location interpolateTime(LocalDateTime time, ChronoUnit precision) {
        if (!timeContained(time))
            throw new ContainmentException("Queried time " + time.format(SpaceTimeCube.dateTimeFormat) + " not contained in route time " 
                    + startTime.format(SpaceTimeCube.dateTimeFormat) + "~" + endTime.format(SpaceTimeCube.dateTimeFormat));
        double startToTime = startTime.until(time, precision);
        double timeToEnd = time.until(endTime, precision);
        double interpolation = startToTime / (startToTime + timeToEnd);
        //System.out.println("interpolation times: start=" + startToTime + " end=" + timeToEnd + " interpolation value=" + interpolation);
        return interpolateFraction(interpolation);
    }
    
    public TimedRoute subRouteInTimespan(LocalDateTime start, LocalDateTime end, ChronoUnit precision) {
        //System.out.println("Subroute of TimedRoute " + this + " in time interval " 
        //        + start.format(SpaceTimeCube.dateTimeFormat) + "~" + end.format(SpaceTimeCube.dateTimeFormat));
        if (!end.isAfter(start))
            throw new RangeException("Invalid time range given " 
                    + start.format(SpaceTimeCube.dateTimeFormat) + "~" + end.format(SpaceTimeCube.dateTimeFormat)
                    + " while handling TimedRoute in range " + startTime.format(SpaceTimeCube.dateTimeFormat) + "~" 
                    + endTime.format(SpaceTimeCube.dateTimeFormat));
        if (start.isBefore(this.startTime)) {
            start = this.startTime;
        }
        if (end.isAfter(this.endTime)) {
            end = this.endTime;
        }
        TimedRoute result = new TimedRoute(
                interpolateTime(start, precision),
                interpolateTime(end, precision),
                start,
                end
        );
        //System.out.println("Resulting route: " + result);
        return result;
    }
    
    public List<TimedRoute> subRoutesOnTimeDivision(Duration timestep, ChronoUnit precision) {
        ArrayList<TimedRoute> result = new ArrayList<>();
        if (this.tripDistance >= 2 * this.getDistanceInMiles()) {
            result.add(new TimedRoute(start, start, startTime, startTime));
            result.add(new TimedRoute(end, end, endTime, endTime));
        } else {
            int layer = 0;
            LocalDateTime searchStart = LocalTime.MIN.atDate(startTime.toLocalDate());
            while (!overlapsTimeRange(searchStart.plus(timestep.multipliedBy(layer)), searchStart.plus(timestep.multipliedBy(layer + 1)))) {
                layer++;
            }
            do {
                LocalDateTime qStart = searchStart.plus(timestep.multipliedBy(layer++));
                LocalDateTime qEnd = qStart.plus(timestep);
                result.add(subRouteInTimespan(qStart.isAfter(startTime) ? qStart : startTime, 
                        qEnd.isBefore(endTime) ? qEnd : endTime, precision));
            } while(overlapsTimeRange(searchStart.plus(timestep.multipliedBy(layer)), searchStart.plus(timestep.multipliedBy(layer + 1))));
        }
        return result;
    }
    
    public static TimedRoute parseLine(String line) throws NumberFormatException, java.time.format.DateTimeParseException, IllegalArgumentException {
        //System.out.println("Parsing line: " + line);
        String[] columns = line.split(",");
        //System.out.println("Important values: " + columns[1] + ", " + columns[2] + ", " + columns[4] + ", " + columns[5] + ", " + columns[6] + ", " + columns[9] + ", " + columns[10]);
        LocalDateTime startTime = LocalDateTime.parse(columns[1], SpaceTimeCube.dateTimeFormat);
        LocalDateTime endTime = LocalDateTime.parse(columns[2], SpaceTimeCube.dateTimeFormat);
        if (startTime.until(endTime, ChronoUnit.SECONDS) == 0) 
            throw new IllegalArgumentException("No time passes during route");
        Location startLocation = new Location(Double.parseDouble(columns[6]), Double.parseDouble(columns[5]));
        Location endLocation = new Location(Double.parseDouble(columns[10]), Double.parseDouble(columns[9]));
        if (startLocation.latitude == 0 || startLocation.longitude == 0 ||
                endLocation.latitude == 0 || endLocation.longitude == 0)
            throw new IllegalArgumentException("Invalid location found during parsing");
        double tripDistance = Double.parseDouble(columns[4]);
        //System.out.println("Created variables: startTime=" + startTime.format(SpaceTimeCube.dateTimeFormat) 
        //        + " endTime=" + endTime.format(SpaceTimeCube.dateTimeFormat)
        //        + " startLocation=" + startLocation
        //        + " endLocation=" + endLocation);
        TimedRoute result = new TimedRoute(startLocation, endLocation, tripDistance, startTime, endTime);
        return result;
    }
    
    public static Optional<TimedRoute> parseLineOptional(String line) {
        try {
            return Optional.of(parseLine(line));
        } catch (java.time.format.DateTimeParseException | IllegalArgumentException ex) {
            return Optional.empty();
        }
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
