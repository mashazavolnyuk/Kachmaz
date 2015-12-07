package com.example.diplom;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by Merry on 07.12.15.
 */
public class KachmazClassicTest extends TestCase {
    private boolean flag=false;
   private double[][] matrixTest = {
            {-6, 2, -3, -24.35},
            {2, 5, 6, -3.43},
            {1, -3, 1, 12.83}
    };
    private double [] testResult;

    @Test
    public void testCalc() throws Exception {

        KachmazClassic k=new KachmazClassic();
        k.calc();
        testResult =k.giveResult();
        assertNotNull(testResult);
    }

    @Test
    public void testIsEnd() throws Exception {
       KachmazClassic k=new KachmazClassic();
        k.calc();
        flag=k.isEnd();
        assertTrue(flag);//algorithm must have finished
    }

    @Test
    public void testCalcResultError() throws Exception {


    }
}
