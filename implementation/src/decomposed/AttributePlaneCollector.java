
package decomposed;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 *
 * @author s132054
 */

public class AttributePlaneCollector implements Collector<TimedRoute, AttributePlane, AttributePlane> {

    Area area;
    RelativeLocation steps;

    public AttributePlaneCollector(Area area, RelativeLocation steps) {
        this.area = area;
        this.steps = steps;
    }

    @Override
    public Supplier<AttributePlane> supplier() {
        return ()->{
            return new AttributePlane(area, steps);
        };
    }

    @Override
    public BiConsumer<AttributePlane, TimedRoute> accumulator() {
        return (plane, route) -> {
            plane.incrementContainingCells(route);
        };
    }

    @Override
    public BinaryOperator<AttributePlane> combiner() {
        return (a, b) -> {
            for (AttributePlane.PlaneCell cell : b) {
                a.incrAttributeAtLocation(cell.getCenter(), b.getAttributeAtLocation(cell.getCenter()));
            }
            return a;
        };
    }

    @Override
    public Function<AttributePlane, AttributePlane> finisher() {
        return plane -> plane;
    }

    @Override
    public Set<Collector.Characteristics> characteristics() {
        return EnumSet.of(Collector.Characteristics.UNORDERED, Collector.Characteristics.CONCURRENT);
    }
} 