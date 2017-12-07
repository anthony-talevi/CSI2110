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
import net.datastructures.HeapAdaptablePriorityQueue;
import net.datastructures.LinkedStack;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.datastructures.Entry;


public class ParisMetro{

  Graph<String, Integer> parisMetro;

  //A hash map to act as a station name lookup table.
  Hashtable<String, String> stations;
  // //Based on the non-static read(String fileName) funciton in WeightGraph
  // public static WeightGraphMetro readMetro(String fileName) throws Exception, IOException{
  //
  //   WeightGraphMetro submap = new WeightGraphMetro(fileName);
  //   return submap;
  //

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
      if(args.length == 2){
        System.out.println("N1 = " + args[1]);
        System.out.println("Stations on the line: ");
        HashMap<String, Vertex<String>> st = a.stationsOnLine(args[1]);
        for(Vertex<String> v : st.values()){
          System.out.println(v.getElement() + " ");

        }
      }
      if (args.length == 3){
        System.out.println("N1 = " + args[1]);
        System.out.println("N2 = " + args[2]);
      	a.shortestPath(args[1], args[2]);
      }
      if (args.length == 4){
        System.out.println("N2 = " + args[2]);
      	System.out.println("N1 = " + args[3]);
      	a.shortestPathWithoutLine(args[1], args[2], args[3]);
      }


    } catch(IOException e){
      System.out.println(e.getMessage());
    }
  }

  public HashMap<String, Vertex<String>> stationsOnLine(String start)throws Exception, IOException{

    //Stack holding all stations found to be on the line that need to be checked for neighbours
    LinkedStack<Vertex<String>> toVisit = new LinkedStack<Vertex<String>>();

    // Create a hash map to store all the vertices already visited
    HashMap<String, Vertex<String>> visited = new HashMap<String, Vertex<String>>();

    //First station to visit
    Vertex<String> first;
    try{
      //use getVertex to return a vertex from the passed string
      first = getVertex(start);
    } catch(IOException e){throw new IOException(e);}

    //Push the first station to the stack
    toVisit.push(first);

    //While there are still stations to visit
    while(!toVisit.isEmpty()){
      //Get a station to visit from the stack
      Vertex<String> currVert = toVisit.pop();
      //Return an iter with all its outgoing edges
      Iterator<Edge<Integer>> outgoing = (parisMetro.outgoingEdges(currVert)).iterator();
      //While there are still edges to check
      while(outgoing.hasNext()){
        //Get the next edge
        Edge<Integer> nextEdge = outgoing.next();
        //Stations connected by walking (represented by an edge weight of -1)
        //do not count as on the same line
        if(nextEdge.getElement() > 0){
          //Get the connected station
          Vertex<String> w = parisMetro.opposite(currVert, nextEdge);
          //Make sure the element hasn't already been visited.
          //Some stations have outgoing edges to the station they just came from
          if(!visited.containsKey(w.getElement())){
            //Add the neighbouring station to the stack to be visited later
            toVisit.push(w);
            //Mark the current station as having been visited
            visited.put(currVert.getElement(), currVert);
          }
        }
      }
    }
    //Return a Hashmap of the stations on the line.
    return visited;
  }

  public void shortestPath(String st, String ed) throws Exception, IOException{
    int max = 1000000;          //Max value to act as infinity
    Vertex<String> start;       //Starting station
    Vertex<String> end;         //end station
    try{
      start = getVertex(st);    //Get the vertices from their strings
      end = getVertex(ed);
    } catch(IOException e){throw e;}

    //Hashmap to store distances from vertex
    HashMap<Vertex<String>, Integer> distances = new HashMap();
    //HashMap to store last edge to get to vertex
    HashMap<String, Edge<Integer>> lastAdded = new HashMap();
    //To store vertices that are in the cloud and don't need to be visited again
    HashMap<String, Vertex<String>> doneVisiting = new HashMap();

    //PriorityQueue to store nodes to visit
    HeapAdaptablePriorityQueue<Integer, Vertex<String>> toVisit = new HeapAdaptablePriorityQueue();
    //Can only use PriorityQueue's replaceKey method by passing an Entry class.
    //This HashMap stores entries that can be looked up by station number
    HashMap<String, Entry<Integer, Vertex<String>>> entries = new HashMap<String, Entry<Integer, Vertex<String>>>();

    distances.put(start, 0);        //Distance from start to start is 0
    for(Vertex<String> v : parisMetro.vertices()){
      if(v != start){
        distances.put(v, max);     //Using max value to indicate infinity
      }
      //Insert all nodes into the priority queue, and storing the returned
      //Entry objects in entries.
      entries.put(v.getElement(), toVisit.insert((distances.get(v)), v));
    }

    //While their are still vertices outside of the cloud
    while(!toVisit.isEmpty()){
      //Station in as an entry
      Entry<Integer, Vertex<String>> ent = toVisit.removeMin();
      //Station as a vertex
      Vertex<String> v = ent.getValue();
      //For all stations connected to this station
      for(Edge<Integer> e : parisMetro.outgoingEdges(v)){
        //Get the station's neighbour for along this edge
        Vertex<String> neighbour = parisMetro.opposite(v, e);
        //Is this station already in the cloud?
        if(!doneVisiting.containsKey(neighbour.getElement())){
            int path;   //Distance to start
            //If a walking edge, its distance is 90
            if(e.getElement() == -1){
              path = distances.get(v) + 90;
            }
            ///Else add the edge's weight
            else{
              path = distances.get(v) + e.getElement();
            }
            //If the path is shorter than the existing path, update it
            if(path < distances.get(neighbour)){
              //Get the Entry Object of the neighbouring station
              Entry<Integer, Vertex<String>> toReplace = entries.get(neighbour.getElement());
              //Remove it from the Priority queue
              toVisit.remove(toReplace);
              //Reinsert it with the updated priority
              toReplace = toVisit.insert(path, neighbour);
              //Update the Entry object in entries to reflect new priority
              entries.put(neighbour.getElement(), toReplace);
              //Update the distance from start
              distances.put(neighbour, path);
              //Add last traveled edge to lastAdded
              lastAdded.put(neighbour.getElement(), e);
            }
        }
      }
      //Pull this station into the cloud
      doneVisiting.put(v.getElement(), v);


    }
    //Reconstruct shortest path, working backwards
    //using a stack so it will print out in order
    LinkedStack<Vertex<String>> shortest = new LinkedStack<Vertex<String>>();
    Vertex<String> current = end;
    //Work backwards from the end node
    while(current!=start){
      //Add the current station to the stack
      shortest.push(current);
      //Get the last visited edge to get to this vertice by the shortest path
      //algorithm from the stating station
      Edge<Integer> last = lastAdded.get(current.getElement());
      //Get the station on the opposite side of the edge
      Vertex<String>[] endVerts = parisMetro.endVertices(last);
      current = endVerts[0];
    }
    //Add the start station to the stack
    shortest.push(start);
    //Printing
    System.out.println("Time: " + distances.get(end));
    System.out.println("Stations to visit from top to bottom: ");
    while(!shortest.isEmpty()){
      String station = shortest.pop().getElement();
      System.out.println(station + "    " + stations.get(station));
    }
  }

  public void shortestPathWithoutLine(String st, String ed, String endpoint) throws Exception, IOException{

      //uses same algorithm as shortestPath()
      //Calls stationsOnLine() on the station of the broken line.
      //If a station being visited by the algorithm exists on the broken line,
      //its distance is left as infinity.  If the algorithm returns a distance
      //of infinity, then there are no ways of getting to the station via other
      //stations, and the person must wa

      HashMap<String, Vertex<String>> brokenStations = new HashMap<String, Vertex<String>>();
      try{
        brokenStations = stationsOnLine(endpoint);
      }catch(IOException e){throw e;}

      int max = 1000000;          //Max value to act as infinity
      Vertex<String> start;       //Starting station
      Vertex<String> end;         //end station
      try{
        start = getVertex(st);    //Get the vertices from their strings
        end = getVertex(ed);
      } catch(IOException e){throw e;}

      //Hashmap to store distances from vertex
      HashMap<Vertex<String>, Integer> distances = new HashMap();
      //HashMap to store last edge to get to vertex
      HashMap<String, Edge<Integer>> lastAdded = new HashMap();
      //To store vertices that are in the cloud and don't need to be visited again
      HashMap<String, Vertex<String>> doneVisiting = new HashMap();

      //PriorityQueue to store nodes to visit
      HeapAdaptablePriorityQueue<Integer, Vertex<String>> toVisit = new HeapAdaptablePriorityQueue();
      //Can only use PriorityQueue's replaceKey method by passing an Entry class.
      //This HashMap stores entries that can be looked up by station number
      HashMap<String, Entry<Integer, Vertex<String>>> entries = new HashMap<String, Entry<Integer, Vertex<String>>>();

      distances.put(start, 0);        //Distance from start to start is 0
      for(Vertex<String> v : parisMetro.vertices()){
        if(v != start){
          distances.put(v, max);     //Using max value to indicate infinity
        }
        //Insert all nodes into the priority queue, and storing the returned
        //Entry objects in entries.
        entries.put(v.getElement(), toVisit.insert((distances.get(v)), v));
      }

      //While their are still vertices outside of the cloud
      while(!toVisit.isEmpty()){
        //Station in as an entry
        Entry<Integer, Vertex<String>> ent = toVisit.removeMin();
        //Station as a vertex
        Vertex<String> v = ent.getValue();
        //If this station is on the broken line, skip it
        if(!brokenStations.containsKey(v.getElement())){
          //For all stations connected to this station
          for(Edge<Integer> e : parisMetro.outgoingEdges(v)){
            //Get the station's neighbour for along this edge
            Vertex<String> neighbour = parisMetro.opposite(v, e);
            //Is this station already in the cloud?
            if(!doneVisiting.containsKey(neighbour.getElement())){
                int path;   //Distance to start
                //If a walking edge, its distance is 90
                if(e.getElement() == -1){
                  path = distances.get(v) + 90;
                }
                ///Else add the edge's weight
                else{
                  path = distances.get(v) + e.getElement();
                }
                //If the path is shorter than the existing path, update it
                if(path < distances.get(neighbour)){
                  //Get the Entry Object of the neighbouring station
                  Entry<Integer, Vertex<String>> toReplace = entries.get(neighbour.getElement());
                  //Remove it from the Priority queue
                  toVisit.remove(toReplace);
                  //Reinsert it with the updated priority
                  toReplace = toVisit.insert(path, neighbour);
                  //Update the Entry object in entries to reflect new priority
                  entries.put(neighbour.getElement(), toReplace);
                  //Update the distance from start
                  distances.put(neighbour, path);
                  //Add last traveled edge to lastAdded
                  lastAdded.put(neighbour.getElement(), e);
                }
            }
          }
        }
        //Pull this station into the cloud
        doneVisiting.put(v.getElement(), v);


      }
      //Reconstruct shortest path, working backwards
      //using a stack so it will print out in order
      LinkedStack<Vertex<String>> shortest = new LinkedStack<Vertex<String>>();
      Vertex<String> current = end;
      //Work backwards from the end node
      while(current!=start){
        //Add the current station to the stack
        shortest.push(current);
        //Get the last visited edge to get to this vertice by the shortest path
        //algorithm from the stating station
        Edge<Integer> last = lastAdded.get(current.getElement());
        //Get the station on the opposite side of the edge
        Vertex<String>[] endVerts = parisMetro.endVertices(last);
        current = endVerts[0];
      }
      //Add the start station to the stack
      shortest.push(start);
      //Printing
      System.out.println("Time: " + distances.get(end));
      System.out.println("Stations to visit from top to bottom: ");
      while(!shortest.isEmpty()){
        String station = shortest.pop().getElement();
        System.out.println(station + "    " + stations.get(station));
      }
  }

  private Vertex<String> getVertex(String s) throws IOException{
    for(Vertex<String> v : parisMetro.vertices()){
      if (v.getElement().equals(s)) return v;
    }
    throw new IOException ("Not a valid station");
  }
}
