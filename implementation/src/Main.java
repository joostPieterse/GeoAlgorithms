import java.io.*;

public class Main {


    public static void main(String[] args) {
        Main main = new Main();
        //download: http://www.nyc.gov/html/tlc/html/about/trip_record_data.shtml
        main.processData(new File("data/yellow_tripdata_2016-01.csv"), ",");
    }

    private void processData(File file, String delimiter) {
        SpaceSquare spaceSquare = new SpaceSquare();
        spaceSquare.addRoute(new Route(73.70014, 40.50014, 73.70234, 40.50601,100.0));
        spaceSquare.printValues();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String lineString;
            //skip first line
            br.readLine();
            while ((lineString = br.readLine()) != null) {
                String[] line = lineString.split(delimiter);
                double pickUpTime = Double.parseDouble(line[1]);
                double dropOffTime = Double.parseDouble(line[2]);
                double tripDistance = Double.parseDouble(line[4]);
                double startLong = Math.abs(Double.parseDouble(line[5]));
                double startLat = Math.abs(Double.parseDouble(line[6]));
                double endLong = Math.abs(Double.parseDouble(line[9]));
                double endLat = Math.abs(Double.parseDouble(line[10]));

                if(startLong!=0 && startLat != 0 && endLong != 0 && endLat != 0){
                    spaceSquare.addRoute(new Route(startLong, startLat, endLong, endLat, tripDistance));
                }
            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
