package nvy.reader.qrcode.qrcode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;



public class MainActivity extends AppCompatActivity {
    Button scancode;
    long start,total;
    String  formattedDate1;
    ListView lv;
    TextView textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = this;
        setContentView(R.layout.activity_main);

        start = System.currentTimeMillis();
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.CANADA);

        formattedDate1 = df1.format(c.getTime());

        scancode = (Button) findViewById(R.id.button);
        scancode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan QR code");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
                writeToFile(formattedDate1,getBaseContext());
                readFromFile(getBaseContext());
            }
        });

        lv = (ListView) findViewById(R.id.listView);
        final ArrayList<String> list = new ArrayList<String>();
        final ArrayAdapter<String> at=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,list){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);
                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                // Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.BLACK);
                // Generate ListView Item using TextView
                return view;
            }
        };

        lv.setAdapter(at);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Log.d(result.getContents() + "hi", "");
        if (result.getContents() != null) {
            long end = System.currentTimeMillis();
            Log.d("TAG", "Found barcode in " + (end - start) + " ms");
            //Toast.makeText(this, "It took you " + (end - start) + " ms to wake up", Toast.LENGTH_SHORT).show();
            total = end - start;
            String hey = Long.toString(total);
            writeToFile(formattedDate1 +" : QRCode :"+ hey +"time taken to scan " +total, getBaseContext());
            readFromFile(getBaseContext());
            //Toast.makeText(this, readFromFile(getBaseContext())+"Read file", Toast.LENGTH_SHORT).show();
            Log.d(readFromFile(getBaseContext()), "read file");
            //Toast.makeText(this, result.getContents()+ "", Toast.LENGTH_SHORT).show();


            final ArrayList<String> list = new ArrayList<String>();
            final ArrayAdapter<String> at=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,list){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    // Get the Item from ListView
                    View view = super.getView(position, convertView, parent);
                    // Initialize a TextView for ListView each Item
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    // Set the text color of TextView (ListView Item)
                    tv.setTextColor(Color.BLACK);
                    // Generate ListView Item using TextView
                    return view;
                }
            };
            list.add(" Date : "+formattedDate1 +"\n QRCode : "+ result.getContents() +"\n Time taken to scan: " +total+".");
            lv.setAdapter(at);

            textview = (TextView) findViewById(R.id.textView);
            textview.setText(formattedDate1 +"\n QRCode : "+ result.getContents() +"\n Time taken to scan: " +total +"ms");

            /*finish();
                System.exit(0);
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);*/
        }
    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput("config.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }


    @Override
    protected void onPause() {
        super.onPause();

    }
}

