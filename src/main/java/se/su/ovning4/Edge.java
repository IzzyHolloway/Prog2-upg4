package se.su.ovning4;
public class Edge<T> {

    private final String destination;
    private final String name;
    private int weight;

    public Edge(String destination, String name, int weight){

        if (weight < 0) {
            throw new IllegalArgumentException("Vikten får inte vara negativ");
        }
        this.destination = destination;
        this.name = name;
        this.weight = weight;

    }
    public  String getDestination(){
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int newWeight){
        if(newWeight < 0){
            throw new IllegalArgumentException("Vikten får inte vara negativ");
        }
        this.weight = newWeight;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return "till " + destination + " med " + name + " tar " + weight;


    }
}







