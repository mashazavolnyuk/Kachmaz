package com.example.diplom;

import android.os.SystemClock;
import android.util.Log;

/**
 * Created by Merry on 21.10.15.
 */
public class Kazchmaz_multithread implements Calc {

//    private double[][] matrixA = {
//            {1, 2, 3, 3},
//            {3, 5, 7, 0},
//            {1, 3, 4, 1}
//    };

    private double[][] matrixA = {
            {-6, 2, -3, -24.35},
            {2, 5, 6, -3.43},
            {1, -3, 1, 12.83}
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

    private double relaxationFunction = 20;

    private int iterationNumber = 0;
    private int localIterationJK = 0;

    private int threadCount = 2;

    private int iterationCount = 5000;
    private double allError;
    private long resultTime;
    private long startTime;


    public Kazchmaz_multithread(){
       initializeUStep();

    }
    public void setStartPoint(double[] startPoint){

        if(startPoint.length!=rowCount)
            return;
        uStep=startPoint;

    }
    public void setRelaxationFunction(double relax){

        if(relax>0)
            relaxationFunction=relax;

    }
    public void calc() {
        startTime = SystemClock.uptimeMillis();
        while (!isEnd()){
            iteration();
        }
        showResult();
        resultTime = SystemClock.uptimeMillis() - startTime;
    }

    private void iteration(){
        setLocalIterationJK();
        changeWorkingLine();
        preNumerator = 0;
        for (int i = 0; i < workingLine.length; i++) {
            preNumerator += workingLine[i] * uStep[i];
        }
        numerator = k - preNumerator;
        denominator = Math.pow(sumSqrt, 2);
        vectorK = relaxationFunction * (numerator / denominator);
        for (int i = 0; i < workingLine.length; i++) {
            double v = vectorK * workingLine[i];
            uStep[i] = v + uStep[i];
        }
        iterationNumber++;
    }

    private void changeWorkingLine() {
        sumSqrt = 0;
        for (int i = 0; i < columnCount - 1; i++) {
            workingLine[i] = matrixA[localIterationJK][i];
            sumSqrt += Math.pow(workingLine[i], 2);
        }
        k = matrixA[localIterationJK][columnCount - 1];
    }

    //todo get point from some line
    private void initializeUStep() {
        workingLine = new double[columnCount - 1];
        uStep = new double[columnCount - 1];
        for (int i = 0; i < uStep.length - 1; i++) {
            uStep[i] = 1;
        }
        //calculate last element with other=0 (cross straight with zero hyperplane)
//        uStep[uStep.length - 1] = matrixA[0][columnCount-1] / matrixA[0][columnCount-2];
    }

    private void setLocalIterationJK(){
        localIterationJK = iterationNumber % rowCount;
    }

    private boolean isEnd(){
        double error = 0;
        if(iterationNumber % 10 == 0){
            error = calcResultError();
            if(Math.abs(error) < 0.000001 || iterationNumber > iterationCount)
                return true;
        }
        return false;

//        if(iterationNumber < iterationCount)
//            return false;
//        else
//            return true;
    }

    private double calcResultError(){
        allError = 0;
        for(int i=0;i<rowCount;i++){
            double sum = 0;
            for(int j=0;j<columnCount-1;j++)
                sum += matrixA[i][j] * uStep[j];
            double error = sum - matrixA[i][columnCount-1];
            allError += error;
        }
//        if(allError < 0.2)
//            relaxationFunction = 1.5;
//        if(allError < 0.1)
//            relaxationFunction = 1.2;
//        if(allError < 0.01)
//            relaxationFunction = 0.5;
        Log.d("Error", "Iteration " + iterationNumber + "All Error = " + allError);
        Log.d("Error", "Result: x1=" + uStep[0] + ", x2=" + uStep[1] + ", x3=" + uStep[2]);
        return allError;
    }

    public double[] getResult(){
        return uStep;
    }

    public int getIterationNumber(){
        return iterationNumber;
    }

    public double getError(){
        return allError;
    }

    private void showResult(){
        Log.d("Result", "Result: x1="+ uStep[0] + ", x2=" + uStep[1] + ", x3=" + uStep[2]);
    }

    public long getResultTime() {
        return resultTime;
    }
}