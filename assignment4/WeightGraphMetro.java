// $Id: WeightGraphMetro.java
// Modified version of WeightGraph.java, cited here:

// $Id: WeightGraph.java,v 1.1 2006/11/18 01:20:12 jlang Exp $
// CSI2110 Fall 2006 Laboratory 9: Adjacency List and DFS
// ==========================================================================
// (C)opyright:
//
//   Jochen Lang
//   SITE, University of Ottawa
//   800 King Edward Ave.
//   Ottawa, On., K1N 6N5
//   Canada.
//   http://www.site.uottawa.ca
//
// Creator: jlang (Jochen Lang)
// Email:   jlang@site.uottawa.ca
// ==========================================================================
// $Log: WeightGraph.java,v $
// Revision 1.1  2006/11/18 01:20:12  jlang
// Added lab10
//
// Revision 1.1  2006/11/11 03:15:52  jlang
// Added Lab9
//
// Modified by Thais Bardini on November 19th, 2017 (tbard069@uottawa.ca)
// ==========================================================================

// Modified by Anthony Talevi (atale097@uottawa.ca) on November 30th, 2017, for
// CSI2110 Assignment 4, and the storing of metro stations

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

import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

// Modified by Anthony Talevi on November 30th
public class WeightGraphMetro {
	Graph<String, Integer> sGraph;

	Object WEIGHT = new Object();

	//A hash map to act as a station name lookup table.
	Hashtable<String, String> stations;

	Integer numStations;
	Integer numConnections;

	/**
	 * Create a WeightGraph from file
	 * Modified by Thais Bardini on November 19th, 2017 (tbard069@uottawa.ca)
	 */
	public WeightGraphMetro(String fileName) throws Exception, IOException {
		sGraph = new AdjacencyMapGraph<String, Integer>(true);
		stations = new Hashtable<String, String>();
		numStations = 0;
		numConnections = 0;
		read(fileName);
	}

	/**
	 * Read a list of edges from file
	 * Modified by Thais Bardini on November 19th, 2017 (tbard069@uottawa.ca)
	 */
	protected void read(String fileName) throws Exception, IOException {
		BufferedReader graphFile = new BufferedReader(new FileReader(fileName));

		// Create a hash map to store all the vertices read
		Hashtable<String, Vertex> vertices = new Hashtable<String, Vertex>();

		// Read the edges and insert


		String line;

		line = graphFile.readLine();
		//String Tokenizer will iterate through the first line
		StringTokenizer fl = new StringTokenizer(line);

		//Read first line
		numStations = Integer.parseInt(fl.nextToken());
		numConnections = Integer.parseInt(fl.nextToken());


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
	}

	/**
	 * Helper routine to get a Vertex (Position) from a string naming the vertex
	 * Modified by Thais Bardini on November 19th, 2017 (tbard069@uottawa.ca)
	 */
	protected Vertex<String> getVertex(String vert) throws Exception {
		// Go through vertex list to find vertex -- why is this not a map
		for (Vertex<String> vs : sGraph.vertices()) {
			if (vs.getElement().equals(vert)) {
				return vs;
			}
		}
		throw new Exception("Vertex not in graph: " + vert);
	}

	/**
	 * Printing all the vertices in the list, followed by printing all the edges
	 * Modified by Thais Bardini on November 19th, 2017 (tbard069@uottawa.ca)
	 */
	void print() {
		System.out.println("Vertices: " + sGraph.numVertices() + " Edges: " + sGraph.numEdges());

		for (Vertex<String> vs : sGraph.vertices()) {
			System.out.println(vs.getElement());
		}
		for (Edge<Integer> es : sGraph.edges()) {
			System.out.println(es.getElement());
		}
		return;
	}

	/**
	 * Print the shortest distances
	 * Modified by Thais Bardini on November 19th, 2017 (tbard069@uottawa.ca)
	 * @throws Exception
	 */
	void printAllShortestDistances(String vert) throws Exception {
        Vertex<String> vSource = getVertex( vert );

        // Your code here!

        // Find shortest path

        // Print shortest path to named cities

        return;
	}



	/**
	 * Helper method: Read a String representing a vertex from the console
	 */
	public static String readVertex() throws IOException {
		System.out.print("[Input] Vertex: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		return reader.readLine();
	}
}
