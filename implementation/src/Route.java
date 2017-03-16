public class Route {

    public double startLong, startLat, endLong, endLat;
    public double tripDistance;

    public Route(double startLong, double startLat, double endLong, double endLat, double tripDistance) {
        this.startLong = startLong;
        this.startLat = startLat;
        this.endLong = endLong;
        this.endLat = endLat;
        this.tripDistance = tripDistance;
    }
}
