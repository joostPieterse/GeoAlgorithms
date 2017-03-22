
package decomposed;

import java.awt.geom.Line2D;
import java.util.Arrays;

/**
 *
 * @author s132054
 */
public class Route {

    Location start;
    Location end;

    public Route(Location start, Location end) {
        this.start = start;
        this.end = end;
    }
    
    public Location interpolateFraction(double fraction) {
        return start.addInterpolatedRelative(RelativeLocation.getRelativeLocation(start, end), fraction);
    }
    
    public boolean intersectsWithRoute(Route route) {
        return Line2D.linesIntersect(start.longitude, start.latitude, end.longitude, end.latitude, 
                route.start.longitude, route.start.latitude, route.end.longitude, route.end.latitude);
    }
    
    public boolean intersectsWithArea(Area area) {
        if (area.contains(start) || area.contains(end)) {
            //System.out.println(this + " intersects with " + area + "; point envelopped");
            return true;
        } else {
            return Arrays.stream(area.getBoundaryRoutes()).anyMatch((route)->{
                boolean result = this.intersectsWithRoute(route);
                //if (result) System.out.println(this + " intersects with " + area + "; boundary " + route);
                return result;
            });
        }
    }

    @Override
    public String toString() {
        return "{" + start + ", " + end + "}";
    }
}
