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


        double closest =999, distance;//initialize as big number
        int whichPool; //index number to track which pool it was
        for (int i=0; i<numPools; i++) {
            distance = calcDistance(pools[0], pools[i];

            if () distance < closest) {
                closest = distance;
                whichPool = i;
            }
        }

        //set closest pool as the child of the root
        pools[0].setChild(pools[whichPool]);
        pools[whichPool].setLat(999999);//set lattitude as very high so that its distance is never considered again

    }//main

    public static void createTree (Pool root, Pool [] pools){
        Pool [] children = new Pool[root.getChildren.length];
        children = root.getChildren();//gets all the children of the root node
        double closest = 999, distance;
        int whichPool, whichChild;

        //finds the two closest children and pool
        for (int i=0; i<children.length; i++){
            for (int j = 0; j < pools.length; j++){

                distance = calcDistance(pools[0], pools[i];

                if () distance < closest) {
                    closest = distance;
                    whichChild = i;
                    whichPool = j;
                }
            }
        }


    }//end createTree

    public static double calcDistance (Pool poolOne, Pool poolTwo){

        //calculate euclidian distance between two pools
        //declare vars
        double latOne, latTwo, lonOne, lonTwo ,dRad, distance, ratio = (3.14/180);

            latOne = ratio *(poolOne.getLat());
            lonOne = ratio *(poolOne.getLon());
            latTwo = ratio *(poolTwo.getLat());
            lonTwo = ratio* (poolTwo.getLon());

            //formula
            dRad = 2*Math.asin(Math.sqrt(Math.pow(Math.sin((latOne - latTwo)/2),2)+(Math.cos(latOne)*Math.cos(latTwo)*Math.pow(Math.sin((lonOne-lonTwo)/2),2))));
            distance = 6371*dRad;
            return distance;
    }
}//class