package org.kclhi.hands.utility.GameTheoretic;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.kclhi.hands.utility.ComparatorResult;
import org.kclhi.hands.utility.Utils;

/**
 * NB: Relies on the lrs_nash C++ library.
 * 
 * @author Martin
 *
 */
public class ApproximatePayoffMatrix {

	/**
	 * @author Martin
	 *
	 */
	private class StrategyPayoff implements Comparable<StrategyPayoff> {
		
		/**
		 * 
		 */
		private String strategyPlayed;
		
		/**
		 * @return
		 */
		public String getStrategyPlayed() {
			
			return strategyPlayed;
		
		}

		/**
		 * 
		 */
		private TreeMap<String, Double> payoffAgainstOpponent;
		
		/**
		 * 
		 */
		public final TreeMap<String, Double> opponentPayoffs() {
			
			return payoffAgainstOpponent;
			
		}
		
		/**
		 * @param opponent
		 */
		public double getOpponentPayoff(String opponentStrategy) {
			
			return payoffAgainstOpponent.get(opponentStrategy);
			
		}
		
		public StrategyPayoff(String strategyPlayed) {
			
			this(strategyPlayed, "", 0.0);
			
		}
		
		/**
		 * @param strategyPlayed
		 * @param opponentStrategy
		 * @param payoff
		 */
		public StrategyPayoff(String strategyPlayed, String opponentStrategy, double payoff) {
			
			this.strategyPlayed = strategyPlayed;
			
			payoffAgainstOpponent = new TreeMap<String, Double>();
			
			addPayoff(opponentStrategy, Utils.round(payoff, 2));
			
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime
					* result
					+ ((strategyPlayed == null) ? 0 : strategyPlayed.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			StrategyPayoff other = (StrategyPayoff) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (strategyPlayed == null) {
				if (other.strategyPlayed != null)
					return false;
			} else if (!strategyPlayed.equals(other.strategyPlayed))
				return false;
			return true;
		}

		/**
		 * @param opponentStrategy
		 * @param payoff
		 */
		public void addPayoff(String opponentStrategy, double payoff) {
			
			payoffAgainstOpponent.put(opponentStrategy, Utils.round(payoff, 2));
			
		}
		
		public String toString() {
			
			return strategyPlayed;
			
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(StrategyPayoff o) {
			
			if ( getStrategyPlayed().compareTo(o.getStrategyPlayed() ) > ComparatorResult.EQUAL ) {
				
				return ComparatorResult.AFTER;
				
			} else if ( getStrategyPlayed().compareTo(o.getStrategyPlayed() ) < ComparatorResult.EQUAL ) {
				
				return ComparatorResult.BEFORE;
				
			} else {
				
				return ComparatorResult.EQUAL;
				
			}
					
		}

		private ApproximatePayoffMatrix getOuterType() {
			return ApproximatePayoffMatrix.this;
		}
		
	}
	
	/**
	 * 
	 */
	private String title;
	
	/**
	 * 
	 */
	private TreeMap<String, ArrayList<StrategyPayoff>> playerToStrategies;
	
	/**
	 * @param title
	 */
	public ApproximatePayoffMatrix(String title) {
		
		this.title = title;
		
		playerToStrategies = new TreeMap<String, ArrayList<StrategyPayoff>>();
		
	}
	
	/**
	 * @param player
	 * @param strategyPlayed
	 * @param opponentStrategy
	 * @param payoff
	 */
	public void addPayoff(String player, String strategyPlayed, String opponentStrategy, double payoff) {
		
		if ( !player.equals("Hider") && !player.equals("Seeker") ) { 
			
			System.out.println("Only Hider or Seeker entries in payoff matrix.");
			
			return;
			
		}

		if ( playerToStrategies.containsKey(player) ) {
			
			if ( playerToStrategies.get(player).contains(new StrategyPayoff(strategyPlayed)) ) {
				
				playerToStrategies.get(player).get(playerToStrategies.get(player).indexOf(new StrategyPayoff(strategyPlayed))).addPayoff(opponentStrategy, payoff);
			
			} else {
				
				playerToStrategies.get(player).add(new StrategyPayoff(strategyPlayed, opponentStrategy, payoff));
				
			}
		
		} else {
			
			ArrayList<StrategyPayoff> playerPayoffList = new ArrayList<StrategyPayoff>();
			
			playerPayoffList.add(new StrategyPayoff(strategyPlayed, opponentStrategy, payoff));
			
			playerToStrategies.put(player, playerPayoffList);
		
		}
		
	}
	
	/**
	 * @return
	 */
	public ArrayList<String> GTAnalysis() {
		
		for ( Entry<String, ArrayList<StrategyPayoff>> player : playerToStrategies.entrySet() ) {
			
			if ( player.getValue().size() < 2 ) { 
				
				System.err.println("Each player must have at least two strategies in order to analyse matrix.");
				
				return new ArrayList<String>();
				
			}
			
		}
		
		if ( GAMBIT ) {
			
			outputForGambit();
		
		} else {
			
			outputForLRS();
			
		}
		
		
		return getGTData();
		
	}

	/**
	 * @param values
	 */
	private void outputForLRS() {
		
		Utils.clearFile(Utils.FILEPREFIX + "GTData");
		
		FileWriter nashOutputWriter = null;
		
		try {
			
			nashOutputWriter = new FileWriter(Utils.FILEPREFIX + "GTData", true);
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		for ( String bimatrixLine : formatAsBimatrix() ) Utils.writeToFile(nashOutputWriter, bimatrixLine);
					
		try {
			
			nashOutputWriter.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	/**
	 * 
	 */
	private void outputForGambit() {
		
		Utils.clearFile(Utils.FILEPREFIX + "GTData.nfg");
		
		FileWriter nashOutputWriter = null;
		
		try {
			
			nashOutputWriter = new FileWriter(Utils.FILEPREFIX + "GTData.nfg", true);
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		for ( String bimatrixLine : formatAsTopDownMatrix() ) Utils.writeToFile(nashOutputWriter, bimatrixLine);
					
		try {
			
			nashOutputWriter.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	/**
	 * 
	 */
	private ArrayList<String> formatAsBimatrix() {
		
		ArrayList<String> bimatrixLines = new ArrayList<String>();
		
		if ( !playerToStrategies.containsKey("Hider") || !playerToStrategies.containsKey("Seeker") ) {
			
			System.out.println("No information for Hider and Seeker");
			
			return new ArrayList<String>();
		
		}
		
		ArrayList<StrategyPayoff> hiderStrategies = playerToStrategies.get("Hider");
		
		ArrayList<StrategyPayoff> seekerStrategies = playerToStrategies.get("Seeker");
		
		Collections.sort(hiderStrategies);
		
		Collections.sort(seekerStrategies);
		
		bimatrixLines.add(hiderStrategies.size() + " " + seekerStrategies.size() + "\n");
		
		bimatrixLines.add("\n");
		
		for ( StrategyPayoff hiderPayoff : hiderStrategies ) {
		
			for ( Entry<String, Double> payoff : hiderPayoff.opponentPayoffs().entrySet() ) {
				
				bimatrixLines.add(payoff.getValue() + " ");
				
			}
			
			bimatrixLines.add("\n");
			
		}
		
		bimatrixLines.add("\n");
		
		for ( int i = 0; i < hiderStrategies.size(); i++ ) {
			
			for ( StrategyPayoff seekerPayoff : seekerStrategies ) {
				
				bimatrixLines.add(new ArrayList<Entry<String, Double>>(seekerPayoff.opponentPayoffs().entrySet()).get(i).getValue() + " ");
				
			}
			
			bimatrixLines.add("\n");
			
		}
		
		return bimatrixLines;

	}
	
	/**
	 * 
	 */
	private ArrayList<String> formatAsTopDownMatrix() {
		
		ArrayList<String> topDownLines = new ArrayList<String>();
		
		topDownLines.add("NFG 1 R \"Untitled Strategic Game\" { \"Player 1\" \"Player 2\" }");
		
		topDownLines.add("");
		
		if ( !playerToStrategies.containsKey("Hider") || !playerToStrategies.containsKey("Seeker") ) {
			
			System.out.println("No information for Hider and Seeker");
			
			return new ArrayList<String>();
		
		}
		
		ArrayList<StrategyPayoff> hiderStrategies = playerToStrategies.get("Hider");
		
		ArrayList<StrategyPayoff> seekerStrategies = playerToStrategies.get("Seeker");
		
		String hiderLine = "{ { ";
		
		for ( StrategyPayoff payoff : hiderStrategies ) hiderLine += "\"" + ( hiderStrategies.indexOf(payoff) + 1 ) + "\" ";
		
		hiderLine += "}";
		
		topDownLines.add(hiderLine);
		
		String seekerLine = "{ ";
		
		for ( StrategyPayoff payoff : seekerStrategies ) seekerLine += "\"" + ( seekerStrategies.indexOf(payoff) + 1 ) + "\" ";
		
		seekerLine += "}";
		
		topDownLines.add(seekerLine);
		
		topDownLines.add("}");
		
		topDownLines.add("\"\"");
		
		topDownLines.add("");
		
		topDownLines.add("{");
		
		for ( StrategyPayoff seekerPayoff : seekerStrategies ) {
			
			for ( StrategyPayoff hiderPayoff : hiderStrategies ) {
				
				topDownLines.add("{ \"\" " + hiderPayoff.getOpponentPayoff(seekerPayoff.getStrategyPlayed()) + ", " + seekerPayoff.getOpponentPayoff(hiderPayoff.getStrategyPlayed())+ " }");
				
			}
		}
		
		topDownLines.add("}");
		
		String numberLine = "";
		
		for ( StrategyPayoff seekerPayoff : seekerStrategies ) numberLine += ( seekerStrategies.indexOf(seekerPayoff) + 1 ) + " ";
		
		return topDownLines;
		
	}
	
	private boolean GAMBIT = true;
	
	/**
	 * @return
	 */
	public ArrayList<String> getGTData() {
		
		if ( GAMBIT ) {

			return getGambitData();
			
		} else {
			
			return getLRSData();
			
		}
		
	}
	
	private ArrayList<String> getGambitData() {
	
		ArrayList<String> formattedData = new ArrayList<String>();
		
		ArrayList<String> GTData = Utils.runCommandWithReturn("/usr/bin/gambit-enumpoly " + Utils.FILEPREFIX + "GTData.nfg");
	
		ArrayList<StrategyPayoff> hiderStrategies = playerToStrategies.get("Hider");
		
		ArrayList<StrategyPayoff> seekerStrategies = playerToStrategies.get("Seeker");
		
		for ( String output : GTData ) {
			
			String hider = "";
			
			String seeker = "";
			
			double hiderUtility = 0.0;
			
			double seekerUtility = 0.0;
			
			if ( output.startsWith("NE") ) {
				
				ArrayList<String> splitNE = new ArrayList<String>(Arrays.asList(output.split(",")));
				
				splitNE.remove(0);
				
				ArrayList<String> hiderDistribution = new ArrayList<String>(splitNE.subList(0, hiderStrategies.size()));
				
				ArrayList<String> seekerDistribution = new ArrayList<String>(splitNE.subList(hiderStrategies.size(), hiderStrategies.size() + seekerStrategies.size()));
				
				for ( String hiderProb : hiderDistribution ) {
					
					for ( String seekerProb : seekerDistribution ) {
					
						double cellProb = Double.parseDouble(hiderProb) * Double.parseDouble(seekerProb);
						
						hiderUtility += hiderStrategies.get(hiderDistribution.indexOf(hiderProb)).getOpponentPayoff(seekerStrategies.get(seekerDistribution.indexOf(seekerProb)).getStrategyPlayed()) * cellProb;
						
						seekerUtility += seekerStrategies.get(seekerDistribution.indexOf(seekerProb)).getOpponentPayoff(hiderStrategies.get(hiderDistribution.indexOf(hiderProb)).getStrategyPlayed()) * cellProb;
						
					}
					
				}
				
				for ( String prob : hiderDistribution ) {
					
					if ( prob.contains("1.") ) {
						
						hider = hiderStrategies.get(hiderDistribution.indexOf(prob)).getStrategyPlayed() + " ";
						
						break;
						
					}
					
					if ( twoDecimalPlaces(Double.parseDouble(prob)).contains("0.00") ) continue;
					
					hider += hiderStrategies.get(hiderDistribution.indexOf(prob)).getStrategyPlayed() + " (" + twoDecimalPlaces(Double.parseDouble(prob)) + "\\%) ";
					
				}
				
				for ( String prob : seekerDistribution ) {
					
					if ( prob.contains("1.") ) {
						
						seeker = seekerStrategies.get(seekerDistribution.indexOf(prob)).getStrategyPlayed() + " ";
						
						break;
						
					}
					
					if ( twoDecimalPlaces(Double.parseDouble(prob)).contains("0.00") ) continue;
					
					seeker += seekerStrategies.get(seekerDistribution.indexOf(prob)).getStrategyPlayed() + " (" + twoDecimalPlaces(Double.parseDouble(prob)) + "\\%) ";
					
				}	
				
				formattedData.add("Hider: " + hider + "(Payoff: " + twoDecimalPlaces(hiderUtility) + ") and Seeker: " + seeker + "(Payoff: " + twoDecimalPlaces(seekerUtility) + ")");
				
			}
			
		}
		
		return formattedData;
		
	}
	
	/**
	 * @return
	 */
	private ArrayList<String> getLRSData() {
		
		Utils.runCommand("/usr/local/bin/setnash " + Utils.FILEPREFIX + "GTData" + " " + Utils.FILEPREFIX + "Player1 " + Utils.FILEPREFIX + "Player2");
		
		ArrayList<String> GTData = Utils.runCommandWithReturn("/usr/local/bin/nash " + Utils.FILEPREFIX + "Player1 " + Utils.FILEPREFIX + "Player2 | tee");
	
		ArrayList<String> formattedData = new ArrayList<String>();
		
		String hider = "";
		
		double seekerUtility = 0;
		
		String seeker = "";
		
		double hiderUtility = 0;
		
		for ( String line : GTData ) {
			
			if ( line.contains("*") || line.length() == 0 ) continue;
			
			Utils.talk("", "Data: " + line);
			
			String[] lineSplitArray = line.split(" ");
		
			ArrayList<String> lineSplit = new ArrayList<String>();
			
			for ( String split : lineSplitArray ) {
				
				if ( split.length() == 0  ) continue;
					
				lineSplit.add(split);
				
			}
			
			if ( lineSplit.get(0).equals("1") ) {
				
				for ( int i = 1; i < lineSplit.size() - 1; i++ ) {
					
					String pureStrategy = playerToStrategies.get("Hider").get( i - 1 ).toString();
					
					if ( lineSplit.get(i).equals("1") ) {
						
						hider = pureStrategy;
						
						seekerUtility = Double.parseDouble(lineSplit.get(lineSplit.size() - 1));
						
						break;
						
					} else if ( lineSplit.get(i).contains("/") ) {
						
						String[] fractionSplit = lineSplit.get(i).split("/");
						
						double percentage = ( (double)(Integer.parseInt(fractionSplit[0].trim())) / (double)(Integer.parseInt(fractionSplit[1].trim())) ) * 100;
						
						hider += pureStrategy + " (" + twoDecimalPlaces(percentage) + "\\%) ";
						
						if (!lineSplit.get(lineSplit.size() - 1).contains("/")) {
							
							seekerUtility = Double.parseDouble(lineSplit.get(lineSplit.size() - 1));
							
						} else {
							
							String[] seekerUtlitySplit = lineSplit.get(lineSplit.size() - 1).split("/");
							
							seekerUtility = ( (double)(Integer.parseInt(seekerUtlitySplit[0].trim())) / (double)(Integer.parseInt(seekerUtlitySplit[1].trim())) );
						
						}
						
					}
					
				}
				
			} else if ( lineSplit.get(0).equals("2") ) {
				
				for ( int i = 1; i < lineSplit.size() - 1; i++ ) {
					
					String pureStrategy = playerToStrategies.get("Seeker").get( i - 1 ).toString();
					
					if ( lineSplit.get(i).equals("1") ) {
						
						seeker = pureStrategy;
							
						hiderUtility = Double.parseDouble(lineSplit.get(lineSplit.size() - 1));
						
						break;
						
					} else if ( lineSplit.get(i).contains("/") ) {
						
						String[] fractionSplit = lineSplit.get(i).split("/");
						
						double percentage = ( (double)Integer.parseInt(fractionSplit[0].trim()) / (double)Integer.parseInt(fractionSplit[1].trim()) ) * 100;
						
						seeker += pureStrategy + " (" + twoDecimalPlaces(percentage) + "\\%) ";
						
						if (!lineSplit.get(lineSplit.size() - 1).contains("/")) {
							
							hiderUtility = Double.parseDouble(lineSplit.get(lineSplit.size() - 1));
							
						} else {
						
							String[] hiderUtilitySplit = lineSplit.get(lineSplit.size() - 1).split("/");
						
							hiderUtility = ( (double)(Integer.parseInt(hiderUtilitySplit[0].trim())) / (double)(Integer.parseInt(hiderUtilitySplit[1].trim())) );
						
						}
						
					}
					
				}
				
			}
			
			if (!hider.equals("") && !seeker.equals("") ) { 
				
				formattedData.add("Hider: " + hider + " (Payoff: " + twoDecimalPlaces(hiderUtility) + ") and Seeker: " + seeker + " (Payoff: " + twoDecimalPlaces(seekerUtility) + ")");
				
				hider = "";
				
				seeker = "";
				
			}
			
		}
		
		return formattedData;
		
	}
	
	/**
	 * @param original
	 * @return
	 */
	public String twoDecimalPlaces(double original) {
		
	     DecimalFormat f = new DecimalFormat("##0.00");
	     
	     return f.format(original);
	     
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		ArrayList<StrategyPayoff> hiderStrategies = playerToStrategies.get("Hider");
		
		ArrayList<StrategyPayoff> seekerStrategies = playerToStrategies.get("Seeker");
		
		Collections.sort(hiderStrategies);
		
		Collections.sort(seekerStrategies);
		
		String topRow = "	 ";
		
		boolean emptyTop = true;
		
		ArrayList<String> rows = new ArrayList<String>();
		
		for ( StrategyPayoff strategy : hiderStrategies ) {
			
			String row = strategy.toString();
			
			for ( Entry<String, Double> payoff : strategy.opponentPayoffs().entrySet() ) {
				
				if ( emptyTop ) topRow += payoff.getKey() + "   ";
				
				row += "   " + payoff.getValue() + "  " + ( seekerStrategies.get(seekerStrategies.indexOf(new StrategyPayoff(payoff.getKey()))).opponentPayoffs().get(strategy.toString()) );
				
			}
			
			rows.add(row);
			
			emptyTop = false;
			
		}
		
		String returner = topRow;
		
		for ( String row : rows ) returner += "\n" + row;
		
		return returner;
		
	}
	
	/**
	 * @return
	 */
	public ArrayList<String> TikzMatrix() {
		
		ArrayList<String> tikzLines = new ArrayList<String>();
		
		//
		
		//tikzLines.add("\\begin{figure}[h!]");
		
		//tikzLines.add("\n");
		
		//tikzLines.add("\\centering");
		
		//tikzLines.add("\n");
		
		//tikzLines.add("\\begin{minipage}{0.5\\textwidth}");
		
		//tikzLines.add("\n");
		
		tikzLines.add("\\begin{tikzpicture};");
		
		tikzLines.add("\n");
				
		//
		
		tikzLines.add("\\def\\mycolhead{{");
		
		for ( StrategyPayoff seekerStrategies : playerToStrategies.get("Seeker") ) {
			
			tikzLines.add("\"" + seekerStrategies.toString() + "\",");
			
		}
		
		String lastLine = tikzLines.remove(tikzLines.size() - 1);
		lastLine = lastLine.substring(0, lastLine.length() - 1);
		tikzLines.add(lastLine);
		
		tikzLines.add("}}");
		
		tikzLines.add("\n");
				
		tikzLines.add("\\def\\myrowhead{{");
		
		for ( StrategyPayoff hiderStrategies : playerToStrategies.get("Hider") ) {
			
			tikzLines.add("\"" + hiderStrategies.toString() + "\",");
			
		}
			
		lastLine = tikzLines.remove(tikzLines.size() - 1);
		lastLine = lastLine.substring(0, lastLine.length() - 1);
		tikzLines.add(lastLine);
		
		tikzLines.add("}}");
		
		tikzLines.add("\n");
		
		//
		
		tikzLines.add("%Alter text width for longer seeker strategy names");
		
		tikzLines.add("\n");
		
		tikzLines.add("\\matrix[matrix of math nodes,draw,every odd row/.style={align=right},everyevenrow/.style={align=left},nodes={text width=2.5cm},row sep=0.2cm,column sep=0.2cm](m){");
		
		tikzLines.add("\n");
		
		ArrayList<String> bimatrixLines = formatAsBimatrix();
		
		// Get rid of reference to strategy numbers
		bimatrixLines.remove(0);
		
		ArrayList<String> hiderPayoffs = new ArrayList<String>(bimatrixLines.subList(1, bimatrixLines.size() / 2));
		
		ArrayList<String> seekerPayoffs = new ArrayList<String>(bimatrixLines.subList((bimatrixLines.size() / 2) + 1, bimatrixLines.size()));
		
		ArrayList<ArrayList<String>> hiderPayoffRows = new ArrayList<ArrayList<String>>();
		
		hiderPayoffRows.add(new ArrayList<String>());
		
		ArrayList<ArrayList<String>> seekerPayoffRows = new ArrayList<ArrayList<String>>();
		
		seekerPayoffRows.add(new ArrayList<String>());
		
		for ( String hiderPayoff : hiderPayoffs ) {
			
			if ( hiderPayoff.trim().length() == 0 ) {
				
				hiderPayoffRows.add(new ArrayList<String>());
				
				continue;
				
			}
			
			hiderPayoffRows.get(hiderPayoffRows.size() - 1).add(hiderPayoff);
		
		}
		
		for ( String seekerPayoff : seekerPayoffs ) {
			
			if ( seekerPayoff.trim().length() == 0 ) {
				
				seekerPayoffRows.add(new ArrayList<String>());
				
				continue;
				
			}
			
			seekerPayoffRows.get(seekerPayoffRows.size() - 1).add(seekerPayoff);
			
		}
		
		if ( hiderPayoffRows.size() != seekerPayoffRows.size() ) System.out.println("Payoffs should be equal");
		
		for ( int i = 0; i < hiderPayoffRows.size(); i++ ) {
		
			String seekersLine = "";
			
			String hidersLine = "";
			
			if ( hiderPayoffRows.get(i).size() == 0 || seekerPayoffRows.get(i).size() == 0 ) continue;
			
			for ( String seekerPayoff : seekerPayoffRows.get(i) ) {
				
				seekersLine += ( seekerPayoff.trim() + "&" );
				
			}
			
			tikzLines.add( seekersLine.substring(0, seekersLine.length() - 1) + " \\\\" );
			
			for ( String hiderPayoff : hiderPayoffRows.get(i) ) {
				
				hidersLine += ( hiderPayoff.trim() + "&" );
				
			}
			
			tikzLines.add( hidersLine.substring(0, hidersLine.length() - 1)  + " \\\\" );
			
		}
		
		tikzLines.add("\n");
		
		tikzLines.add("};");
		
		tikzLines.add("\n");
		
		//
		
		tikzLines.add("\\foreach\\x[count=\\xi from 2,evaluate={\\xx=int(2*\\x));\\xxi=int(\\xx+1)}] in {");
		
		for (int i = 1; i < playerToStrategies.get("Seeker").size(); i++ ) {
			
			tikzLines.add(i + ",");
			
		}
		
		lastLine = tikzLines.remove(tikzLines.size() - 1);
		lastLine = lastLine.substring(0, lastLine.length() - 1);
		tikzLines.add(lastLine);
		
		tikzLines.add("}{");
		
		tikzLines.add("\n");
		
		tikzLines.add("\\draw ({$(m-1-\\x)!0.5!(m-1-\\xi)$}|-m.north) -- ({$(m-1-\\x)!0.5!(m-1-\\xi)$}|-m.south);");
				
		tikzLines.add("\n");
		
		tikzLines.add("}");
		
		tikzLines.add("\n");
		
		//
		
		tikzLines.add("\\foreach\\x[count=\\xi from 2,evaluate={\\xx=int(2*\\x));\\xxi=int(\\xx+1)}] in {");
		
		for (int i = 1; i < playerToStrategies.get("Hider").size(); i++ ) {
			
			tikzLines.add(i + ",");
			
		}
		
		lastLine = tikzLines.remove(tikzLines.size() - 1);
		lastLine = lastLine.substring(0, lastLine.length() - 1);
		tikzLines.add(lastLine);
		
		tikzLines.add("}{");
		
		tikzLines.add("\n");
		
		tikzLines.add("\\draw ({$(m-\\xx-1)!0.5!(m-\\xxi-1)$}-|m.west) -- ({$(m-\\xx-1)!0.5!(m-\\xxi-1)$}-|m.east);");
				
		tikzLines.add("\n");
		
		tikzLines.add("}");
		
		tikzLines.add("\n");
		
		//
		
		tikzLines.add("\\foreach\\x in{");
		
		for (int i = 0; i < playerToStrategies.get("Seeker").size(); i++ ) {
			
			tikzLines.add(i + ",");
			
		}
		
		lastLine = tikzLines.remove(tikzLines.size() - 1);
		lastLine = lastLine.substring(0, lastLine.length() - 1);
		tikzLines.add(lastLine);
		
		tikzLines.add("}{");
		
		tikzLines.add("\n");
		
		tikzLines.add("\\node[text depth=0.25ex,above=2mm] at ($(m.north west)!{(2*\\x+1)/");
		
		tikzLines.add((playerToStrategies.get("Seeker").size() * 2) + "");
		
		tikzLines.add("}!(m.north east)$)");
		
		tikzLines.add("\n");
		
		tikzLines.add("{\\pgfmathparse{\\mycolhead[\\x]}\\pgfmathresult};");
		
		tikzLines.add("\n");
		
		tikzLines.add("}");
		
		tikzLines.add("\n");
		
		//
		
		tikzLines.add("\\foreach\\x in{");
		
		for (int i = 0; i < playerToStrategies.get("Hider").size(); i++ ) {
			
			tikzLines.add(i + ",");
			
		}
		
		lastLine = tikzLines.remove(tikzLines.size() - 1);
		lastLine = lastLine.substring(0, lastLine.length() - 1);
		tikzLines.add(lastLine);
		
		tikzLines.add("}{");
		
		tikzLines.add("\n");
		
		tikzLines.add("\\node[left=2mm] at ($(m.north west)!{(2*\\x+1)/");
		
		tikzLines.add((playerToStrategies.get("Hider").size() * 2) + "");
		
		tikzLines.add("}!(m.south west)$)");
		
		tikzLines.add("\n");
		
		tikzLines.add("{\\pgfmathparse{\\myrowhead[\\x]}\\pgfmathresult};");
		
		tikzLines.add("\n");
		
		tikzLines.add("}");
		
		//tikzLines.add("\n");
		
		//tikzLines.add("\\node[above=18pt of m.north] (firm b) {Seeker};");
		
		//tikzLines.add("\n");
		
		//tikzLines.add("\\node[left=1.6cm of m.west,align=center,anchor=center] {Hider};");
		
		tikzLines.add("\n");
		
		tikzLines.add("\\end{tikzpicture}");
		
		tikzLines.add("\n");
		
		// Uncomment for caption directly in Tikz
		/*String captionString = "";
		
		for ( String line : GTAnalysis() ) { 
			
			captionString += line;
		
		}
		
		tikzLines.add("\\caption{" + captionString + "}");
		
		tikzLines.add("\n");
		
		tikzLines.add("\\label{" + "" + "}");
		
		tikzLines.add("\n");
		
		tikzLines.add("\\end{minipage}");
		
		tikzLines.add("\n");
		
		tikzLines.add("\\end{figure}");
		
		tikzLines.add("\n");*/
		
		return tikzLines;
		
	}
	
	/**
	 * 
	 */
	public void printTikzMatrix() {
		
		for ( String line : TikzMatrix() ) {
		
			if ( line.equals("\n") ) {
				
				System.out.println("");
				
			} else {
				
				System.out.print(line);
				
			}
			
		}
		
	}
	
	/**
	 * 
	 */
	public void printTikzMatrixToFile() {
		
		String outputPath = "figure" + new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
		
		FileWriter tikzOutputWriter = null;
		
		try {
			
			tikzOutputWriter = new FileWriter(Utils.FILEPREFIX + "charts/" + outputPath + ".tex", true);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		
		}
		
		for ( String line : TikzMatrix() ) {
			
			Utils.writeToFile(tikzOutputWriter, line);
			
		}
			
		try {
			
			tikzOutputWriter.close();
		
		} catch (IOException e) {
		
			e.printStackTrace();
		
		}
		
		try {
			
			String captionString = "";
			
			for ( String line : GTAnalysis() ) { 
				
				captionString += line;
			
			}
			

			ArrayList<StrategyPayoff> hiderStrategies = playerToStrategies.get("Hider");
			
			ArrayList<StrategyPayoff> seekerStrategies = playerToStrategies.get("Seeker");
			
			Utils.writeToFile(new FileWriter(Utils.FILEPREFIX + "/charts/figures.bib", true), "\n @FIG{" + outputPath + ", main = {A strategic overview of the " + Utils.traverserNumberToWord(playerToStrategies.get("Hider").size()) + "hider strategies and the " + Utils.traverserNumberToWord(playerToStrategies.get("Seeker").size()) + "seeker strategies introduced in this section, on a X network. The equilibrium strategies for each player are as follows: " + captionString + "}, add = {}, file = {/Users/Martin/Dropbox/workspace/SearchGames/output/charts/" + outputPath + "}, source = {}}");
		
		} catch (IOException e) {
			
			e.printStackTrace();
		
		}
		
	}
	
	/**
	 * For testing.
	 * @param args
	 */
	public static void main(String[] args) {
		
		ApproximatePayoffMatrix HPM = new ApproximatePayoffMatrix("");
		
		/*HPM.addPayoff("Hider", "L", "L'", 2.6);
		HPM.addPayoff("Hider", "L", "R'", 0);
		HPM.addPayoff("Hider", "M", "L'", 0);
		HPM.addPayoff("Hider", "M", "R'", 0);
		HPM.addPayoff("Hider", "R", "L''", 1);
		HPM.addPayoff("Hider", "R", "R'", 1);
	
		HPM.addPayoff("Seeker", "L'", "L", 1);
		HPM.addPayoff("Seeker", "L'", "M", 2);
		HPM.addPayoff("Seeker", "L'", "R", 3);
		HPM.addPayoff("Seeker", "R'", "L", 0);
		HPM.addPayoff("Seeker", "R'", "M", 1);
		HPM.addPayoff("Seeker", "R'", "R", 3);*/
		
		HPM.addPayoff("Hider", "B", "B", 1.0);
		HPM.addPayoff("Hider", "B", "F", 0.0);
		HPM.addPayoff("Hider", "F", "B", 0.0);
		HPM.addPayoff("Hider", "F", "F", 2.0);
		
		HPM.addPayoff("Seeker", "B", "B", 2.0);
		HPM.addPayoff("Seeker", "F", "B", 0.0);
		HPM.addPayoff("Seeker", "B", "F", 0.0);
		HPM.addPayoff("Seeker", "F", "F", 1.0);
		
		System.out.println(HPM);
		
		//HPM.printTikzMatrix();
		
		HPM.GTAnalysis();
		
		//System.out.println(HPM.GTAnalysis());
	
	}
	
}
