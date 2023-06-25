package com.example.drone_instrument;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class DonneeFragment extends Fragment {

    Bundle bundle;

    TextView long_msg,lati_msg,vit_msg,temp_msg,time_msg,son_msg,alti_msg, cmd_drone;

    ImageView img_vit,img_son,img_lum,img_temp;

    String valLongitude, valLatitude, cmd_latitude,cmd_longitude;
    LocationManager locationManager;

    Workbook excel_file = new HSSFWorkbook();
    Cell cell = null;
    Row row = null;
    Sheet sheet = excel_file.createSheet("data");
    int cpt;
    String[][] data_save = new String[8][14400];
    File myExternfile = null;
    FileOutputStream fileOutputStream = null;

    Random random;

    Handler handler;
    Runnable run;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donnee, container, false);

        //===================================== Initialisation des textView =============================================//
        bundle = getArguments();
        long_msg = (TextView) view.findViewById(R.id.longitude);
        lati_msg = (TextView) view.findViewById(R.id.latitude);
        vit_msg = (TextView) view.findViewById(R.id.vitesse);
        temp_msg = (TextView) view.findViewById(R.id.temperature);
        time_msg = (TextView) view.findViewById(R.id.luminosite);
        son_msg = (TextView) view.findViewById(R.id.son);
        alti_msg = (TextView) view.findViewById(R.id.altitude);
        cmd_drone = (TextView) view.findViewById(R.id.commande_drone);

        img_lum = (ImageView) view.findViewById(R.id.imageLuminosite);
        img_son = (ImageView) view.findViewById(R.id.imageSon);
        img_temp = (ImageView) view.findViewById(R.id.imageTemperature);
        img_vit = (ImageView) view.findViewById(R.id.imageVitesse);

        myExternfile = new File(getActivity().getExternalFilesDir("Save_Data"),"Drone_Data.xls");
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        random = new Random();
        handler = new Handler();

        //========================== Affichage du graph en fonction de la donnée souhaiter ============================//
        img_vit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Vitesse");
            }
        });

        img_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Temperature");
            }
        });

        img_lum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Luminosite");
            }
        });

        img_son.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Son");
            }
        });
        vit_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Vitesse");
            }
        });

        temp_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Temperature");
            }
        });

        time_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Luminosite");
            }
        });

        son_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Son");
            }
        });

        //======================== Affichage des données recue en temp réel (aléatoire) ==========//
        run = new Runnable() {

            @Override
            public void run() {

                data_save[0][cpt] = String.valueOf(cpt); // Temps

                String valVitesse= bundle.getString("dataVitesse");// Vitesse
                vit_msg.setText(String.valueOf(valVitesse));
                data_save[1][cpt] = String.valueOf(valVitesse);

                String msgTemp= bundle.getString("dataTemperature"); //Temperature
                temp_msg.setText(String.valueOf(msgTemp));
                data_save[2][cpt] = String.valueOf(msgTemp);

                String ValLum= bundle.getString("dataLuminosity"); //Luminosite
                time_msg.setText(ValLum);
                data_save[3][cpt] = ValLum;

                String valVolume = bundle.getString("dataVolume"); //Son
                son_msg.setText(valVolume);
                data_save[4][cpt] =valVolume;

                valLongitude = bundle.getString("dataLongitude"); //Longitude
                long_msg.setText(valLongitude);
                data_save[5][cpt] = valLongitude;

                valLatitude = bundle.getString("dataLatitude"); //Latitude
                lati_msg.setText(valLatitude);
                data_save[6][cpt] = valLatitude;

                String valAltitude= bundle.getString("dataAltitude"); //Altitude
                alti_msg.setText(valAltitude);
                data_save[7][cpt] = valAltitude;

                cpt++;

                handler.postDelayed(this,1000);

            }
        };
        handler.post(run);
     //   commande_drone();
        return view;
    }

    private void Graph_data (String val) // Affichage du graph_fragment
    {
        Bundle bundle = new Bundle();
        GraphFragment graphFragment = new GraphFragment();
        graphFragment.donneeFragment = this;
        bundle.putString("data",val);
        graphFragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,graphFragment);
        fragmentTransaction.commit();
    }

    void write_data () //Ecriture de données dans un fichier excel
    {

        row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("Time");

        cell = row.createCell(1);
        cell.setCellValue("Vitesse");

        cell = row.createCell(2);
        cell.setCellValue("Temperature");

        cell = row.createCell(3);
        cell.setCellValue("Luminosite");

        cell = row.createCell(4);
        cell.setCellValue("Son");

        cell = row.createCell(5);
        cell.setCellValue("Longitude");

        cell = row.createCell(6);
        cell.setCellValue("Latitude");

        cell = row.createCell(7);
        cell.setCellValue("Altitude");

        sheet.setColumnWidth(0,(20*200));
        sheet.setColumnWidth(1,(20*200));
        sheet.setColumnWidth(2,(20*200));
        sheet.setColumnWidth(3,(20*200));
        sheet.setColumnWidth(4,(20*300));
        sheet.setColumnWidth(5,(20*300));
        sheet.setColumnWidth(6,(20*300));
        sheet.setColumnWidth(7,(20*300));

        for (int i =0;i<cpt;i++)
        {
            row = sheet.createRow(i+1);

            cell = row.createCell(0);
            cell.setCellValue(data_save[0][i]);

            cell = row.createCell(1);
            cell.setCellValue(data_save[1][i]);

            cell = row.createCell(2);
            cell.setCellValue(data_save[2][i]);

            cell = row.createCell(3);
            cell.setCellValue(data_save[3][i]);

            cell = row.createCell(4);
            cell.setCellValue(data_save[4][i]);

            cell = row.createCell(5);
            cell.setCellValue(data_save[5][i]);

            cell = row.createCell(6);
            cell.setCellValue(data_save[6][i]);

            cell = row.createCell(7);
            cell.setCellValue(data_save[7][i]);
        }

        try {
            fileOutputStream = new FileOutputStream(myExternfile);
            excel_file.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeHandler()
    {
        handler.removeCallbacks(run);
    }

    private void commande_drone() {

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                cmd_drone.setText(location.getLatitude() + " " + location.getLongitude());

                if(Double.parseDouble(valLatitude) > location.getLatitude())
                {
                    cmd_latitude = "Ouest";
                }
                else if (Double.parseDouble(valLatitude) < location.getLatitude())
                {
                    cmd_latitude = "Est";
                }
                else
                {
                    cmd_latitude = " ";
                }

                if(Double.parseDouble(valLongitude) > location.getLongitude())
                {
                    cmd_longitude = "Nord";
                }
                else if (Double.parseDouble(valLongitude) < location.getLongitude())
                {
                    cmd_longitude = "Sud";
                }
                else
                {
                    cmd_longitude = " ";
                }

                cmd_drone.setText(cmd_longitude + " " + cmd_latitude);

            }
        });
    }
}