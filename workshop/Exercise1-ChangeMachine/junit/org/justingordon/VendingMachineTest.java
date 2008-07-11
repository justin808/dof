package org.justingordon;

import org.junit.*;
import static org.junit.Assert.*;

public class VendingMachineTest
{

	public void testWorkingInRTC() {
		// This is just an empty test so that we can see test results.
        VendingMachine vendingMachine = new VendingMachine();
        vendingMachine.toString();
	}
    // First tests can assume unlimited coins available

    @Test
    @Ignore
    public void testGetChangeReturnsNoChangeWhenNoCents()
    {

        VendingMachine vendingMachine = new VendingMachine();
        Change changeCalculated = vendingMachine.getChange(0);
        Change changeExpected = new Change(0, 0, 0, 0);
        assertEquals(changeExpected, changeCalculated);
    }

    // testGetChangeReturnsOnePennyIfOneCents
    // testGetChangeReturnsTwoPenniesIfTwoCents
    // testGetChangeReturnsOneNickelIfFiveCents
    // testGetChangeReturnsOneNickelTwoPenniesIfSevenCents
    // testGetChangeReturnsOneDimeIfTenCents
    // testGetChangeReturnsOneDimeTwoPenniesIfTwelveCents
    // testGetChangeReturnsOneDimeOneNickelThreePenniesIf18Cents
    // testGetChangeReturnsTwoDimesOnePennyIf21Cents
    // testGetChangeReturnsTwoDimesOnePennyIf21Cents

    // testGetChangeReturnsOneQuartersOneNickelIf30Cents
    // testGetChangeReturnsThreeQuartersOneNickelIf80Cents

    // Advanced Students!

    // Next tests will make VendingMachine require storing available pennies, nickels, dimes, quarters

    // testGetChangeReturnsTwoQuartersThreeDimesIf80CentsAndNoNickels


    // ADD OTHER TEST CASES IF SOME COIN TYPES NOT AVAILABLE



}
