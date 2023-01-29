package org.kclhi.hands.utility;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfree.chart.JFreeChart;
import org.jgrapht.EdgeFactory;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jibble.epsgraphics.EpsGraphics2D;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.kclhi.hands.utility.output.Serializer;

import org.kclhi.hands.utility.output.HiderRecord;
import org.kclhi.hands.utility.output.TraverserRecord;
import org.kclhi.hands.GraphTraverser;
import org.kclhi.hands.graph.HiddenObjectGraph;
import org.kclhi.hands.graph.StringEdge;
import org.kclhi.hands.graph.StringVertex;

/**
 * A set of static utility methods.
 * 
 * @author Martin
 *
 */
public class Utils {

	/**
	 * 
	 */
	public final static String FILEPREFIX = "output/";
	
	/**
	 * 
	 */
	public final static String MARTINPREFIX = "/Users/Martin/Dropbox/workspace/SearchGames/";
	
	/**
	 * 
	 */
	public static boolean DEBUG = false;
	
	/**
	 * 
	 */
	public static boolean MEMORY_CHECK = false;
	
	/**
	 * 
	 */
	public static String KEY = "trFdcuAh"; 
	
	/**
	 * @param cacheSizeInGB
	 * @return
	 */
	public static <K, V> Pair<DB, BTreeMap<K,V>> getDBAndCache(double cacheSizeInGB) {
		
		//first create store
	    DB db = DBMaker
	            .memoryDirectDB()
	             // make it faster
	            .transactionDisable()
	            .asyncWriteFlushDelay(100)
	            //some additional options for DB
	            .asyncWriteEnable()
	            .cacheSize(1000000000)
	            .closeOnJvmShutdown()
	            //.compressionEnable()
	            .make();
	    
	    BTreeMap<K, V> cache = db
	            .treeMapCreate("cache")
	            //.expireStoreSize(cacheSizeInGB)
	            .counterEnable() //disable this if cache.size() is not used
	            //use proper serializers to and improve performance
	            .keySerializer(Serializer.INTEGER)
	            .valueSerializer(Serializer.FASTJAVA)
	            .make();
	    
		return new Pair<DB, BTreeMap<K, V>>(db, cache);
		
	}
	
	/**
	 * @param byteStrings
	 * @return
	 */
	public static String[] convertToStrings(byte[][] byteStrings) {
	    String[] data = new String[byteStrings.length];
	    for (int i = 0; i < byteStrings.length; i++) {
	        data[i] = new String(byteStrings[i], Charset.defaultCharset());

	    }
	    return data;
	}

	/**
	 * @param strings
	 * @return
	 */
	public static byte[][] convertToBytes(String[] strings) {
	    byte[][] data = new byte[strings.length][];
	    for (int i = 0; i < strings.length; i++) {
	        String string = strings[i];
	        data[i] = string.getBytes(Charset.defaultCharset()); // you can chose charset
	    }
	    return data;
	}
	
	/**
	 * @param path
	 */
	public static void deleteFile(Path path) {
		
		try {
		    
			Files.delete(path);
		    
		} catch (NoSuchFileException x) {
		    
			System.err.format("%s: no such" + " file or directory%n", path);
		
		} catch (DirectoryNotEmptyException x) {
		
			System.err.format("%s not empty%n", path);
		
		} catch (IOException x) {
		
			// File permission problems are caught here.
		    System.err.println(x);
		}
	
	}
	
	/**
	 * @param folder
	 */
	public static ArrayList<Path> listFilesForFolder(final File folder) {
		
		ArrayList<Path> files = new ArrayList<Path>();
		
		if ( !folder.exists() ) return files;
		
	    for (final File fileEntry : folder.listFiles()) {
	    	
	        if (!fileEntry.isDirectory()) {
	        
	        	files.add(Paths.get(fileEntry.getAbsolutePath()));
	        
	        }
	        
	    }
	    
	    return files;
	    
	}
	
	/**
	 * @param path
	 * @param files
	 * @return
	 */
	public static ArrayList<Path> listFilesForFolder( final File folder, ArrayList<Path> files ) {

        if (folder.listFiles() == null) return null;

        for ( final File fileEntry : folder.listFiles() ) {
        	
        	files.add(Paths.get(fileEntry.getAbsolutePath()));
            
        	if ( fileEntry.isDirectory() ) {
            	
            	listFilesForFolder( fileEntry, files );
             
            } else {
            
            	if (!files.contains(Paths.get(fileEntry.getAbsolutePath()))) files.add(Paths.get(fileEntry.getAbsolutePath()));
        
            }
       
        }
        
        return files;
        
    }
	
	/**
	 * @param sourceFile
	 * @param destFile
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    
		if ( !destFile.exists() ) destFile.createNewFile();

	    FileChannel source = null;
	    
	    FileChannel destination = null;
	    
	    try {
	    
	    	source = new FileInputStream(sourceFile).getChannel();
	    	
	        destination = new FileOutputStream(destFile).getChannel();
	        
	        destination.transferFrom(source, 0, source.size());
	    
	    } finally {
	    	
	        if ( source != null ) source.close();
	        
	        if ( destination != null ) destination.close();
	        
	    }
	    
	}
	
	/**
	 * @param integers
	 * @return
	 */
	public static int[] convertIntegers(List<Integer> integers)
	{
	    int[] ret = new int[integers.size()];
	    Iterator<Integer> iterator = integers.iterator();
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = iterator.next().intValue();
	    }
	    return ret;
	}
	
	/**
	 * @param number
	 * @return
	 */
	public static String traverserNumberToWord(int number) {
		
		if (number == 2) return "two ";
		if (number == 3) return "three ";
		if (number == 4) return "four ";
		
		return "";
			
	}
	
	/**
	 * @return
	 */
	public static String timestamp() {
		
		return new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		
	}
	
	/**
	 * @param hiddenObjectGraph
	 * @return
	 */
	public static double totalEdgeCost(HiddenObjectGraph<StringVertex, StringEdge> hiddenObjectGraph) {
	
		double totalCost = 0.0;
		
		for ( StringEdge edge : hiddenObjectGraph.edgeSet() ) {
			
			totalCost += edge.getWeight();
			
		}
		
		return totalCost;
		
	}
	
	/**
	 * @param traverserRecords
	 * @return
	 */
	public static ArrayList<TraverserRecord> expandTraverserRecords( ArrayList<TraverserRecord> traverserRecords ) {
		
		ArrayList<TraverserRecord> localTraverserRecords = new ArrayList<TraverserRecord>();
		
		for ( TraverserRecord traverserRecord : traverserRecords ) {
			
			localTraverserRecords.add(traverserRecord);
			
			if ( traverserRecord instanceof HiderRecord ) {
				
				localTraverserRecords.addAll(((HiderRecord)traverserRecord).getSeekersAndAttributes());
				
			} 
			
		}
		
		return localTraverserRecords;
			
	}
	
	/**
	 * @param list
	 * @return
	 */
	public static <E> String listToProse(ArrayList<E> list, String prefix, String suffix) {
		
		String textList = "";
		
		for ( int i = 0; i < list.size(); i++ ) {
			
			if ( i == list.size() - 1 ) {
				
				textList = textList.substring(0, textList.length() - 2) + " ";
				
				textList += "and " + prefix + list.get(i) + suffix;
				
			} else {
				
				textList += prefix + list.get(i) + suffix + ", ";
			}
			
		}
		
		return textList;
		
	}

	/**
	 * @param name
	 * @return
	 */
	public final static String shortenOutputName(String name) {
		
		Hashtable<String, String> shortenedNames = new Hashtable<String, String>();
		
		//
		
		shortenedNames.put("sDepthFirstSearch", "sDFS");
		shortenedNames.put("sDepthFirstSearchGreedy", "sDFSGreedy");
		shortenedNames.put("sBreadthFirstSearch", "sBFS");
		shortenedNames.put("sBreadthFirstSearchGreedy", "sBFSGreedy");
		shortenedNames.put("sSelfAvoidingRandomWalk", "sRandomWalk");
		shortenedNames.put("hNotConnecting", "hNotConnected");
		shortenedNames.put("hUniqueRandomSetRepeat", "hUniqueRandomSet");
		shortenedNames.put("hStaticLocations", "hAllLocations");
		shortenedNames.put("hVariableGraphKnowledgeMaxDistance", "hMaxDistance");
		shortenedNames.put("hVariableGraphKnowledgeLeastConnected", "hLeastConnected");
		
		//
		
		if (shortenedNames.containsKey(name)) { 
			
			return shortenedNames.get(name);
		
		} else {
			
			return name;
			
		}
		
	}
	
	/**
	 * @return
	 */
	public final static double percentageChange(double A, double B) {
		
		talk("", "Percentage change from " + A + " to " + B);
		
		return ( ( B - A ) / ( Math.abs(A) ) ) * 100;
		
	}
	
	/**
	 * @param command
	 */
	public final static void runCommand(String command) {
		
		runCommandWithReturn(command);
		
	}
	
	/**
	 * 
	 */
	public final static ArrayList<String> runCommandWithReturn(String command) {
		
		ArrayList<String> returnLines = new ArrayList<String>();
		
		Process proc = null;
		
		try {
			
			proc = Runtime.getRuntime().exec(command);
		
		} catch (IOException e1) {
			
			e1.printStackTrace();
		
		}
		
		BufferedReader outputs = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		
		BufferedReader errors = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
		  
		String line = null;  
		 
		try {
			
			while ((line = outputs.readLine()) != null) {  
			
				returnLines.add(line);  
				System.out.println(line);
			
			}
			
		} catch (IOException e1) {
			
			e1.printStackTrace();
		
		}
		
		try {
		
			while ((line = errors.readLine()) != null) {  
				
				returnLines.add(line); 
				System.out.println(line);
			
			}
			
		} catch (IOException e1) {
			
			e1.printStackTrace();
		
		} 
		  
		try {
		
			proc.waitFor();
		
		} catch (InterruptedException e) { System.out.println(e); returnLines.add(e.getMessage()); }
	    
		return returnLines;
		
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	/**
	 * @param traverser
	 * @return
	 */
	public static String shortenTraverserName(GraphTraverser traverser) {
		
		if (traverser.toString().contains(" ")) { 
			
			return traverser.toString().substring(0, traverser.toString().indexOf(" "));
		
		} else {
			
			return traverser.toString();
			
		}
		
	}
	
	/**
	 * @param map
	 * @return
	 */
	public static <A, B> LinkedHashMap<A,B> manualReverse(LinkedHashMap<A, B> map) {
	
		List<Entry<A,B>> list = new ArrayList<Entry<A, B>>(map.entrySet());
	
		map.clear();
		
		for ( int i = list.size() - 1; i >= 0; i-- ){
			
		    map.put(list.get(i).getKey(), list.get(i).getValue());
		
		}
		
		return map;
	
	}

	/**
	 * 
	 */
	public static void printSystemStats() {
		
		printSystemStats("");
		
	}
	
	/**
	 * 
	 */
	public static void printSystemStats(String monitoredSize) {
		
		if ( !MEMORY_CHECK ) return;
		
		System.out.print("\r[");
		
		 /* Total number of processors or cores available to the JVM */
	    // System.out.println("Available processors (cores): " +  Runtime.getRuntime().availableProcessors());

	    /* Total amount of free memory available to the JVM */
	    System.out.print("Free memory (bytes): " + Runtime.getRuntime().freeMemory() + " [ Monitored size: " + monitoredSize + " ]");

	    /* This will return Long.MAX_VALUE if there is no preset limit */
	    // long maxMemory = Runtime.getRuntime().maxMemory();
	    /* Maximum amount of memory the JVM will attempt to use */
	    // System.out.println("Maximum memory (bytes): " + (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

	    /* Total memory currently available to the JVM */
	    // System.out.println("Total memory available to JVM (bytes): " + Runtime.getRuntime().totalMemory());

	    /* Get a list of all filesystem roots on this system */
	    // File[] roots = File.listRoots();

	    /* For each filesystem root, print some info */
	    /* for (File root : roots) {
	      System.out.println("File system root: " + root.getAbsolutePath());
	      System.out.println("Total space (bytes): " + root.getTotalSpace());
	      System.out.println("Free space (bytes): " + root.getFreeSpace());
	      System.out.println("Usable space (bytes): " + root.getUsableSpace());
	    }*/
	    
	    System.out.print("]");
		
	}
	/**
	 * /http://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
	 * 
	 * @param unsortMap
	 * @param order
	 * @return
	 */
	public static <K, V extends Number> Map<K, V> sortByValue(Map<K, V> unsortedMap, final boolean order) {

        List<Entry<K, V>> list = new LinkedList<Entry<K, V>>(unsortedMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<K, V>>() {
        	
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
            	
                if (order) {
                
                	return new Double(o1.getValue().doubleValue()).compareTo(new Double(o2.getValue().doubleValue()));
                
                } else {
                    
                	return new Double(o2.getValue().doubleValue()).compareTo(new Double(o1.getValue().doubleValue()));

                }
                
            }
            
        });

        // Maintaining insertion order with the help of LinkedList
        Map<K, V> sortedMap = new LinkedHashMap<K, V>();
        
        for (Entry<K, V> entry : list) {
        	
            sortedMap.put(entry.getKey(), entry.getValue());
        
        }

        return sortedMap;
        
    }
	
	/**
	 * @param filePath
	 * @param ftpUrl
	 * @param user
	 * @param pass
	 * @param host
	 * @param uploadPath
	 */
	public static void uploadToFTP(String filePath, String ftpUrl, String user, String pass, String host, String uploadPath) {
		
		ftpUrl = String.format(ftpUrl, user, pass, host, uploadPath);
 
        try {
        	
            URL url = new URL(ftpUrl);
            
            URLConnection conn = url.openConnection();
            
            OutputStream outputStream = conn.getOutputStream();
            
            FileInputStream inputStream = new FileInputStream(filePath);
 
            byte[] buffer = new byte[4096];
            
            int bytesRead = -1;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
            
            	outputStream.write(buffer, 0, bytesRead);
            
            }
 
            inputStream.close();
            
            outputStream.close();
            
        } catch (IOException ex) {
            
        	ex.printStackTrace();
        
        }
        
	}
	
	/**
	 * @param url
	 * @return
	 */
	public static String readFirstLineFromFile(String url) {
		
		BufferedReader br = null;
	       
  		try {
   
  			String sCurrentLine;
   
  			br = new BufferedReader(new FileReader(url));
   
  			while ((sCurrentLine = br.readLine()) != null) {
  				
  				return sCurrentLine;
  				
  			}
   
  		} catch (IOException e) {
  			
  			e.printStackTrace();
  		
  		} finally {
  		
  			try {
  			
  				if (br != null) br.close();
  			
  			} catch (IOException ex) {
  			
  				ex.printStackTrace();
  			
  			}
  		
  		}
  		
  		return "";
  		
	}
	
	/**
	 * @param url
	 * @return
	 */
	public static ArrayList<String> readFromFile(String url) {
		
		ArrayList<String> lines = new ArrayList<String>();
		
		BufferedReader br = null;
	       
  		try {
   
  			String sCurrentLine;
   
  			br = new BufferedReader(new FileReader(url));
   
  			while ((sCurrentLine = br.readLine()) != null) {
  				
  				lines.add(sCurrentLine);
  				
  			}
   
  		} catch (IOException e) {
  			
  			e.printStackTrace();
  		
  		} finally {
  		
  			try {
  			
  				if (br != null) br.close();
  			
  			} catch (IOException ex) {
  			
  				ex.printStackTrace();
  			
  			}
  		
  		}
  		
  		return lines;
  		
	}
	
	/**
	 * 
	 */
	public static void clearFile(String URL) {
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(URL));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		writer.print("");
		writer.close();
		
	}
	
	/**
	 * @param url
	 * @param content
	 */
	public static void writeToFile(String url, String content) {
		
		FileWriter writer = null;
		
		try {
			
			writer = new FileWriter(url, false);
		
		} catch (IOException e) { 
			
			e.printStackTrace();
			
		}
		
		try {
	
			writer.append(content);
	
		    writer.flush();
		
		} catch(IOException e) {
			
			System.err.println(e.getMessage());
		
		}
		
	}
	
	/**
	 * @param name
	 * @param chart
	 * @param x
	 * @param y
	 */
	public static void exportAsEPS(String url, JFreeChart chart, int x, int y) {
		
        Graphics2D g = new EpsGraphics2D();
        
        chart.setTitle("");
        
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        chart.draw(g, new Rectangle(x,y));
        
        FileWriter writer = null;
		
		try {
			
			writer = new FileWriter(url, false);
		
		} catch (IOException e) { 
			
			e.printStackTrace();
			
		}
		
		try {
	
			writer.write(g.toString());
	
		    writer.flush();
		    
		    writer.close();
		
		} catch(IOException e) {
			
			System.err.println(e.getMessage());
		
		}
	
	}
	
	/**
	 * @param writer
	 * @param content
	 */
	public static void writeToFile(FileWriter writer, String content) {
		
		try {
	
			writer.append(content);
	
		    writer.flush();
		
		} catch(IOException e) {
			
			System.err.println(e.getMessage());
			
		}
		
	}
	
	/**
	 * @param stringArrayFormat
	 * @param regexpStr
	 * @return
	 */
	public static ArrayList<Pair<String, String>> stringToArray(String stringArrayFormat, String regexpStr) {
    	
    	ArrayList<Pair<String, String>> seekers = new ArrayList<Pair<String, String>>();

        Pattern regexp = Pattern.compile(regexpStr);
        Matcher matcher = regexp.matcher(stringArrayFormat);
        
        while (matcher.find()) {
        
        	MatchResult result = matcher.toMatchResult();

            String type = result.group(2);
            String number = result.group(3);

            Pair<String, String> seeker = new Pair<String, String>(type, number);
            seekers.add(seeker);
        
        }
        
        return seekers;
    	
    }

	/**
	 * @param speaker
	 * @param message
	 */
	public static void talk(String speaker, String message) {
		
		if (DEBUG) System.out.println(speaker + ": " + message);
		
	}	
	
	/**
	 * @param inputStr
	 * @param patternStr
	 * @return
	 */
	public static int startIndexOf(CharSequence inputStr, String patternStr) {
		
	    Pattern pattern = Pattern.compile(patternStr);
	    
	    Matcher matcher = pattern.matcher(inputStr);
	    
	    if( matcher.find() ){

	    	return matcher.start();
	    
	    }
	    
	    return -1;
		
	}
	
	/**
	 * @param inputStr
	 * @param patternStr
	 * @return
	 */
	public static int endIndexOf(CharSequence inputStr, String patternStr) {
		
	    Pattern pattern = Pattern.compile(patternStr);
	    
	    Matcher matcher = pattern.matcher(inputStr);
	    
	    if( matcher.find() ){

	    	return matcher.end();
	    
	    }
	    
	    return -1;
		
	}
	
	/**
	 * Although an existing diameter value can be obtained from the
	 * FWSP, this is in terms of edge weights. This finds the number
	 * of vertices in the greatest path, as an idea of the number
	 * of hops to achieve the max diameter.
	 * 
	 * Could return a rough estimate, for slightly less clear information.
	 * @param <V>
	 * 
	 * @return
	 */
	public static <V, E extends DefaultWeightedEdge> int graphDiameter(final HiddenObjectGraph<V, E> graph) {
		
		// ~MDC 5/4 Over-complicated as an exercise
		HiddenObjectGraph<V, E> localGraph = new HiddenObjectGraph<V, E>(new EdgeFactory<V, E>() {

			@Override
			public E createEdge(V arg0, V arg1) {

				return graph.getEdgeFactory().createEdge(arg0, arg1);
			
			}
			
		});
		
		// Update the local graph from the current node as the Seeker moves
		for ( V sourceVertex : graph.vertexSet() ) {
			
			for ( V targetVertex : graph.vertexSet() ) {
				
				if ( graph.containsEdge(sourceVertex, targetVertex) ) {
					
					localGraph.addVertexIfNonExistent(sourceVertex);
					localGraph.addVertexIfNonExistent(targetVertex);
					
					localGraph.addEdgeWithWeight(sourceVertex, targetVertex, graph.getEdgeWeight(graph.getEdge(sourceVertex, targetVertex)));
					
				}
				
			}
			
		}
				
		FloydWarshallShortestPaths<V, E> FWSP = new FloydWarshallShortestPaths<V, E>(localGraph);
	
		for (GraphPath<V, E> GP : FWSP.getShortestPaths()) {
			
			// Return the length of the path with the greatest weight
			if (GP.getWeight() == FWSP.getDiameter()) return GP.getEdgeList().size();
			
		}
		
		return -1;
		
	}
	
	/**
	 * Given a map, where the value requires instantiation if
	 * currently empty, then this will instantiate, otherwise
	 * add.
	 * 
	 * @param table
	 * @param key
	 * @param value
	 * @param emptyInstance
	 */
	public static <K, V, E extends AbstractCollection<V>> void add( Map<K, E> table, K key, V value, E emptyInstance, boolean unique ) {
		
		if ( table.containsKey(key) ) {
			
			if ( unique ) {
				
				if ( !table.get(key).contains(value) ) table.get(key).add(value);
				
			} else {
				
				table.get(key).add(value);
				
			}
			
		} else {
			
			emptyInstance.add(value);
			
			table.put(key, emptyInstance);
			
		}
		
	}
	
	/**
	 * All possible combinations from N lists.
	 * 
	 * @param lists
	 * @return
	 */
	public static <V> ArrayList<ArrayList<V>> combinations(ArrayList<ArrayList<V>> lists) {
		
		int[] indices = new int[lists.size()];
		
		int maxListSize = Integer.MIN_VALUE;
		
		for ( ArrayList<V> list : lists ) {
			
			if ( list.size() > maxListSize ) maxListSize = list.size();
			
		}
		
		ArrayList<ArrayList<V>> combinations = new ArrayList<ArrayList<V>>();
		
		do {
		    
			ArrayList<V> combination = new ArrayList<V>();
			
			for ( int i = 0; i < indices.length; i++ ) {
				
				int index = indices[i];
				
				if ( index >= lists.get(i).size() ) index = lists.get(i).size() - 1;
				
				combination.add(lists.get(i).get(index));
				
			}
			
			if ( !combinations.contains(combination) ) combinations.add(combination);
			
			Utils.advanceIndices( indices, lists.size(), maxListSize );
	        
		} while ( !Utils.allMaxed( indices, lists.size(), maxListSize  ) );
		
		return combinations;
		
	}
	
	/**
	 * Ancillary method for combinations
	 * 
	 * @param indices
	 * @param n
	 * @param max
	 */
	public static void advanceIndices( int[] indices, int n, int max ) {

        for ( int i = n - 1; i >= 0; i-- ) {
        	
            if ( indices[i] + 1 == max ) {
            
            	indices[i] = 0;
            	
            } else {
            
            	indices[i]++;
            	
            	break;
            
            }
            
        }

    }
	
	/**
	 * Ancillary method for combinations
	 * 
	 * @param indices
	 * @param n
	 * @param max
	 * @return
	 */
	public static boolean allMaxed( int[] indices, int n, int max ) {

        for ( int i = n - 1; i >= 0; i-- ) {
        	
            if ( indices[i] != max - 1 ) {
            
            	return false;
            
            }
        
        }
        
        return true;

    }

}
