import java.io.*;

public class Main {


    public static void main(String[] args) {
        Main main = new Main();
        //download: http://www.nyc.gov/html/tlc/html/about/trip_record_data.shtml
        main.processData(new File("C:\\Users\\Administrator\\IdeaProjects\\GeoAlgorithms\\implementation/data/yellow_tripdata_2016-01.csv"), ",");
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
                int pickUpTime = convertTimeStampToMinutes(line[1]);
                int dropOffTime = convertTimeStampToMinutes(line[2]);
                double tripDistance = Double.parseDouble(line[4]);
                double startLong = Math.abs(Double.parseDouble(line[5]));
                double startLat = Math.abs(Double.parseDouble(line[6]));
                double endLong = Math.abs(Double.parseDouble(line[9]));
                double endLat = Math.abs(Double.parseDouble(line[10]));

                if(startLong!=0 && startLat != 0 && endLong != 0 && endLat != 0){
                    spaceTimeCube.addRoute(new Route(startLong, startLat, endLong, endLat, tripDistance, pickUpTime, dropOffTime));
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Converts a timestamp to minutes
    // @Param timeString, provides a timestamp of the form of "1/29/2016 10:43:02"
    // @Returns amount of minutes since 00:00
    private int convertTimeStampToMinutes(String timeString){
        String[] splited = timeString.split("\\s+");
        int minutes = 0;
        minutes += Integer.parseInt(Character.toString(splited[1].charAt(0)))*10*60;
        minutes += Integer.parseInt(Character.toString(splited[1].charAt(1)))*60;
        minutes += Integer.parseInt(Character.toString(splited[1].charAt(3)))*10;
        minutes += Integer.parseInt(Character.toString(splited[1].charAt(4)));
        return minutes;
    }
}
