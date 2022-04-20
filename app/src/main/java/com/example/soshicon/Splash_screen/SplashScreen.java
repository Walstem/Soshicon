package com.example.soshicon.Splash_screen;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.soshicon.BottomNavigation.Account;
import com.example.soshicon.MainActivity;
import com.example.soshicon.Registration.Authorization;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            int a = 1;
            SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
            String id = sp.getString("ID", "");
            Intent intent;

            if (id.equals("0")) {
                intent = new Intent(this, Authorization.class);
            }
            else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
        }
        catch (Exception e){
            Intent intent = new Intent(this, Authorization.class);
            startActivity(intent);
            System.out.println("Eror");
        }
        this.finish();
    }
}