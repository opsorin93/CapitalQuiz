package android.bignerdranch.com.capitalquiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class Menu extends AppCompatActivity {

    public String authUsername;
    public String username;

    // Populates the view with the design from the resource file
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        // We retrieve the Authorize Username and the guest User from the SignIn activity
        authUsername = getIntent().getStringExtra("authUsername");
        username = getIntent ().getStringExtra ( "username" );
    }

       /*
      The method below overrides the phone Back button and toast the user with a message
       */
    @Override
    public void onBackPressed() {
        Toast.makeText (this,"Please use the back arrow button provided", Toast.LENGTH_SHORT).show();
    }
}

