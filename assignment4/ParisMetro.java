// Modified by Thais Bardini on November 19th, 2017 (tbard069@uottawa.ca)
// ==========================================================================
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

import net.datastructures.AdjacencyMapGraph;
//import net.datastructures.Dijkstra;
import net.datastructures.Edge;
import net.datastructures.Graph;
import net.datastructures.GraphAlgorithms;
import net.datastructures.Map;
import net.datastructures.Vertex;
//import WeightGraph;

import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import net.datastructures.LinkedQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class ParisMetro{

  Graph<String, Integer> parisMetro;

  //A hash map to act as a station name lookup table.
  Hashtable<String, String> stations;

  //map and set of visited nodes
  Map<Vertex, Vertex> visited;
  Set<Vertex> explored;



  // //Based on the non-static read(String fileName) funciton in WeightGraph
  // public static WeightGraphMetro readMetro(String fileName) throws Exception, IOException{
  //
  //   WeightGraphMetro submap = new WeightGraphMetro(fileName);
  //   return submap;
  // }

  public ParisMetro(String fileName)throws Exception, IOException{
      parisMetro = new AdjacencyMapGraph<String, Integer>(true);
      stations = new Hashtable<String, String>();

      parisMetro = readMetro(fileName, stations);
  }

  public static Graph readMetro(String fileName, Hashtable<String, String> stations) throws Exception, IOException{

    Graph<String, Integer> sGraph = new AdjacencyMapGraph<String, Integer>(true);

    BufferedReader graphFile = new BufferedReader(new FileReader(fileName));

		// Create a hash map to store all the vertices read
		Hashtable<String, Vertex> vertices = new Hashtable<String, Vertex>();

		// Read the edges and insert


		String line;

		line = graphFile.readLine();
		//String Tokenizer will iterate through the first line
		StringTokenizer fl = new StringTokenizer(line);

		//Read first line
		Integer numStations = Integer.parseInt(fl.nextToken());
		Integer numConnections = Integer.parseInt(fl.nextToken());


		//This code will run until end of station declarations ("$")
		while (!(line = graphFile.readLine()).equals("$")) {

			//String Tokenizer will iterate through the line
			StringTokenizer st = new StringTokenizer(line);

			//Read the station number from the .txt
			String stationNum = st.nextToken();
			//Read the station name from the .txt
			String stationName = st.nextToken();

			//Create a new vertex and check for preexistence
			Vertex<String> sv = vertices.get(stationNum);
			if (sv == null) {
				// Source vertex not in graph -- insert
				sv = sGraph.insertVertex(stationNum);
				//Add to list of vertices
				vertices.put(stationNum, sv);
				//Add to lookup table of stations
				stations.put(stationNum, stationName);
			}
		}

		//Continue until end of the file
		while((line = graphFile.readLine())!=null){

			//String Tokenizer will iterate through the line
			StringTokenizer st = new StringTokenizer(line);

			//Read source station
			String source = st.nextToken();

			//Adding correct number of zeroes, as the num codes of the second half
			//are missing the zeroes.
			int zeroes = 4 - source.length()%4;
			String z = "";

			for(int i=0; i<zeroes; ++i){
				z+="0";
			}

			source = z + source;
			//Check if the station exists
			Vertex<String> sv = vertices.get(source);
			if(sv==null){
					throw new IOException("Error: Station does not exist" + line);
			}

			//Read destination station
			String dest = st.nextToken();

			//because the names don't match without the zeroes
			zeroes = 4 - dest.length()%4;
			z = "";
			for(int i=0; i<zeroes; ++i){
				z+="0";
			}
			dest = z + dest;

			//Check if the station exists
			Vertex<String> dv = vertices.get(dest);
			if(dv==null){
				throw new IOException("Error: Station does not exist" + line);
			}
			//Read weight of the edge
			Integer weight = new Integer(st.nextToken());

			// check if edge is already in graph
			if (sGraph.getEdge(sv, dv) == null) {
				// edge not in graph -- add
				//e's element is now the distance between the vertices
				//Modified by Thais Bardini on November 19th, 2017 (tbard069@uottawa.ca)
				Edge<Integer> e = sGraph.insertEdge(sv, dv, weight);
			}

		}
    return sGraph;
  }

  // public void allStationsOnLine(Vertex<String> firstStation){
  //
  //   //To hold all connected stations
  //   List<Vertex<String>> ontheline;
  //
  //   Iterable<Edge<Integer>> directOut = parisMetro.outgoingEdges(firstStation);
  //
  // }

  public static void main(String[] args) throws Exception, IOException{
    try{
      ParisMetro a = new ParisMetro(args[0]);
    } catch(IOException e){
      System.out.println(e.getMessage());
    }
  }

  public void stationsOnLine(Vertex<String> start){
    /* metro.txt file lists multiple vertices per station, representing the
    *  different lines connected to the station.
    *  What this means is that if we ask for all vertices connected to a vertex,
    *  it will only return those on the same line as that
    *  vertex, not necessarily all of the vertices connected to a station.
    */

    //List of all stations
    LinkedQueue<Vertex<String>> stations = new LinkedQueue<Vertex<String>>();
    stations.enqueue(start);

    //Graph is directed
    Iterator<Edge<Integer>> outgoing = (parisMetro.outgoingEdges(start)).iterator();
    Iterator<Edge<Integer>> incoming = (parisMetro.incomingEdges(start)).iterator();

    while(outgoing.hasNext()){}

  }
/**
	 * return the shortest distances
	 * 
	 */
	private Set<Vertex> shortestPaths(Vertex element) {
        List<Vertex> neighbours = getNeighbours(element);
        Integer totalTime;
        int weight;
        for (Vertex tmp : neighbours) {
            if (shortestOnePath(tmp) > (shortestOnePath(element) + getDistance(element, tmp))) {
            	weight = (shortestOnePath(element) + (int)parisMetro.getEdge(element, tmp).getElement());
                totalTime= totalTime + weight;
                visited.put(tmp, element);
                explored.add(tmp);
            }
        }

        System.out.println("Total time = " + weight);
        return explored;
    }

/**
*returns the shortest distances when a given line is not functioning
*
*/
	private Set<Vertex> shortestPaths(Vertex element, Vertex start, Vertex end) {
	    //Iterable<Edge<E>> neighbours = getNeighbors(element);
	    List<Vertex> neighbours = getNeighbours(element);
	    Integer totalTime;
	    int weight;
	    for (Vertex tmp : neighbours) {
	    	if (stationsOnLine(start).vertices().contains(tmp)){
	    		for (Vertex n : getNeighbours(tmp)){
	    			if ((int)parisMetro.getEdge(n,tmp).getElement() == -1){
	    				weight = weight + 90;
	    				visited.put(n, element);
	    				explored.add(n);
	    			}
	    		}
	    	}
	        else if (shortestOnePath(tmp) > (shortestOnePath(element) + getDistance(element, tmp))) {
	        	weight = (shortestOnePath(element)+ getDistance(element, tmp));
	            totalTime= totalTime + weight;
	            explored.add(tmp);
	        }
	    }

	    System.out.println("Total time = " + weight);
        return explored;
}



	/**
	 * Helper method: Read a String representing a vertex from the console
	 */
	public static String readVertex() throws IOException {
		System.out.print("[Input] Vertex: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		return reader.readLine();
	}

	private int getDistance(Vertex node, Vertex element) {
        for (Edge edge : parisMetro.edges()) {
            if (edge.equals(node) && parisMetro.endVertices(edge).equals(element)) {
                return (int)edge.getElement();
            }
        }
        throw new RuntimeException("Should not happen");
    }

    private int shortestOnePath(Vertex v){
    	int d = (int)parisMetro.getEdge(v, parisMetro.opposite(v, (Edge)parisMetro.outgoingEdges(v))).getElement();
        if ((Object)d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    private List<Vertex> getNeighbours(Vertex node) {
        List<Vertex> neighbors = new ArrayList<Vertex>();
        int i = 0;
        for (Edge edge : parisMetro.edges()) {
            if (parisMetro.endVertices(edge)[0].equals(node) || parisMetro.endVertices(edge)[1].equals(node)) {
                neighbors.add(i, parisMetro.opposite(node, (Edge)parisMetro.outgoingEdges(node)));
            }
            i++;
        }
        return neighbors;
    }
}
