
package decomposed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author s132054
 */
public class SpaceTimeCube implements Iterable<SpaceTimeCube.SpaceTimeCell> {
    
    static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    Area area;
    Duration timeStep;
    
    AttributePlane[] planes;

    public SpaceTimeCube(Area area, RelativeLocation steps, Duration timeStep) {
        this.area = area;
        this.timeStep = timeStep;
        
        int height = 0;
        Duration checkTime = Duration.ofDays(1);
        while (timeStep.multipliedBy(height).compareTo(checkTime) < 0) {
            height++;
        }
        System.out.println("Height of cube: " + height);
        planes = new AttributePlane[height];
        for (int i = 0; i < height; i++) {
            planes[i] = new AttributePlane(area, steps);
        }
    }
    
    public SpaceTimeCube(Area area, Duration timeSteps, AttributePlane[] planes) {
        this.area = area;
        this.timeStep = timeSteps;
        
        this.planes = planes;
    }
    
    public AttributePlane getPlaneForTime(LocalTime time) {
        return planes[getPlaneIDForTime(time)];
    }
    
    private int getPlaneIDForTime(LocalTime time) {
        return getPlaneIDForTime(time, timeStep);
    }
    
    public static int getPlaneIDForTime(LocalTime time, Duration timeStep) {
        int requiredPlane = 0;
        LocalTime checkTime = LocalTime.MIN;
        while (Duration.between(
                checkTime.plus(timeStep.multipliedBy(requiredPlane)), 
                time).compareTo(timeStep) > 0) {
            requiredPlane++;
        }
        //System.out.println("Plane=" + requiredPlane);
        return requiredPlane;
    }
    
    private LocalTime getStartTimeForPlaneID(int plane) {
        return getStartTimeForPlaneID(plane, timeStep);
    }
    
    public static LocalTime getStartTimeForPlaneID(int plane, Duration timeStep) {
        return LocalTime.MIN.plus(timeStep.multipliedBy(plane));
    }
    
    public void addTimedRoute(TimedRoute route) {
        if (area.contains(route.start) && area.contains(route.end)) {
            int i = 0;
            if (route.tripDistance < 2 * route.getDistanceInMiles()) {
                LocalDateTime startTime = getStartTimeForPlaneID(getPlaneIDForTime(route.startTime.toLocalTime())).atDate(route.startTime.toLocalDate());
                System.out.print("Adding");
                while (startTime.plus(timeStep.multipliedBy(i)).isBefore(route.endTime)) {
                    System.out.print(startTime.plus(timeStep.multipliedBy(i)) + "|");
                    getPlaneForTime(startTime.plus(timeStep.multipliedBy(i)).toLocalTime()).incrementContainingCells(
                        route.subRouteInTimespan(startTime.plus(timeStep.multipliedBy(i)), startTime.plus(timeStep.multipliedBy(i + 1)), ChronoUnit.SECONDS)
                    );
                }
                System.out.println();
            } else {
                getPlaneForTime(route.startTime.toLocalTime()).incrAttributeAtLocation(route.start, 1);
                getPlaneForTime(route.endTime.toLocalTime()).incrAttributeAtLocation(route.end, 1);
            }
        }
    }
    
    public List<SpaceTimeCube.SpaceTimeCell> getNeighbours(SpaceTimeCell cell) {
        ArrayList<SpaceTimeCell> result = new ArrayList<>();
        int startPlane = cell.getTimeLocation();
        for (int i = -1; i <= 1; i++) {
            int plane = startPlane+i;
            int queryPlane = plane < 0 || plane >= planes.length ? startPlane : plane;
            result.addAll(planes[queryPlane].getNeighbours(cell.cell).stream()
            .map(c->{ return new SpaceTimeCell(c, plane); })
            .collect(Collectors.toList()));
        }
        return result;
    }
    
    public static SpaceTimeCube loadFromFile(SpaceTimeCube cube, File file) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        final IntContainer linenr = new IntContainer(0);
        final IntContainer errnr = new IntContainer(0);
        reader.lines().forEach((line)->{
            try {
                TimedRoute route = TimedRoute.parseLine(line);
                cube.addTimedRoute(route);
            } catch (java.time.format.DateTimeParseException | IllegalArgumentException ex) {
                if (errnr.increment() % 1000 == 0) System.out.print("-");
            } finally {
                if (linenr.increment() % 1000 == 0) System.out.print(".");
                if (linenr.get() % 100000 == 0) System.out.println(linenr.get()+":"+errnr.get());
            }
        });
        System.out.println("Lines read: " + linenr.get());
        System.out.println("Lines skipped: " + errnr.get());
        return cube;
    }
    
    public static SpaceTimeCube loadFromFileParallel(File file, Area area, RelativeLocation steps, Duration timeSteps)
            throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        CompoundEveryXListener reportProgress = new CompoundEveryXListener()
                .add("Lines", new EveryXListener(10000, (i)->System.out.print(".")))
                .add("Reports", new EveryXListener(100000, (i)->System.out.println(i*100000)));
        EveryXListener reportError = new EveryXListener(10000, (i)->System.out.print("-"));
        Map <LocalTime, AttributePlane> planes = reader.lines()
            .parallel()
            .map(new Function<String, Optional<TimedRoute>>() {

                int c;
                long time = System.currentTimeMillis();
                
                @Override
                public Optional<TimedRoute> apply(String t) {
                    if (++c % 10000 == 0) {
                        System.out.println(c + ": " + (System.currentTimeMillis() - time)/1000);
                    }
                    return TimedRoute.parseLineOptional(t);
                }
            })
            .filter(i->i.isPresent() && area.contains(i.get().start) && area.contains(i.get().end))
            .map(tr->tr.get().subRoutesOnTimeDivision(timeSteps, ChronoUnit.SECONDS))
            .flatMap(trs->trs.stream().parallel())
            .collect(Collectors.groupingByConcurrent((TimedRoute tr)->
                    getStartTimeForPlaneID(getPlaneIDForTime(tr.startTime.toLocalTime(), timeSteps), timeSteps),
                    new AttributePlaneCollector(area, steps)));
            System.out.println("Lines: " + reportProgress.report().get("Lines"));
            System.out.println("Skips: " + reportError.report());
            return fromMapOfPlanes(planes, area, timeSteps);
    }
    
    public static SpaceTimeCube fromMapOfPlanes(Map<LocalTime, AttributePlane> mapOfPlanes, 
            Area area, Duration timeStep) {
        AttributePlane[] planes = new AttributePlane[mapOfPlanes.values().size()];
        IntStream.range(0, planes.length).forEach((i) -> {
            planes[i] = mapOfPlanes.get(LocalTime.MIN.plus(timeStep.multipliedBy(i)));
        });
        return new SpaceTimeCube(area, timeStep, planes);
    }
    
    /**
     * @deprecated 
     */
    public static SpaceTimeCube fromMapOfPlanes(Map<LocalTime, AttributePlane> mapOfPlanes) {
        AttributePlane planeExample = mapOfPlanes.values().iterator().next();
        Area area = planeExample.area;
        RelativeLocation steps = planeExample.steps;
        LocalTime startTime = mapOfPlanes.keySet().stream()
                .min((a, b) -> a.compareTo(b)).orElseThrow(IllegalArgumentException::new);
        Duration timeStep = Duration.between(startTime, mapOfPlanes.keySet().stream()
                .filter(a->!a.equals(startTime))
                .min((a, b)->a.compareTo(b))
                .orElse(startTime));
        LocalTime endTime = mapOfPlanes.keySet().stream()
                .max((a, b)->a.compareTo(b)).get().plus(timeStep);
        AttributePlane[] planes = new AttributePlane[mapOfPlanes.values().size()];
        IntStream.range(0, planes.length).forEach((i) -> {
            planes[i] = mapOfPlanes.get(startTime.plus(timeStep.multipliedBy(i)));
        });
        return new SpaceTimeCube(area, timeStep, planes);
    }

    @Override
    public Iterator<SpaceTimeCell> iterator() {
        return new SpaceTimeCubeIterator();
    }
    
    class SpaceTimeCubeIterator implements Iterator<SpaceTimeCell> {

        int timeLocation = 0;
        Iterator<AttributePlane.PlaneCell> it = SpaceTimeCube.this.planes[0].iterator();
        
        @Override
        public boolean hasNext() {
            return !(timeLocation == SpaceTimeCube.this.planes.length - 1 && !it.hasNext());
        }

        @Override
        public SpaceTimeCell next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (!it.hasNext()) {
                it = SpaceTimeCube.this.planes[++timeLocation].iterator();
            }
            AttributePlane.PlaneCell cell = it.next();
            return new SpaceTimeCell(cell, timeLocation);
        }
        
    }
    
    class SpaceTimeCell {

        private int timeLocation;
        AttributePlane.PlaneCell cell;

        SpaceTimeCell(AttributePlane.PlaneCell cell, int timeLocation) {
            this.cell = cell;
            this.timeLocation = timeLocation;
        }

        @Override
        public int hashCode() {
            return cell.hashCode() + timeLocation * 180 * 360;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SpaceTimeCell other = (SpaceTimeCell) obj;
            if (this.timeLocation != other.timeLocation) {
                return false;
            }
            if (!Objects.equals(this.cell, other.cell)) {
                return false;
            }
            return true;
        }
        
        /**
         * @return the timeLocation
         */
        public int getTimeLocation() {
            return timeLocation;
        }

        @Override
        public String toString() {
            return timeLocation + ": " + cell.toString();
        }

    }
    
    private static class IntContainer {
        int i;

        public IntContainer(int i) {
            this.i = i;
        }
        
        int increment() {
            return ++i;
        }
        
        int get() {
            return i;
        }
    }
}
