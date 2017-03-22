
package decomposed;

import java.time.Duration;
import java.time.LocalDateTime;
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
        return interpolateFraction(interpolation);
    }
    
    public Route subRouteInTimespan(LocalDateTime start, LocalDateTime end, ChronoUnit precision) {
        if (!timeContained(start) || !timeContained(end))
            throw new ContainmentException("Queried time range not contained in route time");
        if (!end.isAfter(start))
            throw new RangeException("Invalid time range given");
        return new Route(
                interpolateTime(start, precision),
                interpolateTime(end, precision)
        );
    }
    
    
    class RangeException extends RuntimeException {

        public RangeException(String message) {
            super(message);
        }
    }
}
