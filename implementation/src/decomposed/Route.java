
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
    
    public double getDistanceInMiles() {
        /* 
        a = sin^2(dlat/2) + cos lat1 * cos lat2 * sin^2(dlng/2)
        c = 2 ⋅ atan2( √a, √(1−a) )
        d = R ⋅ c
        */
        double R = 3959.0;
        double latStart = Math.toRadians(start.latitude);
        double latEnd = Math.toRadians(end.latitude);
        double latD = Math.toRadians(end.latitude - start.latitude);
        double lngD = Math.toRadians(end.longitude - start.longitude);
        
        double a = Math.pow(Math.sin(latD/2.0), 2.0) + Math.cos(latStart) * Math.cos(latEnd) * Math.pow(Math.sin(lngD/2.0), 2.0);
        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return d;
    }

    @Override
    public String toString() {
        return "{" + start + ", " + end + "}";
    }
}
