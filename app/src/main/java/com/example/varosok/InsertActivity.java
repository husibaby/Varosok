package com.example.varosok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

public class InsertActivity extends AppCompatActivity {

    private EditText editTextVaros, editTextOrszag, editTextLakossag;
    private Button buttonVissza, buttonFelvetel ;
    private String url = "https://retoolapi.dev/m3a55W/varosok";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        init();

        buttonVissza.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent= new Intent(InsertActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonFelvetel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addVaros();
            }
        });
    }

    private void init()
    {
        buttonFelvetel=findViewById(R.id.buttonAdd);
        buttonVissza= findViewById(R.id.buttonBack);
        editTextVaros=findViewById(R.id.editTextVaros);
        editTextOrszag=findViewById(R.id.editTextOrszag);
        editTextLakossag=findViewById(R.id.editTextLakossag);
    }

    private void addVaros(){
        String nev = editTextVaros.getText().toString();
        String orszag = editTextOrszag.getText().toString();
        String lakossag = editTextLakossag.getText().toString();

        boolean valid = validation();

        if (valid){
            Toast.makeText(this, "Minden mező kitöltése kötelező", Toast.LENGTH_SHORT).show();
            return;
        }

        int lakossag_Int = Integer.parseInt(lakossag);
        City city = new City(0,nev,orszag,lakossag_Int);
        Gson jsonConverter = new Gson();
        RequestTask task = new RequestTask(url, "POST", jsonConverter.toJson(city));
        task.execute();
    }

    private boolean validation(){
        if (editTextVaros.getText().toString().isEmpty()||editTextOrszag.getText().toString().isEmpty()||editTextLakossag.getText().toString().isEmpty())
            return true;
        else
            return false;

    }

    class RequestTask extends AsyncTask<Void, Void, Response>{
        private String requestUrl;
        private String requestType;
        private String requestParams;

        public RequestTask(String requestUrl, String requestType, String requestParams) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }
        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                switch (requestType) {
                    case "POST":
                        response = RequestHandler.post(requestUrl, requestParams);
                        break;
                }
            } catch (IOException e) {
                Toast.makeText(InsertActivity.this,
                        e.toString(), Toast.LENGTH_SHORT).show();
            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buttonFelvetel.setEnabled(false);
        }
        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            buttonFelvetel.setEnabled(true);
            if (response.getResponseCode() >= 400) {
                Toast.makeText(InsertActivity.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
                return;
            }
            if (requestType.equals("POST")) {
                Toast.makeText(InsertActivity.this, "Sikeres adatfelvétel", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InsertActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }
}