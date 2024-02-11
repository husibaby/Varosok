package com.example.varosok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private Button buttonVissza;
    private ListView listViewLista;
    private List<City> varosokList = new ArrayList<>();
    private String url = "https://retoolapi.dev/m3a55W/varosok";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        init();
        RequestTask task = new RequestTask(url, "GET");
        task.execute();
        buttonVissza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent= new Intent(ListActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void init()
    {
        buttonVissza= findViewById(R.id.buttonVissza);
        listViewLista=findViewById(R.id.listViewLista);
        listViewLista.setAdapter(new MenuAdapter());
    }

    private class MenuAdapter extends ArrayAdapter<City> {

        public MenuAdapter() {
            super(ListActivity.this, R.layout.city_list_items, varosokList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //inflater létrehozása
            LayoutInflater inflater = getLayoutInflater();
            //view létrehozása a city_list_items.xml-ből
            View view = inflater.inflate(R.layout.city_list_items, null, false);
            //city_list_items.xml-ben lévő elemek inicializálása
            TextView textViewName = view.findViewById(R.id.textViewName);
            TextView textViewCountry = view.findViewById(R.id.textViewCountry);
            TextView textViewPeople = view.findViewById(R.id.textViewPeople);
            //actualCity létrehozása a városok listából
            City actualCity = varosokList.get(position);

            textViewName.setText(actualCity.getNev());
            textViewCountry.setText(actualCity.getOrszag());
            textViewPeople.setText(String.valueOf(actualCity.getLakossag()));

            return view;
        }
    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {
        private String requestUrl;
        private String requestType;
        private String requestParams;

        public RequestTask(String requestUrl, String requestType, String requestParams) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }

        public RequestTask(String requestUrl, String requestType) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                if (requestType.equals("GET")) {
                    response = RequestHandler.get(requestUrl);
                }
            } catch (IOException e) {
                Toast.makeText(ListActivity.this,
                        e.toString(), Toast.LENGTH_SHORT).show();
            }
            return response;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response response){
            Gson converter = new Gson();
            super.onPostExecute(response);
            if(response.getResponseCode() >= 400){
                Toast.makeText(ListActivity.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
                Log.d("onPostExecuteError:", response.getContent());
            }
            if (requestType.equals("GET")) {
                //válasz feldolgozása
                City[] cityArray = converter.fromJson(
                        response.getContent(), City[].class);
                //város lista feltöltése a válasz elemeivel
                varosokList.clear();
                varosokList.addAll(Arrays.asList(cityArray));
                //adapter értesítése az adatváltozásról (újra kell rajzolni a listát)
                listViewLista.invalidateViews();
                Toast.makeText(ListActivity.this, "Sikeres város lista lekérdezés", Toast.LENGTH_SHORT).show();
            }
        }
    }
}