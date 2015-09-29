package com.example.diplom;

/**
 * Created by Merry on 29.09.15.
 */
public class SimplexSolve {

    static boolean solved = false;
    static boolean lim = false;
    static int tempCInd = 0;
    static int minRInd = 0;
    static int minCInd = 1;
    static float[] solution;

    //решение задачи в автоматическом режиме
    static float[][] Solve(float[][] matrix){

        M1: {
            solved = true;
            // проверяем решение на оптимальность
            for (int i = 0; i <= ReadFile.colCount; i++){
                if (matrix[i][0] < 0)
                    solved = false;
            }

            /		/пока решение не оптимально
            while (!solved){
                // находим ведущий столбец
                float minR = matrix[0][0];
                int minRInd = 0;
                for (int i = 0; i <= ReadFile.colCount; i++){
                    if (matrix[i][0] < minR){
                        minR = matrix[i][0];
                        minRInd = i;
                    }
                }

                //проверяем, ограничена ли целевая функция на множестве доп. решений
                lim = false;
                for (int i = 0; i <= ReadFile.rowCount; i++){
                    if (matrix[minRInd][i] > 0)
                        lim = true;
                }
                //если функция не ограничена, выводим сообщение об ошибке, прерываем
                //решение
                if (!lim){
                    solved = true;
                    JOptionPane.showMessageDialog(null, "функция не ограничена на множестве допустимых решений");
                    break M1;
                }

                //находим ведущую строку
                float minC = matrix[ReadFile.colCount][1]/matrix[minRInd][1];
                int minCInd = 1;
                for (int i = 1; i < tempCInd; i++){
                    if (matrix[ReadFile.colCount][i]/matrix[minRInd][i] < minC){
                        minC = matrix[ReadFile.colCount][i]/matrix[minRInd][i];
                        minCInd = i;
                    }
                }
                for (int i = tempCInd + 1; i <= ReadFile.rowCount; i++){
                    if (matrix[ReadFile.colCount][i]/matrix[minRInd][i] < minC){
                        minC = matrix[ReadFile.colCount][i]/matrix[minRInd][i];
                        minCInd = i;
                    }
                }

                //выводим из базиса базисную переменную [0][minCInd], вводим в базис
                //переменную [minRInd][0]
                ReadFile.varCol[minCInd-1] = ReadFile.varRow[minRInd] ;


                //строим новую симплексную таблицу
                //делим ведущую строку на ведущий элемент [minRInd][minCInd]
                float temp = matrix[minRInd][minCInd];
                System.out.print(">> " + temp + "\n");
                System.out.print("\nведущая строка: ");
                for (int i = 0; i <= ReadFile.colCount; i++){
                    matrix[i][minCInd] /= temp;
                }

                //получаем нули в ведущем столбце
                for (int j = 0; j < minCInd; j++){
                    float minTemp = matrix[minRInd][j];
                    for (int i = 0; i <= ReadFile.colCount; i++){
                        matrix[i][j] += matrix[i][minCInd] * -minTemp;
                    }
                }

                for (int j = minCInd+1; j <=ReadFile.rowCount; j++){
                    float minTemp = matrix[minRInd][j];
                    for (int i = 0; i <= ReadFile.colCount; i++){
                        matrix[i][j] += matrix[i][minCInd] * -minTemp;
                    }
                }
                //обновляем вектор решения
                for (int i = 0; i < ReadFile.bvarCount; i++){
                    for (int j = 0 ; j < ReadFile.varCount; j++){
                        int k = j + 1;
                        String tempS = "x" + k;
                        if (tempS.equals(ReadFile.varCol[i]))
                            solution[j] = matrix[ReadFile.colCount][i+1];
                    }
                }
                tempCInd = minCInd;
                //рекурсивно вызываем процедуру, пока решение не будет оптимальным
                Solve(matrix);
            }
        }
        return matrix;
    }
    //создаем вектор решения
    static void initSolution(int varCount){
        solution = new float[varCount];
        for (int i = 0; i < varCount; i++){
            solution[i] = 0;
        }
    }


    //выбор ведущего столбца в режиме обучения
    static boolean userChooseCol(float[][] matrix, JTable tableName){

        boolean err = false;

        M1: {

            //находим ведущий столбец
            float minR = matrix[0][0];
            minRInd = 0;
            for (int i = 0; i <= ReadFile.colCount; i++){
                if (matrix[i][0] < minR){
                    minR = matrix[i][0];
                    minRInd = i;
                }
            }

            //проверяем выбор пользователя
            while (minRInd != SimplexView.getSelectedCol() - 1){
                JOptionPane.showMessageDialog(null, "ведущий столбец выбран
                        неверно");
                        err = true;
                break M1;
            }

            int temp = minRInd;
            float[] proportion = new float[ReadFile.rowCount];

            //вычисляем вспомогательный столбец отношения
            for (int i = 1; i <= ReadFile.rowCount; i++){
                if ( i == tempCInd ){
                    proportion[i-1] = java.lang.Float.NaN;
                }
                else{
                    proportion[i-1] = matrix[ReadFile.colCount][i] /
                            matrix[temp][i];
                }
            }
            TableView.fillProportion(tableName, proportion, tempCInd);
        }

        return err;
    }

    //выбор ведущей строки в режиме обучения
    static boolean userChooseRow(float[][] matrix, JTable tableName){
        lim = false;
        boolean err = false;

        M1:{
            //проверяем, ограничена ли целевая функция на множестве доп. решений
            for (int i = 0; i <= ReadFile.rowCount; i++){
                if (matrix[minRInd][i] > 0)
                    lim = true;
            }

            if (!lim){
                JOptionPane.showMessageDialog(null, "функция не ограничена на
                        множестве допустимых решений");
                break M1;
            }

            //находим ведущую строку
            float minC = matrix[ReadFile.colCount][1]/matrix[minRInd][1];
            minCInd = 1;
            for (int i = 1; i < tempCInd; i++){
                if (matrix[ReadFile.colCount][i]/matrix[minRInd][i] < minC){
                    minC = matrix[ReadFile.colCount][i]/matrix[minRInd][i];
                    minCInd = i;
                }
            }
            for (int i = tempCInd + 1; i <= ReadFile.rowCount; i++){
                if (matrix[ReadFile.colCount][i]/matrix[minRInd][i] < minC){
                    minC = matrix[ReadFile.colCount][i]/matrix[minRInd][i];
                    minCInd = i;
                }
            }

            //проверяем выбор пользователя
            System.out.print("user: " + SimplexView.getSelectedRow() + "; min: "
                    +minCInd);
            while (minCInd != SimplexView.getSelectedRow()){
                err = true;
                JOptionPane.showMessageDialog(null, "ведущая строка выбрана
                        неверно");
                break M1;
            }
        }
        return err;
    }

    //перестраивает симплексную таблицу
    static void userBuildNewTable(float[][] matrix, JTable tableName){
        //выводим из базиса базисную переменную [0][minCInd], вводим в базис
        //переменную [minRInd][0]

        ReadFile.varCol[minCInd-1] = ReadFile.varRow[minRInd] ;


        //строим новую симплексную таблицу
        //делим ведущую строку на ведущий элемент [minRInd][minCInd]
        float temp = matrix[minRInd][minCInd];

        for (int i = 0; i <= ReadFile.colCount; i++){
            matrix[i][minCInd] /= temp;
        }

        //получаем нули в ведущем столбце
        for (int j = 0; j < minCInd; j++){
            float minTemp = matrix[minRInd][j];
            for (int i = 0; i <= ReadFile.colCount; i++){
                matrix[i][j] += matrix[i][minCInd] * -minTemp;
            }
        }

        for (int j = minCInd+1; j <=ReadFile.rowCount; j++){
            float minTemp = matrix[minRInd][j];
            for (int i = 0; i <= ReadFile.colCount; i++){
                matrix[i][j] += matrix[i][minCInd] * -minTemp;
            }
        }

        for (int i = 0; i < ReadFile.bvarCount; i++){
            for (int j = 0 ; j < ReadFile.varCount; j++){
                int k = j + 1;
                String tempS = "x" + k;
                if (tempS.equals(ReadFile.varCol[i]))
                    solution[j] = matrix[ReadFile.colCount][i+1];
            }
        }

        tempCInd = minCInd;
    }

    //проверяет, оптимально ли текущее решение
    static boolean checkSolved(float matrix[][]){

        solved = true;
        for (int i = 0; i <= ReadFile.colCount; i++){
            if (matrix[i][0] < 0)
                solved = false;
        }
        if (solved){
            JOptionPane.showMessageDialog(null, " задача решена ");
            tempCInd = 0;
        }

        return solved;
    }

}
