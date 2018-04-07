import javax.json.*;
import java.io.*;
import java.util.LinkedList;

public class Preprocess {

    public static void main(String[] args) throws IOException {
        String file;
        if (args.length != 1) {
            System.exit (1);
        }
        file  = args[0];

        //variables
        int numPools = 62;
        Pool[] pools = new Pool[numPools];
        LinkedList <Pool> tree = new LinkedList<>();
        Pool root;

        //input stream
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JsonReader reader = Json.createReader(fis);
        JsonObject wadepool = reader.readObject();
        reader.close();


        //wadepools is the JsonArray of all pools
        JsonArray poolArray = wadepool.getJsonArray("features");

        //for loop to read the Json in to array of Pool object called pools
        for (int i = 0; i < numPools; i++) {
            pools[i] = new Pool();
            pools[i].setId(poolArray.getJsonObject(i).getJsonObject("properties").getInt("PARK_ID"));
            pools[i].setName(poolArray.getJsonObject(i).getJsonObject("properties").getString("PARKNAME"));
            pools[i].setLon(Double.parseDouble(String.valueOf(poolArray.getJsonObject(i).getJsonObject("geometry").getJsonArray("coordinates").getJsonNumber(0))));
            pools[i].setLat(Double.parseDouble(String.valueOf(poolArray.getJsonObject(i).getJsonObject("geometry").getJsonArray("coordinates").getJsonNumber(1))));
        }


        //set in order from west to east
        Pool temp;
        for (int i = 0; i < numPools; i++) {
            for (int j = i + 1; j < numPools; j++) {
                if (pools[j].getLon() < pools[i].getLon()) {
                    temp = pools[i];
                    pools[i] = pools[j];
                    pools[j] = temp;
                }
            }
        }//end for loop

        //set root as most west pool
        root = pools[0];
        root.setDistance(0);
        tree.add(root);


        //calculate euclidian distance between two pools
        //declare vars
        double latOne, latTwo, lonOne, lonTwo, dRad, distance, ratio = (3.14 / 180), shortestDistance = 0;
        int shortestDistancePool = 0, shortestTree=0;

        //compares distance between nodes in west to east
        for (int k=0;k<numPools;k++){
            for (int j = 0; j < tree.size(); j++) {
                for (int i = j; i < numPools ; i++) {


                latOne = tree.get(j).getLat() * ratio;//convert to radians
                lonOne = tree.get(j).getLon() * ratio;//convert to radians
                latTwo = pools[i].getLat() * ratio;//convert to radians
                lonTwo = pools[i].getLon() * ratio;//convert to radians
                dRad = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin((latOne - latTwo) / 2), 2) + (Math.cos(latOne) * Math.cos(latTwo) * Math.pow(Math.sin((lonOne - lonTwo) / 2), 2))));
                distance = 6371 * dRad; //distance between pool and pool in tree

                if (shortestDistance == 0) {
                    shortestDistance = distance;
                } else if (distance < shortestDistance) {
                    shortestDistance = distance;
                    shortestDistancePool = i;
                    shortestTree = j;
                    pools[i].setDistance(tree.get(j).getDistance() + distance);
                }
            }

        }
        tree.addLast(pools[shortestDistancePool]);
        tree.get(shortestTree).setChildren(pools[shortestDistancePool]);
        tree.getLast().setParentPool(tree.get(shortestTree));
        tree.getLast().setDistance(tree.getLast().getParentPool().getDistance() + tree.getLast().getDistance());
    }



        //call print in preorder and pass in root pool
        BufferedWriter writer = new BufferedWriter(new FileWriter("solution.txt"));
        try {
            printPreorder (root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }//main

    private static void printPreorder(Pool pool, BufferedWriter writer) throws IOException {
        if (pool == null)
            return;

        writer.write(pool.getName() + " " + pool.getDistance());
        writer.newLine();

        //print pool in ordered way
        System.out.printf("%-50s %-10d %-10f \t %-10f \t %-10f \n",pool.getName(), pool.getId(), pool.getLat(),pool.getLon(), pool.getDistance());


        for (int i=0;i< pool.getChildren().length; i++) {
            if (pool.getChildren()[i] != null) {
                printPreorder(pool.getChildren()[i], writer);
            }
        }//for loop

        writer.close();

    }//printPreorder
}//class