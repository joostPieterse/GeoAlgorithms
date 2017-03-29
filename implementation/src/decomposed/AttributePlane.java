
package decomposed;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 *
 * @author s132054
 */
public class AttributePlane implements Iterable<AttributePlane.PlaneCell> {
    Area area;
    RelativeLocation steps;
    private int[][] plane;

    public AttributePlane(Area area, RelativeLocation steps) {
        this.area = area;
        this.steps = steps;
        int lat = 0, lng = 0;
        while (area.bottomRightCorner.latitude + steps.dLatitude * lat < area.topLeftCorner.latitude) {
            lat++;
        }
        while (area.bottomRightCorner.longitude + steps.dLongitude * lng < area.topLeftCorner.longitude) {
            lng++;
        }
        this.plane = new int[lat][lng];
    }
    
    public int getSize() {
        return plane.length * plane[0].length;
    }
    
    public PlaneCell getCellFromLocation(Location location) {
        if (!area.contains(location))
            throw new ContainmentException("Location " + location + " not contained in area bounds " + area);
        Location check = area.bottomRightCorner;
        int lat = 0, lng = 0;
        while (check.latitude < location.latitude) {
            check = check.addInterpolatedRelative(steps.getLat(), 1);
            lat++;
        }
        while (check.longitude < location.longitude) {
            check = check.addInterpolatedRelative(steps.getLng(), 1);
            lng++;
        }
        return new PlaneCell(lat - 1, lng - 1);
    }
    
    private Area getCellAreaForLocation(Location location) {
        Location check = area.bottomRightCorner;
        while (check.latitude <= location.latitude) {
            check = check.addInterpolatedRelative(steps.getLat(), 1);
        }
        while (check.longitude <= location.longitude) {
            check = check.addInterpolatedRelative(steps.getLng(), 1);
        }
        return new Area(check, check.addInterpolatedRelative(steps, -1));
    }
    
    public RelativeLocation getPlaneDimensions() {
        return area.getSize();
    }
    
    public int getAttributeAtLocation(Location location) {
        return getCellFromLocation(location).getValue();
    }
    
    public PlaneCell incrAttributeAtLocation(Location location, int value) {
        PlaneCell cell = getCellFromLocation(location);
        cell.incrValue(value);
        return cell;
    }
    
    public void setAtributeAtLocation(Location location, int value) {
        getCellFromLocation(location).setValue(value);
    }
    
    public void incrementContainingCells(Route route) {
        PlaneCell startCell = incrAttributeAtLocation(route.start, 1);
        ArrayList<PlaneCell> passed = new ArrayList<>();
        passed.add(startCell);
        incrementNeighboursRecursive(route, startCell, passed, 0);
    }
    
    @Override
    public Iterator<PlaneCell> iterator() {
        return new PlaneIterator();
    }
    
    public List<PlaneCell> getNeighbours(PlaneCell c) {
        
        ArrayList<PlaneCell> cells = new ArrayList<>();
        for (int lat = -1; lat <= 1; lat++) {
            for (int lng = -1; lng <= 1; lng++) {
                cells.add(new PlaneCell(c.lat + lat, c.lng + lng));
            }
        }
        return cells;
    }
    
    public void incrementNeighboursRecursive(Route route, PlaneCell cell, ArrayList<PlaneCell> passed, int iteration) {
        getNeighbours(cell).forEach(c->{
            if (route.intersectsWithArea(c.getArea()) //route intersects cell
                    && !passed.stream().anyMatch(pc->pc.getArea().similar(cell.getArea())) //cell has not been passed yet
                    && area.contains(c.getCenter())) { //cell is inside plane
                PlaneCell ic = incrAttributeAtLocation(c.getCenter(), 1);
                passed.add(ic);
                incrementNeighboursRecursive(route, ic, passed, iteration + 1);
            }
        });
    }
    
    public AttributePlane createPlaneFromMapping(Function<PlaneCell, Integer> mapper) {
        AttributePlane newPlane = new AttributePlane(area, steps);
        for (PlaneCell cell : this) {
            newPlane.setAtributeAtLocation(cell.getCenter(), mapper.apply(cell));
        }
        return newPlane;
    }
    
    class PlaneIterator implements Iterator<PlaneCell> {
        
        int lat = 0, lng = 0;
        
        @Override
        public boolean hasNext() {
            return new PlaneCell(lat, lng).inside();
        }

        @Override
        public PlaneCell next() {
            if (!hasNext()) {
                throw new NoSuchElementException("End of plane reached");
            }
            int rLat = lat, rLng = lng;
            PlaneCell next = new PlaneCell(++lat, lng);
            if (!next.inside()) {
                lat = 0;
                lng++;
            }
            
            return new PlaneCell(rLat, rLng);
        }
    }
    
    class PlaneCell {
        
        protected final int lat, lng;

        public PlaneCell(int lat, int lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public boolean equals(PlaneCell cell) {
            return this.lat == cell.lat && this.lng == cell.lng;
        }

        public Area getArea() {
            Location bottomright = area.bottomRightCorner
                    .addInterpolatedRelative(steps.scaleLat(lat+1).scaleLng(lng+1), 1);
            Location topleft = bottomright.addInterpolatedRelative(steps, 1);
            return new Area(topleft, bottomright);
        }

        private boolean inside() {
            return lat >= 0 && lat < plane.length && lng >= 0 && lng < plane[0].length;
        }
        
        public int getValue() {
            if (inside()) {
                return plane[lat][lng];
            } else {
                return 0;
            }
        }

        public void setValue(int i) {
            if (inside()) {
                plane[lat][lng] = i;
            } else {
                throw new NullPointerException("Element not Available");
            }
        }

        public int incrValue(int i) {
            setValue(getValue() + i);
            return getValue();
        }

        public Location getCenter() {
            return getArea().bottomRightCorner.addInterpolatedRelative(getArea().getSize(), 0.5);
        }

        @Override
        public String toString() {
            return "<" + getArea() + ": " + getValue() + ">";
        }

        @Override
        public int hashCode() {
            return getCenter().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PlaneCell other = (PlaneCell) obj;
            if (this.lat != other.lat) {
                return false;
            }
            if (this.lng != other.lng) {
                return false;
            }
            return true;
        }
    }
}