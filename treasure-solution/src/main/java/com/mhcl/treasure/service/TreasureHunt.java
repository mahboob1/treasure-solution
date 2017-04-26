package com.mhcl.treasure.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mhcl.treasure.model.Distance;
import com.mhcl.treasure.model.Step;
import com.mhcl.treasure.util.ReadFileUtil;

public class TreasureHunt {
	private static final String FILE_RIDETYPE = "C:\\datafile\\ridetype-data.txt";
	private static final String FILE_TREASURELOCATION = "C:\\datafile\\treasurelocation-data.txt";
	private static final String FILE_RIDETYPE_SEP = "-";
	private static final String FILE_TREASURELOCATION_SEP = ",";
	private static final String SPACE_SEP = " ";
	private static double SQRT_ONE_HALF = 0.71; // E, W .. projection for NW, SE ..
	private static double ONE = 1.00;
	private static int MINUTE_TO_HOUR = 60;
	private static int MINUTE_OR_HOUR = 2;
	private static boolean firstStepNS = true;
	private static boolean firstStepEW = true;
	private static DecimalFormat df2 = new DecimalFormat(".##");
	
	private Map<String, Integer> ride = new HashMap<>(); 
	 
	public TreasureHunt() {
		List<String> rideTypeArray = ReadFileUtil.readFile(FILE_RIDETYPE);
		for(String rtString :  rideTypeArray) {
			String[] keyValue = rtString.split(FILE_RIDETYPE_SEP);
			String value[] = keyValue[1].trim().split(SPACE_SEP);
			ride.put(keyValue[0].trim(),Integer.parseInt(value[0]));
		}
	}
	
	public List<Step> readTreasureLocDirections(String fileLocation) {
		List<String> rideTypeArray = ReadFileUtil.readFile(fileLocation);
		List<Step> steps = new ArrayList<>(); 
		for(String rideType : rideTypeArray) {
			Step step = new Step();
			String[] members = rideType.split(FILE_TREASURELOCATION_SEP);
			step.setRideType(members[0].trim());
			step.setTimeString(members[1].trim());
			step.setDirection(members[2].trim());
			steps.add(step);
		}
		return steps;
	}
	
	public double calculateTime(String stepTime) {
		String[] timeArray;
		double time =0;
		timeArray = stepTime.trim().split(SPACE_SEP);
		if(timeArray.length > MINUTE_OR_HOUR) {
			time = Double.parseDouble(timeArray[2])/MINUTE_TO_HOUR + Double.parseDouble(timeArray[0]);
		} else {
			time = Integer.parseInt(timeArray[0]);
			if(timeArray[1].contains("min")) {
				time /= MINUTE_TO_HOUR;
			}
		}
		return time;
	}
	
	public void addStepToDirection(Distance distance, Step step, String direction, double fraction) {
		double tempDistance =ride.get(step.getRideType())*calculateTime(step.getTimeString())*fraction;
		switch(direction) {
		case "N" :
			if(distance.getTravelledNorth() > 0 || firstStepNS) {
				distance.addTravelledNorth(tempDistance);
				firstStepNS = false;
			} else {				 
				tempDistance = distance.getTravelledSouth() - tempDistance;
				if(tempDistance > 0 ) {
					distance.setTravelledSouth(tempDistance);
				} else {
					distance.setTravelledSouth(0.00);
					distance.setTravelledNorth(-tempDistance);
				}
			}
			break;
		case "S" :
			if(distance.getTravelledSouth() > 0 || firstStepNS) {
				distance.addTravelledSouth(tempDistance);
				firstStepNS = false;
			} else {
				tempDistance = distance.getTravelledNorth() - tempDistance;
				if(tempDistance > 0 ) {
					distance.setTravelledNorth(tempDistance);
				} else {
					distance.setTravelledNorth(0.00);
					distance.setTravelledSouth(-tempDistance);
				}
			}
			break;
		case "E" :
			if(distance.getTravelledEast() > 0 || firstStepEW) {
				distance.addTravelledEast(tempDistance);
				firstStepEW = false;
			} else {				 
				tempDistance = distance.getTravelledWest() - tempDistance;
				if(tempDistance > 0 ) {
					distance.setTravelledWest(tempDistance);
				} else {
					distance.setTravelledWest(0.00);
					distance.setTravelledEast(-tempDistance);
				}
			}
			break;
		case "W" :
			if(distance.getTravelledWest() > 0 || firstStepEW) {
				distance.addTravelledWest(tempDistance);
				firstStepEW = false;
			} else {				 
				tempDistance = distance.getTravelledEast() - tempDistance;
				if(tempDistance > 0 ) {
					distance.setTravelledEast(tempDistance);
				} else {
					distance.setTravelledEast(0.00);
					distance.setTravelledWest(-tempDistance);
				}
			}
			break;
		default :
			break;
		}
	}
	
	public String calculateDistance() {
		Distance distance = new Distance();
		String treasureDirection;
		List<Step> steps = this.readTreasureLocDirections(FILE_TREASURELOCATION);
		for(Step step : steps) {
			switch(step.getDirection()) {
			case "N" :
				addStepToDirection(distance, step, "N", ONE);
				break;
			case "NE" :
				addStepToDirection(distance, step, "N", SQRT_ONE_HALF);
				addStepToDirection(distance, step, "E", SQRT_ONE_HALF);
				break;
			case "NW" :
				addStepToDirection(distance, step, "N", SQRT_ONE_HALF);
				addStepToDirection(distance, step, "W", SQRT_ONE_HALF);
				break;
			case "S" :
				addStepToDirection(distance, step, "S", ONE);
				break;
			case "SE" :
				addStepToDirection(distance, step, "S", SQRT_ONE_HALF);
				addStepToDirection(distance, step, "E", SQRT_ONE_HALF);
				break;
			case "SW" :
				addStepToDirection(distance, step, "S", SQRT_ONE_HALF);
				addStepToDirection(distance, step, "W", SQRT_ONE_HALF);
				break;
			case "E" :
				addStepToDirection(distance, step, "E", ONE);
				break;
			case "W" :
				addStepToDirection(distance, step, "W", ONE);
				break;
			default : 
				break;
			}
		}
		
		if(distance.getTravelledNorth() > 0 ) {
			treasureDirection = "Travelled " + df2.format(distance.getTravelledNorth()) + " miles to the north,";
		} else {
			treasureDirection = "Travelled " + df2.format(distance.getTravelledSouth()) + " miles to the south,";
		}
		if(distance.getTravelledEast() > 0 ) {
			treasureDirection += " " + df2.format(distance.getTravelledEast()) + " miles to the east";
		} else {
			treasureDirection += " " + df2.format(distance.getTravelledWest()) + " miles to the west";
		}
		return treasureDirection;
	}
	 

	public static void main(String[] args) {
		TreasureHunt treasureHunt = new TreasureHunt();
		System.out.println(treasureHunt.calculateDistance());
		
	}
}
