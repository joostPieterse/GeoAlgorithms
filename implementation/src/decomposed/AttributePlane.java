
package decomposed;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author s132054
 */
public class AttributePlane {
    Area area;
    RelativeLocation steps;
    private int[][] plane;

    public AttributePlane(Area area, RelativeLocation steps) {
        this.area = area;
        this.steps = steps;
        RelativeLocation size = area.getSize();
        this.plane = new int[(int) Math.ceil(size.dLatitude / steps.dLatitude)][(int) Math.ceil(size.dLongitude / steps.dLongitude)];
    }
    
    private Cell getCellFromLocation(Location location) {
        if (!area.contains(location))
            throw new ContainmentException("Location " + location + " not contained in area bounds " + area);
        RelativeLocation dBottomRight = RelativeLocation.getRelativeLocation(area.bottomRightCorner, location);
        int lat = (int) Math.floor(dBottomRight.dLatitude / steps.dLatitude);
        int lng = (int) Math.floor(dBottomRight.dLongitude / steps.dLongitude);
        return new Cell(lat, lng);
    }
    
    private Area getAreaFromCell(Cell cell) {
        double bottomRightLatitude = area.bottomRightCorner.latitude + steps.dLatitude * cell.lat;
        double bottomRightLongitude = area.bottomRightCorner.longitude + steps.dLongitude * cell.lng;
        Location bottomRightCorner = new Location(bottomRightLatitude, bottomRightLongitude);
        Location topLeftCorner = bottomRightCorner.addInterpolatedRelative(steps, 1.0);
        return new Area(topLeftCorner, bottomRightCorner);
    }
    
    public int getAttributeAtLocation(Location location) {
        Cell c = getCellFromLocation(location);
        return plane[c.lat][c.lng];
    }
    
    public void setAtributeAtLocation(Location location, int value) {
        Cell c = getCellFromLocation(location);
        plane[c.lat][c.lng] = value;
    }
    
    public void incrementContainingCells(Route route) {
        Cell startCell = getCellFromLocation(route.start);
        //System.out.println("Found cell " + startCell + " with area " + getAreaFromCell(startCell) + " for route " + route);
        plane[startCell.lat][startCell.lng]++;
        ArrayList<Cell> passed = new ArrayList<>();
        passed.add(startCell);
        incrementNeighboursRecursive(route, startCell, passed, 0);
    }
    
    public int[][] getPlane() {
        return plane;
    }
    
    public void incrementNeighboursRecursive(Route route, Cell cell, ArrayList<Cell> passed, int iteration) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                final Cell checkCell = cell.delta(i, j);
                if (!passed.stream().anyMatch((c)->c.equals(checkCell)) 
                        && 0 <= checkCell.lat && checkCell.lat < plane.length && 0 <= checkCell.lng && checkCell.lng < plane[0].length) {
                    if (route.intersectsWithArea(getAreaFromCell(checkCell))) {
                        //System.out.println("Checking cell " + checkCell + " with area " + getAreaFromCell(checkCell) + "  in iteration " + iteration);
                        plane[checkCell.lat][checkCell.lng]++;
                        passed.add(checkCell);
                        incrementNeighboursRecursive(route, checkCell, passed, iteration + 1);
                    }
                }
            }
        }
    }

    private class Cell {
        int lat, lng;

        public Cell(int lat, int lng) {
            this.lat = lat;
            this.lng = lng;
        }
        
        public Cell delta(int i, int j) {
            return new Cell(lat + i, lng + j);
        }

        public boolean equals(Cell cell) {
            return lat == cell.lat && lng == cell.lng;
        }

        @Override
        public String toString() {
            return "<"+lat+","+lng+">";
        }
    }
}
