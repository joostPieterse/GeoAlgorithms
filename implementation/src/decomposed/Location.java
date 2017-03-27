
package decomposed;

import java.text.NumberFormat;

/**
 *
 * @author s132054
 */
public class Location{
    
    double latitude;
    double longitude;
    public static int PRECISION = 3;
    
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public Location addInterpolatedRelative(RelativeLocation rel, double interpolate) {
        return new Location(latitude + interpolate * rel.dLatitude, longitude + interpolate * rel.dLongitude);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Location.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Location other = (Location) obj;
        if (!this.similar(other)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return (int) (latitude * Math.pow(10, PRECISION)) + (int) (longitude * Math.pow(10, PRECISION)) * 180;
    }
    
    @Override
    public String toString() {
        return "(" + longitude + ", " + latitude + ")";
    }

    private boolean similar(Location other) {
        return Math.abs(this.latitude - other.latitude) + Math.abs(this.longitude - other.longitude) < 2 * PRECISION;
    }
}
