package decomposed;

import java.util.HashMap;

/**
 * Created by s130604 on 22-3-2017.
 */
public class Statistic {
    SpaceTimeCube cube;

    public Statistic(SpaceTimeCube cube) {
        this.cube = cube;
    }

    private int customStatisticPerCell(int latitude, int longitude, int time) {
        int result = 0;
        AttributePlane[] planes = cube.planes;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    if (longitude + i < planes[time].getPlane()[0].length && longitude + i >= 0 &&
                            latitude + j < planes[time].getPlane().length && latitude + j >= 0 &&
                            time + k < planes.length && time + k >= 0) {
                        result += planes[time + k].getPlane()[latitude + j][longitude + i];
                    }
                }
            }
        }
        result += 25 * planes[time].getPlane()[latitude][longitude];
        return result;
    }

    /**
     * @param numResults the number of cells that is returned
     * @return the greatest cells according to this statistics
     */
    public HashMap<SpaceTimeCell, Double> customStatistic(int numResults) {
        System.out.println("Start calculating statistic");
        AttributePlane[] planes = cube.planes;
        //calculate values by looking at neighbours
        double[][][] statisticValues = new double[planes[0].getPlane()[0].length][planes[0].getPlane().length][planes.length];
        for (int time = 0; time < planes.length; time++) {
            int total = 0;
            //calculate values and keep track of the total
            for (int longitude = 0; longitude < planes[time].getPlane()[0].length; longitude++) {
                for (int latitude = 0; latitude < planes[time].getPlane().length; latitude++) {
                    int statistic = customStatisticPerCell(latitude, longitude, time);
                    statisticValues[longitude][latitude][time] = statistic;
                    total += statistic;
                }
            }
            //divide by total to get relative statistic per time step
            for (int longitude = 0; longitude < planes[time].getPlane()[0].length; longitude++) {
                for (int latitude = 0; latitude < planes[time].getPlane().length; latitude++) {
                    statisticValues[longitude][latitude][time] /= Math.max(total, 1);
                }
            }
        }
        System.out.println("Done calculating statistic");
        System.out.println("Start calculating max values");
        HashMap<SpaceTimeCell, Double> maxValueMap = new HashMap<>();
        for (int longitude = 0; longitude < statisticValues.length; longitude++) {
            for (int latitude = 0; latitude < statisticValues[longitude].length; latitude++) {
                for (int time = 0; time < statisticValues[longitude][latitude].length; time++) {
                    double currentValue = statisticValues[longitude][latitude][time];
                    SpaceTimeCell currentCell = new SpaceTimeCell(latitude, longitude, time);
                    //add cell if the map is not yet full, check if it is larger than the smallest value in the map otherwise
                    if (maxValueMap.size() < numResults) {
                        maxValueMap.put(currentCell, (double) statisticValues[longitude][latitude][time]);
                    } else {
                        SpaceTimeCell smallestCell = null;
                        double smallestValue = Double.MAX_VALUE;
                        //check if there is a larger value in the map
                        for (SpaceTimeCell cell : maxValueMap.keySet()) {
                            double value = statisticValues[cell.longitude][cell.latitude][cell.time];
                            if (value < smallestValue) {
                                smallestCell = cell;
                                smallestValue = value;
                            }
                        }
                        if (currentValue > smallestValue) {
                            maxValueMap.remove(smallestCell);
                            maxValueMap.put(currentCell, (double) currentValue);
                        }
                    }
                }
            }
        }
        return maxValueMap;
    }
}
