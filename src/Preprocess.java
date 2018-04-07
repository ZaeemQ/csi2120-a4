import javax.json.*;
import java.io.*;

public class Preprocess {

    public static void main(String[] args) throws IOException {

        //variables
        int numPools = 61;
        Pool [] pools = new Pool[numPools];

        //input stream
        InputStream fis = null;
        try {
            fis = new FileInputStream("wading-pools.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JsonReader reader = Json.createReader(fis);
        JsonObject wadepool = reader.readObject();
        reader.close();



        //wadepools is the JsonArray of all pools
        JsonArray poolArray=wadepool.getJsonArray("features");

        //for loop to read the Json in to array of Pool object called pools
        for (int i = 0; i < numPools; i++) {
            pools[i] = new Pool();
            pools[i].setId(poolArray.getJsonObject(i).getJsonObject("properties").getInt("PARK_ID"));
            pools[i].setName(poolArray.getJsonObject(i).getJsonObject("properties").getString("PARKNAME"));
            pools[i].setLon(Double.parseDouble(String.valueOf(poolArray.getJsonObject(i).getJsonObject("geometry").getJsonArray("coordinates").getJsonNumber(0))));
            pools[i].setLat(Double.parseDouble(String.valueOf(poolArray.getJsonObject(i).getJsonObject("geometry").getJsonArray("coordinates").getJsonNumber(1))));
        }

        //set in order from west to east
        Pool temp ;
        for (int i = 0; i < numPools; i++){
            for (int j = i+1; j < numPools; j++){
                if (pools [j].getLon() < pools[i].getLon() ){
                    temp = pools[i];
                    pools[i]=pools[j];
                    pools[j]=temp;
                }
            }
        }//end for loop

        /*creates binary tree, with left child being the next southern pool
        and right child being the next pool if its north
        */
        for (int i=0;i<numPools-1;i++){

            if (pools[i+1].getLat() > pools[i].getLat()){
                pools[i].setRightChild(pools[i + 1]);
            }
            else {
                pools[i].setLeftChild(pools[i + 1]);
            }

            pools[i+1].setParentPool(pools[i]);
        }



        //calculate euclidian distance
        //declare vars
        double latOne, latTwo, lonOne, lonTwo ,dRad, distance, ratio = (3.14/180);
        pools[0].setDistance(0);
        for (int i=0; i<numPools-1; i++){
            latOne = pools[i].getLat();
            lonOne = pools[i].getLon();
            latTwo = pools[i+1].getLat();
            lonTwo = pools[i+1].getLon();
            latOne = latOne * ratio;//convert to radians
            lonOne = lonOne* ratio;//convert to radians
            latTwo = latTwo * ratio;//convert to radians
            lonTwo = lonTwo* ratio;//convert to radians

            //formula
            dRad = 2*Math.asin(Math.sqrt(Math.pow(Math.sin((latOne - latTwo)/2),2)+(Math.cos(latOne)*Math.cos(latTwo)*Math.pow(Math.sin((lonOne-lonTwo)/2),2))));
            distance = 6371*dRad;
            distance = distance + pools[i].getDistance();
            //System.out.printf("%-5f \t %-5f \t %-5f \t %-5f \t %-5f \t %-5f \n", latOne, lonOne, latTwo, lonTwo, dRad, distance);
            pools[i+1].setDistance(distance);
        }

        //call print in preorder and pass in root pool
        BufferedWriter writer = new BufferedWriter(new FileWriter("solution.txt"));
        try {
            printPreorder (pools[0], writer);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //test printing

        /*
        for (int i=0;i<numPools;i++) {
            System.out.print(pools[i].getName());
            System.out.printf("%-1s %-10.2f \n"," " ,pools[i].getDistance());
        }*/


    }//main

    public static void printPreorder(Pool pool, BufferedWriter writer) throws IOException {
        if (pool == null)
            return;


        writer.write(pool.getName() + " " + pool.getDistance());
        writer.newLine();



        //print pool in ordered way
        System.out.printf("%-50s %-10d %-10f \t %-10f \t %-10f \n",pool.getName(), pool.getId(), pool.getLat(),pool.getLon(), pool.getDistance());

        //left tree
        printPreorder(pool.getLeftChild(), writer);

        //right tree
        printPreorder(pool.getRightChild(), writer);

        writer.close();

    }//printPreorder
}//class