import static java.lang.Math.abs;
import static jdk.nashorn.internal.objects.NativeMath.round;

public class SpaceSquare {
    private int[][] values;
    private static final double STEP_SIZE = 0.001;

    public SpaceSquare(int longitude, int latitude) {
        this.values = new int[longitude][latitude];
    }

    public void addRoute(Route route){
         double difLat = abs(route.endLat-route.startLat);
         double difLong = abs(route.endLong-route.startLong);
         double slope = difLat/difLong;
         System.out.println(slope);
         for(double i=0; i <= difLong; i+=STEP_SIZE){
             // Defining which x coordinate we are at now
             double xLong = route.startLong+i;

             double minLat = route.startLat+(i*slope);
             // Rounding the minLat such that it fits the step_size.
             minLat = ((double)((int)(minLat / STEP_SIZE)))*STEP_SIZE;
             double maxLat = route.startLat+((i+STEP_SIZE)*slope);

             for(double j=minLat; j <= maxLat; j+=STEP_SIZE){
                 System.out.println("I appended shit to M[" +
                         String.format("$%,.3f", xLong) + ", " + String.format("$%,.3f", j)+"]");
             }
         }
    }

    public int[][] getValues(){
        return values;
    }
}
