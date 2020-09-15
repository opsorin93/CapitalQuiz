package android.bignerdranch.com.capitalquiz;

import android.bignerdranch.com.capitalquiz.Model.Player;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class TopFragment extends Fragment {

    private List<Player> subList ;
    private ArrayList<Player> PlayersArray = new ArrayList<> ( );

    //widgets declaration
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    public TopFragment() {
        // Required empty public constructor
    }

    // Populates the view with the design from the resource file
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate ( R.layout.fragment_top, container, false );
    }

    /*
    The method below is Called immediately after onCreateView() has returned,
    but before any saved state has been restored in to the view, improving performance;
   */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated ( view, savedInstanceState );

        mRecyclerView = view.findViewById ( R.id.recycleView );
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager ( getContext ( ) );
        mRecyclerView.setLayoutManager ( layoutManager );

        TextView title = view.findViewById ( R.id.message );
        if ((getArguments ( ).getString ( "continent" ) == "Oceania")) {
            title.setText ( "Australia and " + (getArguments ( ).getString ( "continent" )) + " Top" );
        } else {
            title.setText ( (getArguments ( ).getString ( "continent" )) + " Top" );
        }

        //check the Firebase Database for all the players, saving their name and score in PlayersArray
        DatabaseReference ref = FirebaseDatabase.getInstance ( ).getReference ( ).child ( (getArguments ( ).getString ( "continent" )) ).child ( "Users" );
        ref.addListenerForSingleValueEvent ( new ValueEventListener ( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> players = dataSnapshot.getChildren ( ).iterator ( );
                Toast.makeText ( getContext ( ), dataSnapshot.getChildrenCount ( ) +" users have played this quiz", Toast.LENGTH_SHORT ).show ( );
                while (players.hasNext ( )) {
                    DataSnapshot player = players.next ( );
                    String name;
                    String score;
                    name = player.child ( "name" ).getValue ( String.class );
                    score = player.child ( "score" ).getValue ( String.class );
                    Player playerScore = new Player ( name, score );
                    PlayersArray.add ( playerScore );
                }
                //sort the PlayersArray by score
                Collections.sort ( PlayersArray, new Comparator<Player> ( ) {
                    @Override
                    public int compare(Player o1, Player o2) {
                        return Integer.parseInt ( o2.getScore ( ) ) - Integer.parseInt ( o1.getScore ( ) );
                    }
                } );
                // check if the PlayersArray is bigger than 10, if yes, create of sublist of with the first 10 players
                if (PlayersArray.size () >= 10) {
                    subList = PlayersArray.subList ( 0, 9 );
                    Log.d ( "Full List", subList.toString ( ) );
                }else{
                    subList = PlayersArray;
                    Log.d ( "Sublist", subList.toString ( ) );
                }
                //populate the RecyclerView with the subList
                mAdapter = new MyAdapter (subList, getContext ());
                mRecyclerView.setAdapter ( mAdapter );
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText ( getContext ( ), "An error has occurred", Toast.LENGTH_SHORT ).show ( );
            }
        } );

        NavController navController = Navigation.findNavController ( view );
        Button back = view.findViewById ( R.id.btn_Back );

        // on click navigates the user to menuFragment
        back.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                navController.navigate ( R.id.action_topFragment_to_menuFragment );
            }
        } );

    }
}