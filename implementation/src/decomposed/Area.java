
package decomposed;

import java.awt.geom.Rectangle2D;

/**
 *
 * @author s132054
 */
public class Area {
    
    Location topLeftCorner; //maximal lat and long
    Location bottomRightCorner; //minimal lat and long

    public Area(Location topLeftCorner, Location bottomRightCorner) {
        if (bottomRightCorner.latitude > topLeftCorner.latitude || bottomRightCorner.longitude > topLeftCorner.longitude)
            throw new IllegalArgumentException("Invalid area given");
        this.topLeftCorner = topLeftCorner;
        this.bottomRightCorner = bottomRightCorner;
    }
    
    public Route[] getBoundaryRoutes() {
        Location bottomLeftCorner = new Location(topLeftCorner.latitude, bottomRightCorner.longitude);
        Location topRightCorner = new Location(bottomLeftCorner.latitude, topLeftCorner.longitude);
        return new Route[]{
            new Route(topLeftCorner, topRightCorner),
            new Route(bottomLeftCorner, bottomRightCorner),
            new Route(topLeftCorner, bottomLeftCorner),
            new Route(topRightCorner, bottomRightCorner)
        };
    }
    
    public Rectangle2D.Double getRectangle() {
        return new Rectangle2D.Double(bottomRightCorner.longitude, bottomRightCorner.latitude, getSize().dLongitude, getSize().dLatitude);
    }
    
    public boolean contains(Location point) {
        return bottomRightCorner.longitude <= point.longitude && point.longitude <= topLeftCorner.longitude
                && bottomRightCorner.latitude <= point.latitude && point.latitude <= topLeftCorner.latitude;
    }
    
    public RelativeLocation getSize() {
        return RelativeLocation.getRelativeLocation(bottomRightCorner, topLeftCorner);
    }
}
