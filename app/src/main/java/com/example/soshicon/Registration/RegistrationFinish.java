package com.example.soshicon.Registration;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.soshicon.BottomNavigation.Response;
import com.example.soshicon.Check.NetCheck;
import com.example.soshicon.FileHanler.AppSpecificFiles;
import com.example.soshicon.R;
import com.example.soshicon.SQLUtils.SQLUtils;
import com.example.soshicon.asynctasks.SendQuery;

import java.io.FileNotFoundException;
import java.io.IOException;

public class RegistrationFinish extends Fragment {

    private final String U_NICKNAME = "u_nickname";
    final String ID = "ID";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_registration_finish, container, false);

        Button finish = (Button) root.findViewById(R.id.finish);
        try {
            Welcome(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    finish(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

    //we read the data written to the file and display the greeting line of the new user
    public void Welcome(View view) throws IOException {
        //излекаем данные регистрации из файла
        TextView welcome = (TextView) view.findViewById(R.id.welcome);
        SharedPreferences sp = getContext().getSharedPreferences("user_data", getContext().MODE_PRIVATE);

        String welcomeText = sp.getString(U_NICKNAME, "") + ", добро \n пожаловать в Soshicon!";
        welcome.setText(welcomeText);
    }

    //when you click on the finish button, we start the main_activity
    public void finish(View view) throws IOException {
        if (NetCheck.StatusConnection(getContext())) {
            ViewToastMessage(view);
        } else {
            //излекаем данные регистрации из файла
            String[] DataArr = extraction();

            //добавляем данные в бд
            String UserData = new SQLUtils(DataArr).InsertRegData();
            System.out.println(UserData);
            new SendQuery("input_request_handler.php").execute(UserData);

            try {
                //получаем id пользователя
                SendQuery request = new SendQuery("get_id.php");
                request.execute("?name=" + DataArr[0]);
                String id = request.get();

                //добавляем id пользователя в сохраненные настрйоки
                SharedPreferences sp = getContext().getSharedPreferences("user_data", getContext().MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putString(ID, id);
                ed.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Окончание регистрации
            Response r = new Response();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(this.getId(), r);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public String[] extraction() throws FileNotFoundException {
        String Data = new AppSpecificFiles().ReadFile(getContext().getApplicationContext(), "/user_data.txt");
        return Data.split(" ");
    }

    public void ViewToastMessage(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_internet_message,(ViewGroup) view.findViewById(R.id.toast_layout_root));
        Toast toast = new Toast(getContext().getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
