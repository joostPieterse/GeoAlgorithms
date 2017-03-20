import static java.lang.Math.abs;
import static jdk.nashorn.internal.objects.NativeMath.round;

public class SpaceSquare {
    private int[][] values;
    //in degrees
    private static final double MIN_LONG = 73.7;
    private static final double MAX_LONG = 74.25;
    private static final double MIN_LAT = 40.5;
    private static final double MAX_LAT = 40.9;
    private static final double STEP_SIZE = 0.001;

    public SpaceSquare() {
        int longitude = (int) ((MAX_LONG - MIN_LONG) / STEP_SIZE);
        int latitude = (int) ((MAX_LAT - MIN_LAT) / STEP_SIZE);
        this.values = new int[longitude][latitude];
    }

    public void addRoute(Route route) {
        double difLat = abs(route.endLat - route.startLat);
        double difLong = abs(route.endLong - route.startLong);
        double slope = difLat / difLong;
        System.out.println(slope);
        for (double i = 0; i <= difLong; i += STEP_SIZE) {
            // Defining which x coordinate we are at now
            double xLong = route.startLong + i;

            double minLat = route.startLat + (i * slope);
            // Rounding the minLat such that it fits the step_size.
            minLat = ((double) ((int) (minLat / STEP_SIZE))) * STEP_SIZE;
            double maxLat = route.startLat + ((i + STEP_SIZE) * slope);

            for (double j = minLat; j <= maxLat; j += STEP_SIZE) {
                System.out.println("I appended shit to M[" +
                        String.format("$%,.3f", xLong) + ", " + String.format("$%,.3f", j) + "]");
                increaseValue(j, xLong);
            }
        }
    }

    private void increaseValue(double latitude, double longitude) {
        int latSteps = (int) ((latitude - MIN_LAT) / STEP_SIZE);
        int longSteps = (int) ((longitude - MIN_LONG) / STEP_SIZE);
        values[longSteps][latSteps]++;
    }

    public int[][] getValues() {
        return values;
    }

    public void printValues() {
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                if(values[i][j]>0){
                    System.out.println("["+i+", "+j+"] "+values[i][j]);
                }
            }
        }
    }
}
