import javax.json.JsonNumber;
import java.util.LinkedList;

public class Pool {


    String name;
    int parkId;
    double lat, lon, distance;
    Pool parent;
    LinkedList<Pool> childrenList = new LinkedList<Pool>();

    public void setName (String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(int parkId){
        this.parkId = parkId;
    }

    public int getId(){
        return parkId;
    }

    public void setLat (double lat){
        this.lat=lat;
    }

    public double getLat (){
        return lat;
    }

    public void setLon (double lon){
        this.lon = lon;
    }

    public double getLon (){
        return lon;
    }

    public void setParentPool(Pool parent){
        this.parent = parent;
    }

    public Pool getParentPool(){
        return parent;
    }

    public void setDistance(double distance){
        this.distance = distance;
    }

    public double getDistance(){
        return distance;
    }

    public void setChild (Pool pool){childrenList.add(pool);}

    public Pool [] getChildren {return childrenList.toArray();}

}
