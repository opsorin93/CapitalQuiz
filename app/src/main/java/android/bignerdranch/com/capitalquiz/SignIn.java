package android.bignerdranch.com.capitalquiz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignIn extends AppCompatActivity {

    static final int RC_SIGN_IN = 0;
    // declaring authorized User
    FirebaseAuth mAuth;

    // declaring Widgets
    Button signIN, signOUT, btn_Info, btn_Guest, btn_Play;
    ProgressBar progressBar;
    GoogleSignInClient mGoogleSignInClient;
    ImageView userImage;
    ImageView img;
    TextView name, profileInfo, noImageText;

    //declaring variables
    String authUsername;
    String username;
    String email;
    Uri personPhoto;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_sign_in );

        profileInfo = findViewById ( R.id.profileInfo );
        noImageText = findViewById ( R.id.noImageText );
        name = findViewById ( R.id.userName );
        img = findViewById ( R.id.image );
        userImage = findViewById ( R.id.userImage );
        btn_Play = findViewById ( R.id.btn_Play );
        btn_Guest = findViewById ( R.id.btn_Guest );
        btn_Info = findViewById ( R.id.btn_Info );
        signIN = findViewById ( R.id.btn_singIN );
        signOUT = findViewById ( R.id.btn_signOut );
        progressBar = findViewById ( R.id.progressBar );


        // Start Configuring Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder ( GoogleSignInOptions.DEFAULT_SIGN_IN )
                .requestIdToken ( getString ( R.string.default_web_client_id ) )
                .requestEmail ( )
                .build ( );
        mGoogleSignInClient = GoogleSignIn.getClient ( this, gso );
        //end of configuration

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance ( );

        // on click execute method singIN
        signIN.setOnClickListener ( v -> {
            signIn ( );
        } );

        // on click execute method singOUT
        signOUT.setOnClickListener ( v -> {
            signOut ( );
        } );

        // on click opens Activity Menu
        btn_Play.setOnClickListener ( v -> {
            Intent intent = new Intent ( getApplicationContext ( ), Menu.class );
            intent.putExtra("authUsername", authUsername);
            startActivity ( intent );
        } );

        //on click prompts the user with an alertDialog asking him to enter a nickname
        btn_Guest.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder ( SignIn.this, R.style.AlertDialogTheme );
                View mView = SignIn.this.getLayoutInflater ( ).inflate ( R.layout.username_dialog, null );
                final EditText nickname = mView.findViewById ( R.id.username );

                builder.setPositiveButton ( "Enter", new DialogInterface.OnClickListener ( ) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                } );
                builder.setNegativeButton ( "Back", new DialogInterface.OnClickListener ( ) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                } );
                builder.setView ( mView );
                AlertDialog dialog = builder.create ( );
                dialog.show ( );
                dialog.getButton ( AlertDialog.BUTTON_POSITIVE ).setOnClickListener ( new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View v) {
                        // check if the user has a enter a nickname
                        if(!nickname.getText ().toString ().isEmpty ()) {
                            username = nickname.getText().toString ();
                            Toast.makeText ( SignIn.this, "Hello " +username, Toast.LENGTH_SHORT ).show ( );
                            Intent intent = new Intent ( getApplicationContext ( ), Menu.class );
                            intent.putExtra("username", username);
                            startActivity ( intent );
                            dialog.dismiss ();
                        }else{
                            Toast.makeText ( SignIn.this, "Please enter a nickname", Toast.LENGTH_SHORT ).show ( );
                        }
                    }
                } );
            }
        } );

        // on click prompt the user with an alertDialog with information about the quiz/game
        btn_Info.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder ( SignIn.this, R.style.AlertDialogTheme );
                View mView = SignIn.this.getLayoutInflater ( ).inflate ( R.layout.info_dialog, null );

                builder.setPositiveButton ( "Return", new DialogInterface.OnClickListener ( ) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                } );
                builder.setView ( mView );
                AlertDialog dialog = builder.create ( );
                dialog.show ( );
            }
        } );

    }

    @Override
    protected void onStart() {
        super.onStart ( );
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser ( );
        if (mAuth.getCurrentUser () != null) {
            updateUI ( currentUser );
            btn_Guest.setVisibility ( View.INVISIBLE );
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent ( data );
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult ( ApiException.class );
                firebaseAuthWithGoogle ( account );
            } catch (ApiException e) {
                // Check if user is signed in (non-null) and update UI accordingly.
                updateUI ( null );
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential ( acct.getIdToken ( ), null );
        mAuth.signInWithCredential ( credential )
                .addOnCompleteListener ( this, new OnCompleteListener<AuthResult> ( ) {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful ( )) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser ( );
                            updateUI ( user );
                            Toast.makeText ( getApplicationContext ( ), "Signed In Successfully", Toast.LENGTH_SHORT ).show ( );
                        } else {
                            // If sign in fails toast the user with a message
                            Toast.makeText ( SignIn.this, "Sign In Unsuccessfully", Toast.LENGTH_SHORT ).show ( );
                            updateUI ( null );
                        }
                    }
                } );
    }

    /*
     The method below sings the user in and updates the UI accordingly
   */
    private void signIn() {

        Intent singInIntent = mGoogleSignInClient.getSignInIntent ( );
        startActivityForResult ( singInIntent, RC_SIGN_IN );

        progressBar.setVisibility ( View.VISIBLE );
        signOUT.setVisibility ( View.VISIBLE );

        btn_Guest.setVisibility ( View.INVISIBLE );
        signIN.setVisibility ( View.INVISIBLE );

    }

    /*
    The method below sings the user out and updates the UI accordingly
   */
    private void signOut() {
        // Firebase sing out
        mAuth.signOut ( );
        // Google sign out
        mGoogleSignInClient.signOut ( ).addOnCompleteListener ( this,
                task -> updateUI ( null ) );
        Toast.makeText ( this, "You Signed Out", Toast.LENGTH_SHORT ).show ( );

        signIN.setVisibility ( View.VISIBLE );
        btn_Guest.setVisibility ( View.VISIBLE );
        img.setVisibility ( View.VISIBLE );

        progressBar.setVisibility ( View.INVISIBLE );
        signOUT.setVisibility ( View.INVISIBLE );
        name.setVisibility ( View.INVISIBLE );
        userImage.setVisibility ( View.INVISIBLE );
        profileInfo.setVisibility ( View.INVISIBLE );
        noImageText.setVisibility ( View.INVISIBLE );
    }

    /*
     The method below updates the UI with user information
   */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount ( this );
            Toast.makeText ( getApplicationContext ( ), "Signed In Successfully", Toast.LENGTH_SHORT ).show ( );

            authUsername = account.getDisplayName ( );
            email = account.getEmail ( );
            personPhoto = account.getPhotoUrl ( );

            if (personPhoto == null) {
                noImageText.setVisibility ( View.VISIBLE );
            } else {
                Glide.with ( getApplicationContext ( ) ).load ( String.valueOf ( personPhoto ) ).into ( userImage );
                userImage.setVisibility ( View.VISIBLE );
            }

            signOUT.setVisibility ( View.VISIBLE );
            profileInfo.setVisibility ( View.VISIBLE );
            name.setVisibility ( View.VISIBLE );
            name.setText ( email );
            Glide.with ( getApplicationContext ( ) ).load ( String.valueOf ( personPhoto ) ).into ( userImage );
            progressBar.setVisibility ( View.INVISIBLE );
            img.setVisibility ( View.INVISIBLE );
        }
    }
}





