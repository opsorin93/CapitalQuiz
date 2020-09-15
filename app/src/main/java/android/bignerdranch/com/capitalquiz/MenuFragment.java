package android.bignerdranch.com.capitalquiz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MenuFragment extends Fragment {

    private String continent_name;
    private int numberOfQuestions;

    public MenuFragment(){
        // Required empty public constructor
    }

    // Populates the view with the design from the resource file
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate ( R.layout.fragment_menu, container, false );
    }

    /*
   The method below is Called immediately after onCreateView() has returned,
   but before any saved state has been restored in to the view, improving performance;
    */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated ( view, savedInstanceState );

        NavController navController = Navigation.findNavController ( view);

        // on click directs the user to the Quiz fragment created with the variables saved in the Bundle
        Button Africa = view.findViewById (R.id.btn_Africa  );
        Africa.setOnClickListener ( v -> {
            navController.navigate (R.id.action_menuFragment_to_quizFragment );
            Bundle bundle = new Bundle();
            numberOfQuestions = 54;
            continent_name = "Africa";
            bundle.putString("continent", continent_name);
            bundle.putInt ( "questions", numberOfQuestions );
            Navigation.findNavController(view).navigate(R.id.quizFragment, bundle);
        } );

        // on click directs the user to the Quiz fragment created with the variables saved in the Bundle
        Button America = view.findViewById (R.id.btn_America );
        America.setOnClickListener ( v -> {
            navController.navigate ( R.id.action_menuFragment_to_quizFragment );
            Bundle bundle = new Bundle ( );
            numberOfQuestions = 34;
            continent_name = "America";
            bundle.putString ( "continent", continent_name );
            bundle.putInt ( "questions", numberOfQuestions );
            Navigation.findNavController ( view ).navigate ( R.id.quizFragment, bundle );
        });

        // on click directs the user to the Quiz fragment created with the variables saved in the Bundle
        Button Asia = view.findViewById (R.id.btn_Asia );
        Asia.setOnClickListener ( v -> {
            navController.navigate ( R.id.action_menuFragment_to_quizFragment );
            Bundle bundle = new Bundle ( );
            numberOfQuestions = 49;
            continent_name = "Asia";
            bundle.putString ( "continent", continent_name );
            bundle.putInt ( "questions", numberOfQuestions );
            Navigation.findNavController ( view ).navigate ( R.id.quizFragment, bundle );
        });

        // on click directs the user to the Quiz fragment created with the variables saved in the Bundle
        Button Australia = view.findViewById (R.id.btn_Australia );
        Australia.setOnClickListener ( v -> {
            navController.navigate ( R.id.action_menuFragment_to_quizFragment );
            Bundle bundle = new Bundle ( );
            numberOfQuestions = 14;
            continent_name = "Oceania";
            bundle.putString ( "continent", continent_name );
            bundle.putInt ( "questions", numberOfQuestions );
            Navigation.findNavController ( view ).navigate ( R.id.quizFragment, bundle );
        });

        // on click directs the user to the Quiz fragment created with the variables saved in the Bundle
        Button Europe = view.findViewById (R.id.btn_Europe );
        Europe.setOnClickListener ( v -> {
            navController.navigate ( R.id.action_menuFragment_to_quizFragment );
            Bundle bundle = new Bundle ( );
            numberOfQuestions = 44;
            continent_name = "Europe";
            bundle.putString ( "continent", continent_name );
            bundle.putInt ( "questions", numberOfQuestions );
            Navigation.findNavController ( view ).navigate ( R.id.quizFragment, bundle );
        });

        // on click directs the user to the Sing In activity
        Button back = view.findViewById ( R.id.btn_Back );
        back.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext (), SignIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent); }
        } );

        // on prompts the user with an alertBox of with Buttons for each particular Top
        Button top = view.findViewById ( R.id.btn_Top );
        top.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder ( getContext (), R.style.AlertDialogTheme );
                View topView = getLayoutInflater ().inflate(R.layout.top_dialog, null);

                Button africaTop =  topView.findViewById(R.id.btn_TopAfrica);
                Button asiaTop =   topView.findViewById(R.id.btn_TopAsia);
                Button americaTop =   topView.findViewById(R.id.btn_TopAmerica);
                Button australiaTop =   topView.findViewById(R.id.btn_TopAustralia);
                Button europeTop =   topView.findViewById(R.id.btn_TopEurope);

                builder.setPositiveButton ( "Return to menu", new DialogInterface.OnClickListener ( ) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                } );
                builder.setView ( topView );
                AlertDialog dialog = builder.create ( );
                dialog.show ( );

                // on click directs the user to the Top fragment created with the variables saved in the Bundle
                africaTop.setOnClickListener(new View.OnClickListener () {
                    public void onClick(View v) {
                        navController.navigate (R.id.action_menuFragment_to_topFragment);
                        Bundle bundle = new Bundle ( );
                        continent_name = "Africa";
                        bundle.putString ( "continent", continent_name);
                        Navigation.findNavController ( view ).navigate ( R.id.topFragment, bundle );
                        dialog.dismiss ();
                    }
                });
                // on click directs the user to the Top fragment created with the variables saved in the Bundle
                asiaTop.setOnClickListener(new View.OnClickListener () {
                    public void onClick(View v) {
                        navController.navigate (R.id.action_menuFragment_to_topFragment);
                        Bundle bundle = new Bundle ( );
                        continent_name = "Asia";
                        bundle.putString ( "continent", continent_name);
                        Navigation.findNavController ( view ).navigate ( R.id.topFragment, bundle );
                        dialog.dismiss ();
                    }
                });
                // on click directs the user to the Top fragment created with the variables saved in the Bundle
                americaTop.setOnClickListener(new View.OnClickListener () {
                    public void onClick(View v) {
                        navController.navigate (R.id.action_menuFragment_to_topFragment);
                        Bundle bundle = new Bundle ( );
                        continent_name = "America";
                        bundle.putString ( "continent", continent_name);
                        Navigation.findNavController ( view ).navigate ( R.id.topFragment, bundle );
                        dialog.dismiss ();
                    }
                });
                // on click directs the user to the Top fragment created with the variables saved in the Bundle
                australiaTop.setOnClickListener(new View.OnClickListener () {
                    public void onClick(View v) {
                        navController.navigate (R.id.action_menuFragment_to_topFragment);
                        Bundle bundle = new Bundle ( );
                        continent_name = "Oceania";
                        bundle.putString ( "continent", continent_name);
                        Navigation.findNavController ( view ).navigate ( R.id.topFragment, bundle );
                        dialog.dismiss ();
                    }
                });
                // on click directs the user to the Top fragment created with the variables saved in the Bundle
                europeTop.setOnClickListener(new View.OnClickListener () {
                    public void onClick(View v) {
                        navController.navigate (R.id.action_menuFragment_to_topFragment);
                        Bundle bundle = new Bundle ( );
                        continent_name = "Europe";
                        bundle.putString ( "continent", continent_name);
                        Navigation.findNavController ( view ).navigate ( R.id.topFragment, bundle );
                        dialog.dismiss ();
                    }
                });
            }
        });
    }
}

