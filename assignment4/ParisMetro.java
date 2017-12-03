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


public class ParisMetro{

  WeightGraphMetro parisMetro;
  //Based on the non-static read(String fileName) funciton in WeightGraph
  public static WeightGraphMetro readMetro(String fileName) throws Exception, IOException{

    WeightGraphMetro submap = new WeightGraphMetro(fileName);
    return submap;
  }

  public ParisMetro(String fileName)throws Exception, IOException{
      parisMetro = readMetro(fileName);
      parisMetro.print();
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

}
