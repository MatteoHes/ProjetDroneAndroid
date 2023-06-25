package com.example.drone_instrument;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drone_instrument.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.Random;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Bundle bundle = new Bundle();
    ActivityMainBinding binding;
    DonneeFragment donneeFragment;
    Handler handlerHes;
    Handler handler2;
    Runnable run2;
    RefreshDataTask refreshDataTask;
    MapFragment mapFragment;

    String message=" ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handler2 = new Handler();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Création d'un Handler pour gérer le temps d'éxecution de la boucle
        handlerHes = new Handler();
        refreshDataTask = new RefreshDataTask();
        handlerHes.post(refreshDataTask);

        donneeFragment = new DonneeFragment();
        donneeFragment.setArguments(bundle);
        mapFragment = new MapFragment();
        replaceFragment(donneeFragment);


        //======================================= Affichage d'un fragment via la barre de navigation ========================================//
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.donnée: // Affichage de DonneeFragment
                    donneeFragment.removeHandler();
                    replaceFragment(donneeFragment);
                    break;
                case R.id.map: // Affichage de MapFragment
                    replaceFragment(mapFragment);
                    mapFragment.donneeFragment = donneeFragment;
                    break;
            }
            return true;
        });
    }

    private class RefreshDataTask implements Runnable {
        @Override

        public void run() {
            //Execution en continu des fonctions de récupération des données
            new DownloadDataLuminosity().execute("http://192.168.4.1/luminosity");
            new DownloadDataAltitude().execute("http://192.168.4.1/altitude");
            new DownloadDataLongitude().execute("http://192.168.4.1/longitude");
            new DownloadDataLatitude().execute("http://192.168.4.1/latitude");
            new DownloadDataHumidity().execute("http://192.168.4.1/humidity");
            new DownloadDataTemperature().execute("http://192.168.4.1/temperature");
            new DownloadDataVolume().execute("http://192.168.4.1/volume");
            new DownloadDataVitesse().execute("http://192.168.4.1/vitesse");
        }
    }

    private class DownloadDataLuminosity extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            StringBuilder result = new StringBuilder();
            try {
                //Récupération de l'URL donné
                URL url = new URL(urls[0]);
                //Connection à l'URL, ce qui effectue une requête HTTP_GET au serveur de l'ESP32
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                //Ces lignes servent à lire de grandes quantité de données de manière efficace
                //Mais cela marche aussi pour le simple string que nous transmettons
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                //Fin de la connection
                connection.disconnect();
                //Ecriture d'une eventuelle erreur
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            //On met le string dans un bundle pour le transmettre aux fragments
            bundle.putString("dataLuminosity",result);
            //On met un délai de 200ms. La boucle met environ 800ms a s'éxecuter sans,
            // or nous voulons qu'elle fasse environ 1 seconde.
            handlerHes.postDelayed(refreshDataTask, 200);
        }
    }
    private class DownloadDataAltitude extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            bundle.putString("dataAltitude",result);
            handlerHes.postDelayed(refreshDataTask, 0);
        }
    }
    private class DownloadDataLongitude extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            bundle.putString("dataLongitude",result);
            handlerHes.postDelayed(refreshDataTask, 0);
        }
    }
    private class DownloadDataLatitude extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            bundle.putString("dataLatitude",result);
            handlerHes.postDelayed(refreshDataTask, 0);
        }
    }
    private class DownloadDataHumidity extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            bundle.putString("dataHumidity",result);
            handlerHes.postDelayed(refreshDataTask, 0);
        }
    }
    private class DownloadDataTemperature extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            bundle.putString("dataTemperature",result);
            handlerHes.postDelayed(refreshDataTask, 0);
        }
    }
    private class DownloadDataVolume extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {

                    result.append(line);

                }
                reader.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            bundle.putString("dataVolume",result);
            handlerHes.postDelayed(refreshDataTask, 0);
        }
    }
    private class DownloadDataVitesse extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            //texte6.setText("Temperature :  " + result +"°C");
            bundle.putString("dataVitesse",result);
            handlerHes.postDelayed(refreshDataTask, 0);
            // Refresh every 1 second
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the callback to stop refreshing after activity is destroyed
        handlerHes.removeCallbacks(refreshDataTask);
    }
    private void replaceFragment (Fragment fragment) // Affichage d'un fragment
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}