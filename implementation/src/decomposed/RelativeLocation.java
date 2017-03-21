
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
}
