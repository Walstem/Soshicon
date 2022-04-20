package com.example.soshicon.Registration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.soshicon.Check.NetCheck;
import com.example.soshicon.Check.UniqueNameCheck;
import com.example.soshicon.R;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class RegistrationName extends Fragment {

    private final String U_NICKNAME = "u_nickname";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_registration_name, container, false);

        Button onwards = (Button) root.findViewById(R.id.login_btn);

        onwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onwards(root);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

    //when you click on the onwards button, we start the registrationActivityPassword activity
    public void onwards(View view) throws IOException, ExecutionException, InterruptedException {
        if (NetCheck.StatusConnection(getContext())) {
            ViewToastMessage(view);
        }
        else {
            EditText username = (EditText) view.findViewById(R.id.username);
            String u_nickname = username.getText().toString();
            TextView MessageError = (TextView) view.findViewById(R.id.error_text);

            //запрос в бд на существование имени
            UniqueNameCheck uniq = new UniqueNameCheck();
            String answerUniq = uniq.uniqueness(u_nickname);

            //Если пользователь введет слишком короткое имя
            if(u_nickname.length() < 3) {
                String ErrorMess = getResources().getString(R.string.min_size_name_error);
                System.out.println(ErrorMess);
                alertError(username, MessageError, ErrorMess);
            }
            //Если пользователь введет уже существующие имя
            else if(answerUniq.equals(u_nickname)) {
                String ErrorMess = getResources().getString(R.string.name_taken_1) + u_nickname + getResources().getString(R.string.name_taken_2);;
                alertError(username, MessageError, ErrorMess);
            }
            //Если все будет верно

            else {
                //Переход на фрагмент создания пароля
                SharedPreferences sp = getContext().getSharedPreferences("user_data", getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString(U_NICKNAME, u_nickname);
                editor.apply();

                RegistrationPassword rp = new RegistrationPassword();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(this.getId(), rp);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }

    //анимация edittext, если пользователь ошибется
    public void alertError(EditText filed, TextView MessageError ,String message) {
        final Animation shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.error_shake);

        //анимация
        filed.startAnimation(shakeAnimation);
        filed.setBackground(getResources().getDrawable(R.drawable.anim_et_changecolor));

        //сообщение о ошибке
        MessageError.setText(message);
        MessageError.setVisibility(View.VISIBLE);
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