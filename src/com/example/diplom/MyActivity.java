package com.example.diplom;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

class MyActivity extends Activity {
    TextView countText;
    EditText relax;
    LinearLayout startPointContainer;
    GridLayout matrixContainer;

    private ProgressDialog progress;

    private int seekBarValue = 2;
    private KachmazModify kazchmaz;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SeekBar countSeekBar = (SeekBar) findViewById(R.id.count_seekbar);

        countText = (TextView) findViewById(R.id.countText);
        relax = (EditText) findViewById(R.id.relaxFunctionEdit);
        startPointContainer = (LinearLayout) findViewById(R.id.startPointContainer);
        matrixContainer = (GridLayout) findViewById(R.id.matrixContainer);

        countSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarValue = i+2;
                countText.setText(String.valueOf(seekBarValue));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateCount();
            }
        });
        updateCount();

        Button calc = (Button)findViewById(R.id.calcButton);
        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCalc();
            }
        });


    }

    private void startCalc(){

        kazchmaz = new KachmazModify();
        double[] startPoint=new double[3];
        boolean isStartPointFull=true;
        for(int i=0;i<startPointContainer.getChildCount();i++){

            EditText v=(EditText)startPointContainer.getChildAt(i);
            String s=v.getText().toString();
            if(s.isEmpty()){
                isStartPointFull=false;
            break;
            }
            startPoint[i]=Double.valueOf(s);
        }


        if(isStartPointFull)
            kazchmaz.setStartPoint(startPoint);

        String r=relax.getText().toString();
        if(!r.isEmpty())
            kazchmaz.setRelaxationFunction(Double.valueOf(r));

        progress = ProgressDialog.show(this,null,"Please wait...", true);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                kazchmaz.calc();
//                publishProgress();
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                progress.dismiss();
                ResultDialog result = new ResultDialog();
                result.setKazchmaz(kazchmaz);
                result.show(getFragmentManager(), null);
            }
        }.execute(null);
    }

    private void updateCount() {
//        int count = seekBarValue;
        int count = 3;

        countText.setText(String.valueOf(count));

        startPointContainer.removeAllViews();
        matrixContainer.removeAllViews();

        matrixContainer.setColumnCount(count + 1);
        matrixContainer.setRowCount(count);

        for (int i = 0; i < count; i++) {
            View v = View.inflate(this, R.layout.item_matrix, null);
            startPointContainer.addView(v);
        }

        for (int i = 0; i < count * (count + 1); i++) {
            View v = View.inflate(this, R.layout.item_matrix, null);
            matrixContainer.addView(v);
        }
    }

    class ResultDialog extends DialogFragment{

        private KachmazModify kazchmaz;
        private TextView resultTime;
        private long startTime;

        public void setKazchmaz(KachmazModify kazchmaz){
            this.kazchmaz = kazchmaz;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = View.inflate(MyActivity.this, R.layout.result_dialog, null);
            double[] result = kazchmaz.getResult();

            ViewGroup resultContainer = (ViewGroup)v.findViewById(R.id.result_resultContainer);
            for(int i=0;i<result.length;i++){
                TextView res = (TextView)View.inflate(getActivity(), R.layout.item_matrix_result, null);
                res.setText(String.valueOf(result[i]));
                resultContainer.addView(res);
            }

            TextView error = (TextView)v.findViewById(R.id.result_error);
            error.setText("Result error: " + kazchmaz.getError());

            TextView iteration = (TextView)v.findViewById(R.id.result_iterationCount);
            iteration.setText("Iteration count: " + kazchmaz.getIterationNumber());

            TextView resultTime = (TextView)v.findViewById(R.id.result_time);
            resultTime.setText("Time of calc: " + kazchmaz.getResultTime() + "ms");

            Button ok = (Button)v.findViewById(R.id.result_okButton);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResultDialog.this.dismiss();
                }
            });

            return v;
        }
    }
}
