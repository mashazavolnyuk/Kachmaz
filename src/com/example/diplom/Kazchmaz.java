package com.example.diplom;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Merry on 21.10.15.
 */
public class Kazchmaz implements Calc {
    private double[][] matrixA = {
            {1, 2, 3, 3},
            {3, 5, 7, 0},
            {1, 3, 4, 1}
    };

    private int columnCount = 4;
    private int rowCount = 3;

//    private ArrayList<Double> uZero = new ArrayList<Double>();
    private double[] uStep;
    private double[] workingLine;
    private double k = 0;
    private double sumSqrt = 0;
    private double denominator = 0;//знаменатель
    private double numerator = 0;//числитель
    private double preNumerator = 0;//числитель
    private double vectorK = 0;

    private int iterationNumber = 0;
    private int localIterationJK = 0;

    private int iterationCount = 50;

    public void calc() {
        initializeUStep();
        while (!isEnd()){
            iteration();
        }
        showResult();
    }

    private void iteration(){
        setLocalIterationJK();
        changeWorkingLine(localIterationJK);
        for (int i = 0; i < workingLine.length; i++) {
            preNumerator += workingLine[i] * uStep[i];
        }
        numerator = k - preNumerator;
        denominator = Math.pow(sumSqrt, 2);
        vectorK = numerator / denominator;
        for (int i = 0; i < workingLine.length; i++) {
            double v = vectorK * workingLine[i];
            uStep[i] = v + uStep[i];
        }
        iterationNumber++;
    }

    private void changeWorkingLine(int count) {
        for (int i = count; i < columnCount - 1; i++) {
            workingLine[i] = matrixA[localIterationJK][i];
            sumSqrt += Math.pow(workingLine[i], 2);
        }
        k = matrixA[localIterationJK][columnCount];
    }

    //todo get point from some line
    private void initializeUStep() {
        workingLine = new double[columnCount - 1];
        uStep = new double[columnCount - 1];
        for (int i = 0; i < uStep.length; i++) {
            uStep[i] = 0.5;
        }
    }

    private void setLocalIterationJK(){
        localIterationJK = iterationNumber % rowCount;
    }

    private boolean isEnd(){
        if(iterationNumber < 50)
            return false;
        else
            return true;
    }

    private void showResult(){
        Log.d("Result", "Result: x1="+ uStep[0] + ", x2=" + uStep[1] + ", x3=" + uStep[2]);
    }
}