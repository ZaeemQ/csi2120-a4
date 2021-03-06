import java.util.LinkedList;

public class Pool {


    String name;
    double lat, lon, distance=0;
    Pool parent;
    LinkedList<Pool> childrenList = new LinkedList<Pool>();

    public void setName (String name){
        this.name = name;
    }

    public String getName() {return name;}

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

    public double getDistance(){return distance;}

    public void setChild (Pool pool){childrenList.add(pool);}

    public Pool [] getChildren () {
        Pool [] children = new Pool [childrenList.size()];

        for (int i = 0; i < children.length; i++){
            children[i]=childrenList.get(i);
        }
        return children;
    }

}
