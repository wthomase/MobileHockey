package com.project.tcss450.wthomase.mobilehockey;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.tcss450.wthomase.mobilehockey.authenticate.SignInActivity;

public class LoginFragment extends Fragment {

    private final String USER_LOGIN_URL =
            "http://cssgate.insttech.washington.edu/~wthomase/login.php?";

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_login, container, false);
        final EditText userIdText = (EditText) v.findViewById(R.id.edittext_login_username);
        final EditText pwdText = (EditText) v.findViewById(R.id.edittext_login_password);
        Button signInButton = (Button) v.findViewById(R.id.login_button);
        Button signInGuestButton = (Button) v.findViewById(R.id.login_guest_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userIdText.getText().toString();
                String pwd = pwdText.getText().toString();
                if (TextUtils.isEmpty(userId))  {
                    Toast.makeText(v.getContext(), "Enter userid"
                            , Toast.LENGTH_SHORT)
                            .show();
                    userIdText.requestFocus();
                    return;
                }
                if (!userId.contains("@")) {
                    Toast.makeText(v.getContext(), "Enter a valid email address"
                            , Toast.LENGTH_SHORT)
                            .show();
                    userIdText.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(pwd))  {
                    Toast.makeText(v.getContext(), "Enter password"
                            , Toast.LENGTH_SHORT)
                            .show();
                    pwdText.requestFocus();
                    return;
                }
                if (pwd.length() < 6) {
                    Toast.makeText(v.getContext(), "Enter password of at least 6 characters"
                            , Toast.LENGTH_SHORT)
                            .show();
                    pwdText.requestFocus();
                    return;
                }

                String URL = loginUserURL(v, userId, pwd);
                ((SignInActivity) getActivity()).login(userId, pwd, URL);
            }
        });

        signInGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LoginMenuActivity.class);
                getActivity().startActivity(intent);

                Toast.makeText(getActivity().getApplicationContext(), "Logged in as Guest",
                        Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    private String loginUserURL(View v, String userid, String pwd) {

        StringBuilder sb = new StringBuilder(USER_LOGIN_URL);

        try {

            sb.append("email=");
            sb.append(userid);

            sb.append("&pwd=");
            sb.append(pwd);

            Log.i("Generated Login URL:", sb.toString());

        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    public interface LoginInteractionListener {
        public void login(String userId, String pwd, String URL);
    }

}
