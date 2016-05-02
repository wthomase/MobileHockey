package com.project.tcss450.wthomase.mobilehockey.authenticate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.project.tcss450.wthomase.mobilehockey.LoginFragment;
import com.project.tcss450.wthomase.mobilehockey.LoginMenuActivity;
import com.project.tcss450.wthomase.mobilehockey.MainMenuActivity;
import com.project.tcss450.wthomase.mobilehockey.R;

public class SignInActivity extends AppCompatActivity implements LoginFragment.LoginInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new LoginFragment() )
                .commit();

        setContentView(R.layout.activity_sign_in);
    }

    public void login(String userid, String pwd) {
        Intent intent = new Intent(this, LoginMenuActivity.class);
        startActivity(intent);
        finish();
    }

}
