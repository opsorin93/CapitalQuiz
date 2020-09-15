package android.bignerdranch.com.capitalquiz;

import android.bignerdranch.com.capitalquiz.Model.Question;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;


public class QuizFragment extends Fragment {

    // declaring Widgets
    private ImageView img;
    private TextView questionTxt;
    private TextView counter;
    private TextView scoreTxt;
    private TextView questionCount;
    private Button btn_choice1;
    private Button btn_choice2;
    private Button btn_choice3;
    private Button skip_btn;
    private Button btn_info;

    //declaring variables
    private static final long COUNT_IN_MILLIS = 10000;
    private int count = 0;
    private int score = 0;
    private int correct = 0;
    private int wrong = 0;
    private int skip = 0;
    private final static long Interval = 1000; //1 sec
    private long Timeout = COUNT_IN_MILLIS;// 10 sec
    private boolean shouldRestart = false;

    private CountDownTimer mCountDownTimer;
    private ArrayList<Integer> questionList = new ArrayList<Integer> ( );
    private DatabaseReference ref;

    public QuizFragment() {
        // Required empty public constructor
    }

    // Populates the view with the design from the resource file
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate ( R.layout.fragment_quiz, container, false );

    }
    // The method below overrides onPause method
    @Override
    public void onPause() {
        shouldRestart = true;
        super.onPause ();
    }

    // The method below overrides onResume and executes method restart()
    @Override
    public void onResume() {
        super.onResume ();
        restart ();
    }

    /*
    The method below is Called immediately after onCreateView() has returned,
    but before any saved state has been restored in to the view, improving performance;
   */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated ( view, savedInstanceState );

        img = view.findViewById ( R.id.image );
        questionTxt = view.findViewById ( R.id.questionTxt );
        counter = view.findViewById ( R.id.timer );
        scoreTxt = view.findViewById ( R.id.textScore );
        questionCount = view.findViewById ( R.id.textTotalQuestions );

        Button back_btn = view.findViewById ( R.id.btn_Back );
        btn_info = view.findViewById ( R.id.btn_Info );
        skip_btn = view.findViewById ( R.id.skip );
        btn_choice1 = view.findViewById ( R.id.btn_choice1 );
        btn_choice2 = view.findViewById ( R.id.btn_choice2 );
        btn_choice3 = view.findViewById ( R.id.btn_choice3 );

        questionList = Random ( );
        startTimer ( );
        updateQuestion ( );

        // on prompts the user with an alertBox with information on how to play
        btn_info.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder ( getActivity ( ), R.style.AlertDialogTheme );
                View mView = getActivity ( ).getLayoutInflater ( ).inflate ( R.layout.rules_dialog, null );
                btn_info.setVisibility ( View.INVISIBLE );
                pauseTimer ( );

                builder.setNegativeButton ( "Return to quiz", new DialogInterface.OnClickListener ( ) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startTimer ( );
                    }
                } );
                builder.setView ( mView );
                android.app.AlertDialog dialog = builder.create ( );
                dialog.show ( );
                dialog.getButton ( android.app.AlertDialog.BUTTON_POSITIVE ).setOnClickListener ( new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View v) {
                    }
                } );
            }
        } );
        // on click prompts the user with an alertBox containing information about his score
        back_btn.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder scoreBoard = new AlertDialog.Builder ( getContext ( ) );
                scoreBoard.setTitle ( "Are you sure you want to quit ?" + "\n" + "Progress will be lost" )
                        .setMessage ( "Your score is " + score + "\n" + "Correct answers " + correct + "\n" + "Wrong answers " + wrong )
                        .setNegativeButton ( "No", new DialogInterface.OnClickListener ( ) {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                shouldRestart = false;
                            }
                        } )
                        .setPositiveButton ( "Yes", (dialog, which) -> {
                            shouldRestart = true;
                            pauseTimer ( );
                            restart ();
                        } );
                scoreBoard.show ( );
            }
        } );

        // on click lets user skip questions
        skip_btn.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                skip_btn.setBackground ( ContextCompat.getDrawable ( Objects.requireNonNull ( getContext ( ) ), R.drawable.skip_red ) );
                Handler handler = new Handler ( );
                handler.postDelayed ( () -> {
                    skip_btn.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.skip ) );
                }, Interval );
                skipQuestion ( );
            }
        } );
    }

    /*
    The method below is updates the questions in the quiz and shows the user
    if the answer is correct or not
   */
    private void updateQuestion() {
        resetTimer ( );
        if (count >= questionList.size ( )) {

            btn_choice1.setEnabled ( false );
            btn_choice2.setEnabled ( false );
            btn_choice3.setEnabled ( false );
            skip_btn.setEnabled ( false );
            alertBox ( );
            writeData ( );

        } else {
            startTimer ( );
            assert getArguments ( ) != null;
            // we retrieve the questions from Firebase database
            ref = FirebaseDatabase.getInstance ( ).getReference ( ).child ( (getArguments ( ).getString ( "continent" )) ).child ( String.valueOf ( questionList.get ( count ) ) );
            ref.addValueEventListener ( new ValueEventListener ( ) {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Question question = dataSnapshot.getValue ( Question.class );
                    if (question != null && question.getImg ( ) != null) {
                        Glide.with ( Objects.requireNonNull ( getContext ( ) ) ).load ( question.getImg ( ) ).override ( 600, 200 ).into ( img );
                        questionTxt.setText ( question.getQuestion ( ) );
                        btn_choice1.setText ( question.getOption1 ( ) );
                        btn_choice2.setText ( question.getOption2 ( ) );
                        btn_choice3.setText ( question.getOption3 ( ) );
                    }
                    count++;
                    questionCount ( );
                    btn_choice1.setOnClickListener ( v -> {
                        assert question != null;
                        if (btn_choice1.getText ( ).toString ( ).equals ( question.getAnswer ( ) )) {
                            btn_choice1.setBackground ( ContextCompat.getDrawable ( Objects.requireNonNull ( getContext ( ) ), R.drawable.btn_rounded_green ) );
                            Handler handler = new Handler ( );
                            handler.postDelayed ( () -> {
                                btn_choice1.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded ) );
                                updateQuestion ( );
                                increaseScore ( );
                            }, Interval );
                        } else {
                            btn_choice1.setBackground ( ContextCompat.getDrawable ( Objects.requireNonNull ( getContext ( ) ), R.drawable.btn_rounded_red ) );
                            decreaseScore ( );
                            if (btn_choice2.getText ( ).toString ( ).equals ( question.getAnswer ( ) )) {
                                btn_choice2.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded_green ) );
                            } else if (btn_choice3.getText ( ).toString ( ).equals ( question.getAnswer ( ) )) {
                                btn_choice3.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded_green ) );
                            }
                            Handler handler = new Handler ( );
                            handler.postDelayed ( () -> {
                                btn_choice1.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded ) );
                                btn_choice2.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded ) );
                                btn_choice3.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded ) );
                                updateQuestion ( );
                            }, Interval );
                        }
                    } );
                    btn_choice2.setOnClickListener ( v -> {
                        assert question != null;
                        if (btn_choice2.getText ( ).toString ( ).equals ( question.getAnswer ( ) )) {
                            btn_choice2.setBackground ( ContextCompat.getDrawable ( Objects.requireNonNull ( getContext ( ) ), R.drawable.btn_rounded_green ) );
                            Handler handler = new Handler ( );
                            handler.postDelayed ( () -> {
                                btn_choice2.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded ) );
                                updateQuestion ( );
                                increaseScore ( );
                            }, Interval );
                        } else {
                            btn_choice2.setBackground ( ContextCompat.getDrawable ( Objects.requireNonNull ( getContext ( ) ), R.drawable.btn_rounded_red ) );
                            decreaseScore ( );
                            if (btn_choice1.getText ( ).toString ( ).equals ( question.getAnswer ( ) )) {
                                btn_choice1.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded_green ) );
                            } else if (btn_choice3.getText ( ).toString ( ).equals ( question.getAnswer ( ) )) {
                                btn_choice3.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded_green ) );
                            }
                            Handler handler = new Handler ( );
                            handler.postDelayed ( () -> {
                                btn_choice1.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded ) );
                                btn_choice2.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded ) );
                                btn_choice3.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded ) );
                                updateQuestion ( );
                            }, Interval );
                        }
                    } );
                    btn_choice3.setOnClickListener ( v -> {
                        assert question != null;
                        if (btn_choice3.getText ( ).toString ( ).equals ( question.getAnswer ( ) )) {
                            btn_choice3.setBackground ( ContextCompat.getDrawable ( Objects.requireNonNull ( getContext ( ) ), R.drawable.btn_rounded_green ) );
                            Handler handler = new Handler ( );
                            handler.postDelayed ( () -> {
                                btn_choice3.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded ) );
                                updateQuestion ( );
                                increaseScore ( );
                            }, Interval );
                        } else {
                            btn_choice3.setBackground ( ContextCompat.getDrawable ( Objects.requireNonNull ( getContext ( ) ), R.drawable.btn_rounded_red ) );
                            decreaseScore ( );
                            if (btn_choice2.getText ( ).toString ( ).equals ( question.getAnswer ( ) )) {
                                btn_choice2.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded_green ) );
                            } else if (btn_choice1.getText ( ).toString ( ).equals ( question.getAnswer ( ) )) {
                                btn_choice1.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded_green ) );
                            }
                            Handler handler = new Handler ( );
                            handler.postDelayed ( () -> {
                                btn_choice1.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded ) );
                                btn_choice2.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded ) );
                                btn_choice3.setBackground ( ContextCompat.getDrawable ( getContext ( ), R.drawable.btn_rounded ) );
                                updateQuestion ( );
                            }, Interval );
                        }
                    } );
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText ( getContext ( ), "An error has occurred", Toast.LENGTH_SHORT ).show ( );
                }
            } );
        }
    }

    /*
    The method below is populating an ArrayList with the corresponding number of the question
    from thr Firebase database and shuffles it
   */
    private ArrayList<Integer> Random() {
        ArrayList<Integer> list = new ArrayList<Integer> ( );
        assert getArguments ( ) != null;
        for (int i = 1; i <= (getArguments ( ).getInt ( "questions" )); i++) {
            list.add ( i );
        }
        Collections.shuffle ( list );
        return list;
    }

    /*
    The method below  start the countdown (timer) for each question
    */
    private void startTimer() {
        mCountDownTimer = new CountDownTimer ( Timeout, Interval ) {
            @Override
            public void onTick(long millisUntilFinished) {
                Timeout = millisUntilFinished;
                updateCounterText ( );
            }

            @Override
            public void onFinish() {
                resetTimer ( );
                updateQuestion ( );
                decreaseScore ( );
                Message ( );
            }
        }.start ( );
    }

    /*
    The method below updates the countdown text
    */
    private void updateCounterText() {
        int seconds = (int) (Timeout / 1000) % 60;
        String timeFormatted = String.format ( Locale.getDefault ( ), "%02d", seconds );
        counter.setText ( timeFormatted );
        if (Timeout < 5000) {
            counter.setTextColor ( Color.RED );
        } else {
            counter.setTextColor ( Color.WHITE );
        }
    }
    /*
     The method below resets the timer
    */
    private void resetTimer() {
        mCountDownTimer.cancel ( );
        Timeout = COUNT_IN_MILLIS;
        updateCounterText ( );
    }
     /*
     The method below pauses the timer;
     */
    private void pauseTimer() {
        mCountDownTimer.cancel ( );
    }

    /*
    The method below increases the score
    */
    private void increaseScore() {
        correct++;
        score = score + 10;
        scoreTxt.setText ( "Score" + " " + score );
    }

    /*
    The method below decreases the score
    */
    private void decreaseScore() {
        wrong++;
        if (score != 0) {
            score = score - 10;
            scoreTxt.setText ( "Score" + " " + score );
        }
    }

    /*
    The method below allows to skip questions
    */
    private void skipQuestion() {
        if (score != 0) {
            updateQuestion ( );
            score = score - 5;
            skip++;
            scoreTxt.setText ( MessageFormat.format ( "Score {0}", score ) );
            Toast.makeText ( getContext ( ), "Question Skipped", Toast.LENGTH_SHORT ).show ( );
        } else {
            Toast.makeText ( getContext ( ), "You have no points!", Toast.LENGTH_SHORT ).show ( );
        }
    }

    /*
    The method below counts the questions and updates the questionCounter
    */
    private void questionCount() {
        if (count <= questionList.size ( )) {
            questionCount.setText ( MessageFormat.format ( "{0}/{1}", count, getArguments ( ).getInt ( "questions" ) ) );
        }
    }

    /*
    The method prompts the user with a message when the timer has run out
    */
    private void Message() {
        Toast.makeText ( getContext ( ), "Time Out!", Toast.LENGTH_SHORT ).show ( );
    }

    /*
    The method below prompts the user with amn alertBox with information about his game progress
    */
    private void alertBox() {
        Handler handler = new Handler ( );
        handler.postDelayed ( () -> {

            NavController navController = Navigation.findNavController ( getView ( ) );

            AlertDialog.Builder scoreBoard = new AlertDialog.Builder ( getContext ( ) );
            scoreBoard.setTitle ( "Game Over !" + "\n" + "Your score is " + score )
                    .setMessage ( "Correct answers " + correct + "\n" + "Wrong answers " + wrong + "\n" + "Skipped question " + skip )
                    .setNegativeButton ( "Return to Menu", (dialog, which) ->
                            navController.navigate ( R.id.action_quizFragment_to_menuFragment ) );
            scoreBoard.show ( );
        }, 100 );
    }

    /*
    The method below restarts the quiz, once the user outs the app in the background
    */
    private void restart(){
        if (!shouldRestart) return;
        resetTimer ();
        NavController navController = Navigation.findNavController (getActivity (),R.id.fragment1);
        navController.navigate ( R.id.action_quizFragment_to_menuFragment );
        Toast.makeText (getContext (), "Progress lost !", Toast.LENGTH_SHORT ).show ( );
    }

    /*
    The method below writes the score on the database under the user name/nickname
    */
    private void writeData() {
        Menu myActivity = (Menu) getActivity ( );
        if ((myActivity != null) && (myActivity.authUsername != null)) {
            ref = FirebaseDatabase.getInstance ( ).getReference ( ).child ( (getArguments ( ).getString ( "continent" )) ).child ( "Users" );
            ref.addListenerForSingleValueEvent ( new ValueEventListener ( ) {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild ( myActivity.authUsername )) {
                        String databaseScore = dataSnapshot.child ( myActivity.authUsername ).child ( "score" ).getValue ( String.class );
                        int userscore = Integer.parseInt(databaseScore);
                        if (score > userscore) {
                            ref.child ( myActivity.authUsername ).child ( "name" ).setValue ( myActivity.authUsername );
                            ref.child ( myActivity.authUsername ).child ( "score" ).setValue ( String.valueOf ( score ));
                            Log.e ( "HIGHER SCORE", String.valueOf ( score ) );
                        }
                    } else {
                        ref.child ( myActivity.authUsername ).child ( "name" ).setValue ( myActivity.authUsername );
                        ref.child ( myActivity.authUsername ).child ( "score" ).setValue ( String.valueOf ( score ) );
                        Log.e ( "NO USER", String.valueOf ( score ) );
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText ( getContext ( ), "An error has occurred", Toast.LENGTH_SHORT ).show ( );
                }
            } );
        }
        if ((myActivity != null) && (myActivity.username != null)) {
            ref = FirebaseDatabase.getInstance ( ).getReference ( ).child ( (getArguments ( ).getString ( "continent" )) ).child ( "Users" );
            ref.addListenerForSingleValueEvent ( new ValueEventListener ( ) {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild ( myActivity.username )) {
                        int userscore = dataSnapshot.child ( myActivity.username ).child("score").getValue ( Integer.class );
                        if (score > userscore) {
                            ref.child ( myActivity.username ).child ( "score" ).setValue (String.valueOf ( score )); ;
                            ref.child ( myActivity.username ).child ( "name" ).setValue ( myActivity.username  );
                            Log.e ( "HIGHER SCORE", String.valueOf ( score ) );
                        }
                    } else {
                        ref.child ( myActivity.username ).child ( "score" ).setValue (String.valueOf ( score ));
                        ref.child ( myActivity.username ).child ( "name" ).setValue (myActivity.username );
                        Log.e ( "NO USER", String.valueOf ( score ) );
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText ( getContext ( ), "An error has occurred", Toast.LENGTH_SHORT ).show ( );
                }
            } );
        }
    }

}


