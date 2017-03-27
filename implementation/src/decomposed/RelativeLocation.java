
package decomposed;

/**
 *
 * @author s132054
 */
public class RelativeLocation {

    double dLatitude;
    double dLongitude;

    public RelativeLocation(double dLatitude, double dLongitude) {
        this.dLatitude = dLatitude;
        this.dLongitude = dLongitude;
    }
    
    public static RelativeLocation getRelativeLocation(Location a, Location b) {
        return new RelativeLocation(b.latitude - a.latitude, b.longitude - a.longitude);
    }
    
    public RelativeLocation getLat() {
        return new RelativeLocation(dLatitude, 0);
    }
    
    public RelativeLocation getLng() {
        return new RelativeLocation(0, dLongitude);
    }
    
    public RelativeLocation scaleLat(double scale) {
        return new RelativeLocation(dLatitude * scale, dLongitude);
    }
    
    public RelativeLocation scaleLng(double scale) {
        return new RelativeLocation(dLatitude, dLongitude * scale);
    }
    
    public double size() {
        return Math.sqrt(Math.pow(dLatitude, 2) + Math.pow(dLongitude, 2));
    }
}
