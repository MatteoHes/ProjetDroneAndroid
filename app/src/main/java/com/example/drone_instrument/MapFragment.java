package com.example.drone_instrument;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MapFragment extends Fragment {

    String[][] data= new String[7][14400];
    int nb_ligne;
    SupportMapFragment supportMapFragment;
    ImageButton bp_refresh;

    DonneeFragment donneeFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        bp_refresh = (ImageButton) view.findViewById(R.id.button_refresh_map);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        donneeFragment.write_data(); // Ecriture des données dans la base de données Excel
        read_data(); // Lecture des données enregistrée dans la base de données
        ecriture_map(); // Affichage des points sur la map

        bp_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                donneeFragment.write_data();
                read_data();
                ecriture_map();
                Toast.makeText(getContext(), "Données actualisées", Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

    private void read_data() {

        File file = new File(getActivity().getExternalFilesDir("Save_Data"),"Drone_Data.xls");
        FileInputStream fileInputStream= null;
        Workbook workbook;

        try {

            fileInputStream = new FileInputStream(file);
            workbook = new HSSFWorkbook(fileInputStream);
            Sheet sheet;
            sheet = workbook.getSheetAt(0);
            nb_ligne = sheet.getLastRowNum();

            for (int i =1;i<nb_ligne;i++)
            {
                Row row = sheet.getRow(i+1);

                Cell cell_vitesse = row.getCell(1);
                data[0][i] = cell_vitesse.getStringCellValue();

                Cell cell_Temperature = row.getCell(2);
                data[1][i] = cell_Temperature.getStringCellValue();

                Cell cell_Luminosite = row.getCell(3);
                data[2][i] = cell_Luminosite.getStringCellValue();

                Cell cell_Son = row.getCell(4);
                data[3][i] = cell_Son.getStringCellValue();

                Cell cell_Longitude = row.getCell(5);
                data[4][i] = cell_Longitude.getStringCellValue();

                Cell cell_Latitude = row.getCell(6);
                data[5][i] = cell_Latitude.getStringCellValue();

                Cell cell_Altitude = row.getCell(7);
                data[6][i] = cell_Altitude.getStringCellValue();

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();
        }
    }
    private void ecriture_map()
    {
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                for (int i=1;i<nb_ligne;i++) // Affichage des points sur la carte en récupérant les données GPS stocké dans le tableau
                {
                    LatLng latLng = new LatLng(Double.parseDouble(data[5][i]),Double.parseDouble(data[4][i]));
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                    markerOptions.title(" Vit: "+data[0][i]+" Tem: "+data[1][i]+" Lum: "+data[2][i]+" Son: "+data[3][i]+ " Alt: "+data[6][i]);
                    googleMap.addMarker(markerOptions);
                }
            }
        });

    }
}