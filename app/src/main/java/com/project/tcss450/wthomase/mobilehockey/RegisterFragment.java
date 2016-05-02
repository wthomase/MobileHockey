package com.project.tcss450.wthomase.mobilehockey;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.tcss450.wthomase.mobilehockey.authenticate.RegisterActivity;

public class RegisterFragment extends Fragment {

    private final String USER_REGISTER_URL =
            "http://cssgate.insttech.washington.edu/~wthomase/addUser.php?";

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_register, container, false);
        final EditText userIdText = (EditText) v.findViewById(R.id.edittext_register_username);
        final EditText pwdText = (EditText) v.findViewById(R.id.edittext_register_password);
        Button registerButton = (Button) v.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
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

                String url = addUserURL(v, userIdText.getText().toString(), pwdText.getText().toString());

                ((RegisterActivity) getActivity()).register(userId, pwd, url);
            }
        });

        return v;
    }

    private String addUserURL(View v, String userid, String pwd) {

        StringBuilder sb = new StringBuilder(USER_REGISTER_URL);

        try {

            sb.append("email=");
            sb.append(userid);

            sb.append("&pwd=");
            sb.append(pwd);

            Log.i("Generated Register URL:", sb.toString());

        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    public interface RegisterInteractionListener {
        public void register(String userId, String pwd, String URL);
    }

}
