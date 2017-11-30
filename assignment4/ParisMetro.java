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

  WeightGraph parisMetro;
  //Based on the non-static read(String fileName) funciton in WeightGraph
  public static WeightGraph readMetro(String fileName) throws Exception, IOException{
    WeightGraph submap = new WeightGraph(fileName);
    return submap;
  }

  public ParisMetro(String fileName)throws Exception, IOException{
    try{
      parisMetro = readMetro(fileName);
    } catch (IOException e){
      System.out.println("File cannot be found");
    }
  }




}
