import java.io.*;

public class Main {


    public static void main(String[] args) {
        Main main = new Main();
        //download: http://www.nyc.gov/html/tlc/html/about/trip_record_data.shtml
        main.processData(new File("data/yellow_tripdata_2016-01.csv"), ",");
    }

    private void processData(File file, String delimiter) {
        SpaceSquare spaceSquare = new SpaceSquare((int) ((MAX_LONG - MIN_LONG) / STEP_SIZE), (int) ((MAX_LAT - MIN_LAT) / STEP_SIZE));
        spaceSquare.addRoute(new Route(73.70014, 40.50014, 73.70234, 40.50601,100.0));
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String lineString;
            //skip first line
            br.readLine();
            while ((lineString = br.readLine()) != null) {
                String[] line = lineString.split(delimiter);
                double startLong = Math.abs(Double.parseDouble(line[5]));
                double startLat = Math.abs(Double.parseDouble(line[6]));
                double endLong = Math.abs(Double.parseDouble(line[9]));
                double endLat = Math.abs(Double.parseDouble(line[10]));
                double tripDistance = Double.parseDouble(line[4]);
                //spaceSquare.addRoute(new Route(startLong, startLat, endLong, endLat, tripDistance));
            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
