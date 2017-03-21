public class Route {

    public double startLong, startLat, endLong, endLat;
    public double tripDistance;
    public int startTime, endTime;

    public Route(double startLong, double startLat, double endLong, double endLat, double tripDistance, int startTime, int endTime) {
        this.startLong = startLong;
        this.startLat = startLat;
        this.endLong = endLong;
        this.endLat = endLat;
        this.tripDistance = tripDistance;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
