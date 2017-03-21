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
        int longitude = (int) ((MAX_LONG - MIN_LONG) / STEP_SIZE)+1;
        int latitude = (int) ((MAX_LAT - MIN_LAT) / STEP_SIZE)+1;
        this.values = new int[longitude][latitude][(int) (24 * 60 / TIME_STEP_SIZE)];
    }

    /**
     * update the values in the cube that are on the route
     *
     * @param route
     */
    public void addRoute(Route route) {
        System.out.println("---------------------------------------------------------------------------------------------" +
                "NEW POINT!"+
        "--------------------------------------------------------------------------------------------------------");
        System.out.println("StartLong: "+route.startLong+" EndLong: "+route.endLong+
                " StartLat: "+route.startLat+" EndLat: "+route.endLat+
                " StartTime: "+route.startTime+" EndTime: "+route.endTime);

        // Start out by rounding all variables as we won't use non-rounded.
        double startLong = ((int) (route.startLong / STEP_SIZE))*STEP_SIZE;
        double endLong = ((int) (route.endLong / STEP_SIZE))*STEP_SIZE;
        double startLat = ((int) (route.startLat / STEP_SIZE))*STEP_SIZE;
        double endLat = ((int) (route.endLat / STEP_SIZE))*STEP_SIZE;

        // Specifying time
        int startTime = route.startTime;
        int endTime = route.endTime;
        int difTime = route.endTime - route.startTime;
        if (difTime < 0) {
            difTime += 60 * 24;
        }

        // Calculating the differences and which direction the cab went.
        double difLong = route.endLong - route.startLong;
        double difLat = route.endLat - route.startLat;

        double directionLong = Math.signum(route.endLong - route.startLong);
        double directionLat = Math.signum(route.endLat - route.startLat);

        // Step sizes. Whether they should be positive or negative.
        double STEP_SIZE_HOR = STEP_SIZE;
        if(directionLong!=1){
            STEP_SIZE_HOR = 0 - STEP_SIZE;
        }
        double STEP_SIZE_VER = STEP_SIZE;
        if(directionLat!=1){
            STEP_SIZE_VER = 0 - STEP_SIZE;
        }

        // Calculating the slope and the line
        // The line is a formula consisting of y=ax+b. a_slope is a and b_start is b
        double a_slope = difLat / difLong;
        double b_start = route.endLat - a_slope*route.endLong;

        double timeSlope = difTime / difLong;
        System.out.println("y= "+a_slope+" x +"+b_start);
        for (double xLong = route.startLong;
             ((directionLong==1 && xLong <= endLong + STEP_SIZE) ||
                     (directionLong!=1 && xLong >= endLong)) &&
                     (xLong >= MIN_LONG && xLong < MAX_LONG);
             xLong += STEP_SIZE_HOR){

            double current_start_x;
            double current_stop_x;
            double current_start_y;
            double current_stop_y;

            // If the direction is increasing horizontally
            if(directionLong==1){
                current_start_x = ((int) (xLong / STEP_SIZE)) * STEP_SIZE;
                if(current_start_x < route.startLong){
                    current_start_x = route.startLong;
                }
                current_stop_x = ((int) (xLong / STEP_SIZE)+1) * STEP_SIZE;
                if(current_stop_x > route.endLong){
                    current_stop_x = route.endLong;
                }
            } else { // If the direction is decreasing horizontaly
                current_start_x = ((int) (xLong / STEP_SIZE)+1) * STEP_SIZE;
                if(current_start_x > route.startLong){
                    current_start_x = route.startLong;
                }
                current_stop_x = ((int) (xLong / STEP_SIZE)) * STEP_SIZE;
                if(current_stop_x < route.endLong){
                    current_stop_x = route.endLong;
                }
            }

            current_start_y = getYValue(a_slope, b_start, current_start_x);
            current_stop_y = getYValue(a_slope, b_start, current_stop_x);

            double rounded_current_x;
            if(directionLong==1){
                rounded_current_x = ((int)(current_start_x/STEP_SIZE))*STEP_SIZE;
            } else {
                rounded_current_x = ((int)(current_stop_x/STEP_SIZE))*STEP_SIZE;
            }
            double rounded_current_stop_y = ((int)(current_stop_y/STEP_SIZE))*STEP_SIZE;

//            int minTime = (int) (route.startTime + (i * timeSlope));
//            minTime = ((int) (minTime / TIME_STEP_SIZE)) * TIME_STEP_SIZE;
//            int maxTime = (int) (route.startTime + ((i + STEP_SIZE) * timeSlope));

            int k = 0;
            System.out.println("---------------------------------------------- NEW LINE ----------------------------------------");
            System.out.println("Starting for2 with "+rounded_current_x+" and "+directionLong+" and "+directionLat+" and "+rounded_current_stop_y);
            System.out.println("Coordinate start "+current_start_x+" and "+current_start_y);
            System.out.println("Coordinate end "+current_stop_x+" and "+current_stop_y);
            for (double yLat = current_start_y;
                 ((directionLat == 1 && yLat <= rounded_current_stop_y + STEP_SIZE) ||
                         (directionLat != 1 && yLat >= rounded_current_stop_y)) &&
                         (yLat >= MIN_LAT  && yLat < MAX_LAT);
                 yLat += STEP_SIZE_VER) {
//                if (j <= Math.max(route.startLat, route.endLat) && j >= Math.min(route.startLat, route.endLat) &&
//                        xLong <= Math.max(route.startLong, route.endLong) && xLong >= Math.min(route.startLong, route.endLong)) {
                double rounded_current_y = ((int)(yLat/STEP_SIZE))*STEP_SIZE;
                System.out.println("M[" +
                        String.format("$%,.3f", rounded_current_x) + ", " + String.format("$%,.3f", rounded_current_y) + ", "+String.format("$%,d", k) + "]");

                increaseValue(yLat, xLong, k);

//                for (int k = minTime; k <= maxTime; k += TIME_STEP_SIZE) {
//                }
            }
        }
    }
    /**
     * Calculating the y for a formula y = a*x+b
     * @param a, b, x
     * @returns a*x+b
     */
    private double getYValue(double a, double b, double x){
        return a*x+b;
    }

    /**
     * Calculating the x value for a formula y = a*x+b
     * @param a, b, y
     * @returns (y-b)/a
     */
    private double getXValue(double a, double b, double y){
        return (y-b)/a;
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
