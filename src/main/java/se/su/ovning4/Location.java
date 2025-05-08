package se.su.ovning4;

public class Location implements Node{
    private final String name;
    private final double x, y;

    public Location(String name, double x, double y){
        this.name = name;
        this.x = x;
        this.y = y;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return name + " [" + x + ", " + y + "]";
    }
}
