package org.kclhi.hands.utility.output;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import org.kclhi.hands.utility.Metric;
import org.kclhi.hands.utility.TraverserDataset;
import org.kclhi.hands.utility.TraverserDatasetMeasure;
import org.kclhi.hands.utility.Utils;

public class HiderRecord extends TraverserRecord {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private ArrayList<TraverserRecord> seekersAndAttributes;
	
	/**
	 * @return
	 */
	public ArrayList<TraverserRecord> getSeekersAndAttributes() {
		
		return seekersAndAttributes;
	
	}
	
	/**
	 * 
	 */
	public void clearSeekersAndAttributes() {
		
		seekersAndAttributes.clear();
		
	}
	
	/**
	 * 
	 */
	private String fileRelatingTo;
	
	/**
	 * @return
	 */
	public Path getFileRelatingTo() {
		
		return new File(fileRelatingTo).toPath();
	
	}
	
	/* (non-Javadoc)
	 * @see Utility.output.TraverserRecord#duplicateRecord(Utility.output.TraverserRecord)
	 */
	public void duplicateRecord(TraverserRecord record) {
		
		super.duplicateRecord(record);
		
		this.seekersAndAttributes = new ArrayList<TraverserRecord>(((HiderRecord)record).getSeekersAndAttributes());
		
		this.fileRelatingTo = ((HiderRecord)record).getFileRelatingTo().toString();
		
	}
	
	/**
	 * @param hider
	 */
	public HiderRecord(String hider) {
		
		super(hider);
		
		seekersAndAttributes = new ArrayList<TraverserRecord>();
	
	}
	
	/**
	 * @param fileRelatingTo
	 * @param hider
	 */
	public HiderRecord(Path fileRelatingTo, String hider) {
		
		super(hider);
		
		this.fileRelatingTo = fileRelatingTo.toString();
		
		seekersAndAttributes = new ArrayList<TraverserRecord>();
	
	}
	
	/**
	 * @param seekerRecord
	 */
	public void addSeeker(TraverserRecord seekerRecord) {
		
		seekersAndAttributes.add(seekerRecord);
		
		opponents = seekersAndAttributes.toString().replace("[", "").replace("]", "").replace(",", "");
		
	}
	
	/**
	 * @param seeker
	 * @return
	 */
	public boolean containsSeeker(TraverserRecord seeker) {
		
		return seekersAndAttributes.contains(seeker);
		
	}
	
	/**
	 * @param seeker
	 * @return
	 */
	public TraverserRecord getSeeker(String seeker) {
	
		return seekersAndAttributes.get(seekersAndAttributes.indexOf(new TraverserRecord(seeker)));
		
	}
	
	/**
	 * @return
	 */
	public HashSet<String> getSeekerAttributes() {
		
		return seekersAndAttributes.get(0).getAttributes();
		
	}
	
	/* (non-Javadoc)
	 * @see Utility.output.TraverserRecord#gameDatasetForPayoff(java.util.Hashtable, java.util.Hashtable)
	 */
	public TraverserDataset gameDatasetForPayoff(Hashtable<String, Double> minAttributeToValueAllSeries, Hashtable<String, Double> maxAttributeToValueAllSeries) {
		
		TraverserDataset payoffData = new TraverserDataset();
		
		TraverserDataset hiderData = getAttributeToDataset(getGameSeries(), minAttributeToValueAllSeries, maxAttributeToValueAllSeries).get(Metric.COST.getText());
		
		for ( int i = 0; i < hiderData.getDataset().size(); i++ ) {
			
			double cumulativeSeekerDataEntry = 0.0;
			
			for ( TraverserRecord seeker : seekersAndAttributes ) {
				
				Utils.printSystemStats();
				
				TraverserDataset seekerData = seeker.getAttributeToDataset(seeker.getGameSeries(), minAttributeToValueAllSeries, maxAttributeToValueAllSeries).get(Metric.COST.getText());
				
				cumulativeSeekerDataEntry += seekerData.getDataset().get(i);
				
			}
			
			payoffData.addItemToDataset( ( cumulativeSeekerDataEntry / (double)seekersAndAttributes.size() ) - hiderData.getDataset().get(i) ); 
			
		}
		
		return payoffData;
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		if ( showOpponents && opponents != null ) {
			
			return traverser + " vs " + opponents;
			
		} else {
			
			return traverser;
			
		}
		
	}
	
	/**
	 * @return
	 */
	public String printStats() {
		
		String returner = "\n-------------------\n";
		returner += "\nAverages:\n";
		returner += "\n-------------------\n";
		
		returner += "\n" + traverser + " " + attributeToGameMeasure(TraverserDatasetMeasure.MEAN) + "\n";
		
		for ( TraverserRecord seeker : seekersAndAttributes ) returner += "\n" + seeker.printGameAverage() + "\n";
		
		returner += "\n-------------------\n";
		returner += "\nSeries:\n";
		returner += "\n-------------------\n";
		
		returner += "\n" + traverser + " " + showGameSeries() + "\n";
		
		for ( TraverserRecord seeker : seekersAndAttributes ) returner += "\n" + seeker.printSeries() + "\n";
		
		return returner;
		
	}

}
