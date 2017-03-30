
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
            throw new IllegalArgumentException("Invalid area given: " + bottomRightCorner + " | " + topLeftCorner);
        this.topLeftCorner = topLeftCorner;
        this.bottomRightCorner = bottomRightCorner;
    }
    
    public Route[] getBoundaryRoutes() {
        Location topRightCorner = new Location(topLeftCorner.latitude, bottomRightCorner.longitude);
        Location bottomLeftCorner = new Location(bottomRightCorner.latitude, topLeftCorner.longitude);
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
    
    public boolean similar(Area other) {
        return (RelativeLocation.getRelativeLocation(topLeftCorner, other.topLeftCorner).size() < getSize().size() / 2.0) &&
                (RelativeLocation.getRelativeLocation(bottomRightCorner, other.bottomRightCorner).size() < getSize().size() / 2.0);
    }
    
    public RelativeLocation getSize() {
        return RelativeLocation.getRelativeLocation(bottomRightCorner, topLeftCorner);
    }
    
    @Override
    public String toString() {
        return "(" + bottomRightCorner + "; " + topLeftCorner + ")";
    }
}
