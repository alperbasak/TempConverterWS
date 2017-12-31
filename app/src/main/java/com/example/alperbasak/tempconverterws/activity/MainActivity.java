package com.example.alperbasak.tempconverterws.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.alperbasak.tempconverterws.R;
import com.example.alperbasak.tempconverterws.util.Util;
import org.xmlpull.v1.XmlPullParserException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;


public class MainActivity extends Activity {

    private EditText editTemp, editTo, editFrom;
    private Button btnConv;
    private TextView txtResult;
    private String strTemp,strFrom,strTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        manageActions();
    }

    private void manageActions() {

        btnConv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.isTextEmpty(editTemp)) {
                    strTemp = editTemp.getText().toString();
                    if (!Util.isTextEmpty(editFrom)) {
                        strFrom = editFrom.getText().toString();
                        if (!Util.isTextEmpty(editTo)) {
                            strTo = editTo.getText().toString();
                            if(Util.getInstance(MainActivity.this).isInternetActive()){

                                AsyncTaskWSCall asyncTaskWSCall=new AsyncTaskWSCall();
                                asyncTaskWSCall.execute();

                            }else {Util.getInstance(MainActivity.this).showMessage("Check your internet connection");}

                        } else {
                            Util.getInstance(MainActivity.this).showMessage("To cannot be left blank");
                        }
                    } else {
                        Util.getInstance(MainActivity.this).showMessage("From cannot be left blank");
                    }
                } else {
                    Util.getInstance(MainActivity.this).showMessage("Temperature cannot be left blank");
                }
            }
        });
    }

    private void initComponents() {
        editTemp= (EditText) findViewById(R.id.edit_temp);
        editFrom= (EditText) findViewById(R.id.edit_from);
        editTo= (EditText) findViewById(R.id.edit_to);
        btnConv= (Button) findViewById(R.id.btn_convert);
        txtResult= (TextView) findViewById(R.id.txt_result);
    }

    private class AsyncTaskWSCall extends AsyncTask<Void,Void,String>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Connection Screen");
            progressDialog.setMessage("Connecting to the WS");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result=null;

            try {
                result=manageWS(strTemp,strFrom,strTo);
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            txtResult.setText(s);
        }

        private String manageWS(String strTemp, String strFrom, String strTo) throws IOException, XmlPullParserException {

            //1. Taşıma katmanı oluşturuldu
            HttpTransportSE httpTransportSE=new HttpTransportSE(getResources().getString(R.string.URL));

            //2. Request-responseları wraplayacak envolopeu oluşturduk
            SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;

            //3.Requestin oluşturulması
            SoapObject request=new SoapObject(getResources().getString(R.string.NAME_SPACE),getResources().getString(R.string.METHOD_NAME));
            request.addProperty("Temperature",strTemp);
            request.addProperty("FromUnit",strFrom);
            request.addProperty("ToUnit",strTo);

            //4. Envelope'a request yüklenmesi
            envelope.setOutputSoapObject(request);

            //5. Webservis çağırıldı
            httpTransportSE.call(getResources().getString(R.string.SOAP_ACTION),envelope);

            //6. Response u çekiyoruz
            SoapPrimitive soapPrimitive= (SoapPrimitive) envelope.getResponse();
            return soapPrimitive.toString();
        }
    }
}
