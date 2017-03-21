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
    private static final double TIME_STEP_SIZE = 60;

    public SpaceTimeCube() {
        int longitude = (int) ((MAX_LONG - MIN_LONG) / STEP_SIZE);
        int latitude = (int) ((MAX_LAT - MIN_LAT) / STEP_SIZE);
        this.values = new int[longitude][latitude][(int) (24 * 60 / TIME_STEP_SIZE)];
    }

    public void addRoute(Route route) {
        System.out.println("StartLong: "+route.startLong+" EndLong: "+route.endLong+
                " StartLat: "+route.startLat+" EndLat: "+route.endLat);
        double startLong = ((int) (route.startLong / STEP_SIZE))*STEP_SIZE;
        double difLong = abs(route.endLong - route.startLong);
        double directionLong = Math.signum(route.endLong - route.startLong);
        double difLat = abs(route.endLat - route.startLat);
        double directionLat = Math.signum(route.endLong - route.startLong);
        double slope = difLat / difLong;
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

            for (double j = minLat; j <= maxLat; j += STEP_SIZE) {
                if (j <= Math.max(route.startLat, route.endLat) && j >= Math.min(route.startLat, route.endLat)) {
                    System.out.println("I appended shit to M[" +
                            String.format("%,.4f", xLong) + ", " + String.format("%,.4f", j) + "]");
                    increaseValue(j, xLong, 0);
                }
            }
        }
    }

    private void increaseValue(double latitude, double longitude, int time) {
        int latSteps = (int) ((latitude - MIN_LAT) / STEP_SIZE);
        int longSteps = (int) ((longitude - MIN_LONG) / STEP_SIZE);
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
                        System.out.println("[" + i + ", " + j + ", " + k + "] " + values[i][j]);
                    }
                }
            }
        }
    }
}
