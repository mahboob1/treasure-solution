package com.mhcl.treasure.service;

 
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for TreasureHunt
 */
public class TreasureHuntTest  extends TestCase
{
    public TreasureHuntTest( String testName )
    {
        super( testName );
    }
    
    protected void setUp() throws Exception {
    	super.setUp();
    	}
    
    protected void tearDown() throws Exception {
    	super.tearDown();
    	}
    

    public void testCalculateDistance()
    {
    	TreasureHunt treasureHunt = new TreasureHunt();
		System.out.println(treasureHunt.calculateDistance());
    }
}
