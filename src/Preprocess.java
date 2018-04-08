import sun.awt.image.ImageWatched;

import javax.json.*;
import java.io.*;
import java.util.LinkedList;

public class Preprocess {

    public static void main(String[] args) throws IOException {

        //variables
        int numPools = 61;
        Pool [] pools = new Pool[numPools];
        LinkedList < Pool> tree = new LinkedList<Pool>();
        LinkedList < Pool> westEast = new LinkedList<Pool>();

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

        //create a linked list from west to east with all the pools in it
        for (int i = 0; i < pools.length; i++){
            westEast.add (pools [i]);
        }

        //creating the tree
        double distance=0, closest = 999;
        int index=0;
        tree.add(westEast.pollFirst()); //add the most west pool as head (root)
        while (westEast.peekFirst() != null){
            for (int j = 0; j < tree.size(); j++) {
                distance = calcDistance(tree.get(j), westEast.getFirst());
                if (distance < closest){ //get the closest node in the tree to the pool
                    closest = distance;
                    index = j;
                }
            }
            tree.add(westEast.pollFirst());
            tree.get(index).setChild(tree.peekLast());//removes first element of westEast and adds it as the child
            tree.peekLast().setParentPool(tree.get (index));
            tree.peekLast().setDistance(distance + tree.get(index).getDistance());
            distance = 0;
            closest = 999;
            index=0;
        }

        preorder (tree.peekFirst());

    }//main

    public static void preorder (Pool root){
        System.out.println (root.getName() + "  " + root.getDistance());
        Pool [] kids = root.getChildren();

        if (kids.length > 0) {
            for (int i = 0; i < root.getChildren().length; i++) {
            preorder(kids[i]);
            }
        }
    }


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