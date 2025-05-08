package se.su.ovning4;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Exercise4 {
    // Här sparas grafen som vi jobbar med. Den kan innehålla Location, Person och Record.
    private Graph<Node> graph = new ListGraph<>();

    // Ladda in en graf med platser och vägar mellan dem

    public void loadLocationGraph(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // Första raden innehåller alla platser (noder i grafen)
            String line = reader.readLine();
            String[] parts = line.split(";");

            // Skapa en Location-nod för varje plats
            for (int i = 0; i < parts.length; i += 3) {
                String name = parts[i].trim();
                double x = Double.parseDouble(parts[i + 1].trim());
                double y = Double.parseDouble(parts[i + 2].trim());
                Location location = new Location(name, x, y);
                graph.add(location);
            }

            // Nu läser vi resterande rader som innehåller vägar (kanter i grafen)
            while ((line = reader.readLine()) != null) {
                String[] edgeParts = line.split(";");
                String fromName = edgeParts[0].trim(); // från vilken plats
                String toName = edgeParts[1].trim();   // till vilken plats
                String connectionType = edgeParts[2].trim(); // t.ex. "Tåg"
                int weight = Integer.parseInt(edgeParts[3].trim()); // t.ex. restid

                // Hitta plats-noderna i grafen
                Node from = graph.findNode(fromName);
                Node to = graph.findNode(toName);

                // Lägg till en väg i grafen om båda noderna hittades
                if (from != null && to != null) {
                    graph.connect(from, to, connectionType, weight);
                } else {
                    System.err.println("Hittade inte: " + fromName + " eller " + toName);
                }
            }

            // Skriv ut hela grafen så vi ser att det blev rätt
            System.out.println(graph);

        } catch (IOException e) {
            System.err.println("Fel när filen skulle läsas: " + e.getMessage());
        }
    }

    // Ladda in graf med personer och skivor

    public void loadRecommendationGraph(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;


            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length != 3) continue;

                String personName = parts[0].trim();
                String recordTitle = parts[1].trim();
                String artist = parts[2].trim();

                // Skapa objekt för person och skiva
                Person person = new Person(personName);
                Record record = new Record(recordTitle, artist);

                // Lägg till noder i grafen (om de inte redan finns)
                graph.add(person);
                graph.add(record);

                // Skapa kopplingen mellan person och skiva
                graph.connect(person, record, "", 1);
            }

            System.out.println("Rekommendationsgrafen är nu inläst.");

        } catch (IOException e) {
            System.err.println("Fel vid läsning av fil: " + e.getMessage());
        }
    }



    public int getPopularity(Record item) {
        if (item == null) return 0;

        // Alla kanter som går från skivan visar vilka som äger den
        return graph.getEdgesFrom(item).size();
    }



    public SortedMap<Integer, SortedSet<Record>> getAlsoLiked(Record item) {
        SortedMap<Integer, SortedSet<Record>> result = new TreeMap<>(Comparator.reverseOrder());

        if (item == null) return result;

        //Hittar alla personer som äger skivan
        Set<Node> owners = graph.getEdgesTo(item).stream()
                .map(Edge::getSource)
                .collect(Collectors.toSet());

        // Räknar hur många gånger varje annan skiva förekommer hos dessa personer
        Map<Record, Integer> counts = new HashMap<>();

        for (Node owner : owners) {
            for (Edge edge : graph.getEdgesFrom(owner)) {
                Node other = edge.getDestination();
                if (other instanceof Record && !other.equals(item)) {
                    Record r = (Record) other;
                    counts.put(r, counts.getOrDefault(r, 0) + 1);
                }
            }
        }

        // Sorterar alla skivor efter popularitet
        for (Map.Entry<Record, Integer> entry : counts.entrySet()) {
            int popularity = entry.getValue();
            result.putIfAbsent(popularity, new TreeSet<>(Comparator.comparing(Record::getName)));
            result.get(popularity).add(entry.getKey());
        }

        return result;
    }


    public SortedMap<Integer, Set<Record>> getTop5() {
        Map<Record, Integer> popularityMap = new HashMap<>();

        // Går igenom alla skivor och räknar hur många som äger varje skiva
        for (Node node : graph.getNodes()) {
            if (node instanceof Record) {
                Record record = (Record) node;
                int pop = getPopularity(record);
                popularityMap.put(record, pop);
            }
        }

        // Gruppera skivorna efter popularitet
        SortedMap<Integer, Set<Record>> result = new TreeMap<>(Comparator.reverseOrder());
        for (Map.Entry<Record, Integer> entry : popularityMap.entrySet()) {
            int popularity = entry.getValue();
            result.putIfAbsent(popularity, new HashSet<>());
            result.get(popularity).add(entry.getKey());
        }

        // Plocka ut max 5 (eller färre) nivåer av populära skivor
        SortedMap<Integer, Set<Record>> top5 = new TreeMap<>(Comparator.reverseOrder());
        int count = 0;
        for (Map.Entry<Integer, Set<Record>> entry : result.entrySet()) {
            if (count >= 5) break;
            top5.put(entry.getKey(), entry.getValue());
            count += entry.getValue().size();
        }

        return top5;
    }
}
