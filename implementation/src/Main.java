import java.io.*;

public class Main {

    //in degrees
    private static final double MIN_LONG = 73.7;
    private static final double MAX_LONG = 74.25;
    private static final double MIN_LAT = 40.5;
    private static final double MAX_LAT = 40.9;
    private static final double STEP_SIZE = 0.001;


    public static void main(String[] args) {
        Main main = new Main();
        //download: http://www.nyc.gov/html/tlc/html/about/trip_record_data.shtml
        main.processData(new File("data/yellow_tripdata_2016-01.csv"), ",");
    }

    private void processData(File file, String delimiter) {
        SpaceSquare spaceSquare = new SpaceSquare((int) ((MAX_LONG - MIN_LONG) / STEP_SIZE), (int) ((MAX_LAT - MIN_LAT) / STEP_SIZE));
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String lineString;
            //skip first line
            br.readLine();
            while ((lineString = br.readLine()) != null) {
                String[] line = lineString.split(delimiter);
                double startLong = Double.parseDouble(line[5]);
                double startLat = Double.parseDouble(line[6]);
                double endLong = Double.parseDouble(line[9]);
                double endLat = Double.parseDouble(line[10]);
                double tripDistance = Double.parseDouble(line[4]);
                Route route = new Route(startLong, startLat, endLong, endLat, tripDistance);
            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
