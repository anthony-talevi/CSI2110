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

import java.util.LinkedHashMap;
import java.util.Collection;
import net.datastructures.LinkedStack;


public class ParisMetro{

  Graph<String, Integer> parisMetro;

  //A hash map to act as a station name lookup table.
  Hashtable<String, String> stations;


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

  public static void main(String[] args) throws Exception, IOException{
    try{
      ParisMetro a = new ParisMetro(args[0]);
      if(args.length>1){
        String vert = args[1];
        LinkedHashMap<String, Vertex<String>> line = a.stationsOnLine(vert);
        Collection<Vertex<String>> col = line.values();
        Iterator<Vertex<String>> iter = col.iterator();
        String s = "";
        while(iter.hasNext()){
           s += iter.next().getElement();
           s += " ";
           iter.remove();
        }
        System.out.println(s);
        //System.out.println(line.toString());
      }
    } catch(IOException e){
      System.out.println(e.getMessage());
    }
  }

  public LinkedHashMap<String, Vertex<String>> stationsOnLine(String start)throws Exception, IOException{
    //Stack holding all
    LinkedStack<Vertex<String>> toVisit = new LinkedStack<Vertex<String>>();
    // Create a hash map to store all the vertices already visited
    LinkedHashMap<String, Vertex<String>> visited = new LinkedHashMap<String, Vertex<String>>();

    Vertex<String> first;
    try{
      first = getVertex(start);
    } catch(IOException e){throw new IOException(e);}

      toVisit.push(first);
      visited.put(start, first);S


    //While there are still nodes to visit
    while(!toVisit.isEmpty()){
      Vertex<String> currVert = toVisit.pop();        //Get the next element to visit
      Iterator<Edge<Integer>> outgoing = (parisMetro.outgoingEdges(currVert)).iterator();       //Return an iter with all outgoing edges
      while(outgoing.hasNext()){        //while iter still has elements
        Edge<Integer> nextEdge = outgoing.next();     //Get the next element
        if(nextEdge.getElement() > 0){
          Vertex<String> w = parisMetro.opposite(currVert, nextEdge);
          if(!visited.containsKey(w.getElement())){    //If the element hasn't already been visited
            toVisit.push(w);
            visited.put(w.getElement(), w);
          }
        }
      }
    }
    return visited;
  }

  //Needs to be cited
  private Vertex<String> getVertex(String s) throws Exception{
      for(Vertex<String> vs : parisMetro.vertices()){
        if((vs.getElement()).equals(s)){
          return vs;
        }
      }throw new IOException ("Vertex not in graph: " + s);
  }
}
