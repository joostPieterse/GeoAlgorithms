
package decomposed;

/**
 *
 * @author s132054
 */
public class Location{
    
    double latitude;
    double longitude;
    
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public Location addInterpolatedRelative(RelativeLocation rel, double interpolate) {
        return new Location(latitude + interpolate * rel.dLatitude, longitude + interpolate * rel.dLongitude);
    }
    
    @Override
    public String toString() {
        return "(" + longitude + ", " + latitude + ")";
    }
}
