import static java.lang.Math.abs;

public class SpaceTimeCube {

    private int[][][] values;
    //in degrees
    private static final double MIN_LONG = 73.7;
    private static final double MAX_LONG = 74.25;
    private static final double MIN_LAT = 40.5;
    private static final double MAX_LAT = 40.9;
    private static final double STEP_SIZE = 0.001;

    //in minutes
    private static final int TIME_STEP_SIZE = 60;

    public SpaceTimeCube() {
        int longitude = (int) ((MAX_LONG - MIN_LONG) / STEP_SIZE);
        int latitude = (int) ((MAX_LAT - MIN_LAT) / STEP_SIZE);
        this.values = new int[longitude][latitude][(int) (24 * 60 / TIME_STEP_SIZE)];
    }

    /**
     * update the values in the cube that are on the route
     *
     * @param route
     */
    public void addRoute(Route route) {
        System.out.println("StartLong: "+route.startLong+" EndLong: "+route.endLong+
                " StartLat: "+route.startLat+" EndLat: "+route.endLat);
        double startLong = ((int) (route.startLong / STEP_SIZE))*STEP_SIZE;
        double difLong = abs(route.endLong - route.startLong);
        double directionLong = Math.signum(route.endLong - route.startLong);
        double difLat = abs(route.endLat - route.startLat);
        double directionLat = Math.signum(route.endLong - route.startLong);
        double difTime = route.endTime - route.startTime;
        if (difTime < 0) {
            difTime += 60 * 24;
        }
        double slope = difLat / difLong;
        double timeSlope = difTime / difLong;
        System.out.println(slope);
        for (double i = 0; i <= difLong; i += STEP_SIZE){
            // Defining which x coordinate we are at now
            double xLong; //
            double minLat; // Rounding this down to the stepsize to avoid weird digits
            double maxLat;
            if(directionLong==1){
                xLong = startLong + i;
                minLat = route.startLat + (i * slope);
                minLat = ((double) ((int) (minLat / STEP_SIZE))) * STEP_SIZE;
                maxLat = route.startLat + ((i + STEP_SIZE) * slope);
            } else {
                xLong = startLong - i;
                minLat = route.startLat - (i * slope);
                minLat = ((double) ((int) (minLat / STEP_SIZE))) * STEP_SIZE;
                maxLat = route.startLat + ((i - STEP_SIZE) * slope);
            }

            int minTime = (int) (route.startTime + (i * timeSlope));
            minTime = ((int) (minTime / TIME_STEP_SIZE)) * TIME_STEP_SIZE;
            int maxTime = (int) (route.startTime + ((i + STEP_SIZE) * timeSlope));

            for (double j = minLat; j <= maxLat; j += STEP_SIZE) {
                for (int k = minTime; k <= maxTime; k += TIME_STEP_SIZE) {
                    if (j <= Math.max(route.startLat, route.endLat) && j >= Math.min(route.startLat, route.endLat) &&
                            xLong <= Math.max(route.startLong, route.endLong) && xLong >= Math.min(route.startLong, route.endLong)) {
                    System.out.println("I appended shit to M[" +
                            String.format("$%,.3f", xLong) + ", " + String.format("$%,.3f", j) + String.format("$%,d", k) + "]");
                    increaseValue(j, xLong, k);
                    }
                }
            }
        }
    }

    /**
     * increase a value at given coordinate (in degrees/minutes)
     *
     * @param latitude
     * @param longitude
     * @param time
     */
    private void increaseValue(double latitude, double longitude, int time) {
        int latSteps = (int) ((latitude - MIN_LAT) / STEP_SIZE);
        int longSteps = (int) ((longitude - MIN_LONG) / STEP_SIZE);
        time = time % (60 * 24);
        int timeSteps = (int) (time / TIME_STEP_SIZE);
        values[longSteps][latSteps][timeSteps]++;
    }

    public int[][][] getValues() {
        return values;
    }

    public void printValues() {
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                for (int k = 0; k < values[i][j].length; k++) {
                    if (values[i][j][k] > 0) {
                        System.out.println("[" + i + ", " + j + ", " + k + "] " + values[i][j][k]);
                    }
                }
            }
        }
    }
}
