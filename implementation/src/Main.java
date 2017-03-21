import java.io.*;

public class Main {


    public static void main(String[] args) {
        Main main = new Main();
        //download: http://www.nyc.gov/html/tlc/html/about/trip_record_data.shtml
        main.processData(new File("data/yellow_tripdata_2016-01.csv"), ",");
    }

    private void processData(File file, String delimiter) {
        SpaceTimeCube spaceTimeCube = new SpaceTimeCube();
        spaceTimeCube.addRoute(new Route(73.70014, 40.50014, 73.70234, 40.50601,100.0, 60, 180));
        spaceTimeCube.printValues();
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
                    spaceTimeCube.addRoute(new Route(startLong, startLat, endLong, endLat, tripDistance,0,0));
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
