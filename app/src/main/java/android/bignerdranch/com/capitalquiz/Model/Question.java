package android.bignerdranch.com.capitalquiz.Model;

import java.util.ArrayList;
import java.util.Collections;
// Object Question, its attributes, setters and getters.
public class Question {
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String answer;
    private String img;
    // We declare an ArrayList<String> to store the option attributes retrieved from the Database
    private ArrayList<String> optionList = new ArrayList<String>();

    public Question (String question, String option1, String option2, String option3, String answer, String img) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;;
        this.option3 = option3;
        this.answer = answer;
        this.img = img;
    }

    public Question() {
    }

    public String getQuestion() {
        Random ( optionList );
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return optionList.get(0);
    }

    public void setOption1(String option1) {
        optionList.add ( option1 );
        this.option1 = option1;
    }

    public String getOption2() {
        return optionList.get(1);
    }

    public void setOption2(String option2) {
        optionList.add ( option2 );
        this.option2 = option2;
    }

    public String getOption3() {
        return optionList.get(2);
    }

    public void setOption3(String option3) {
        optionList.add ( option3 );
        this.option3 = option3;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getImg() {
        return img;
    }

    public void setImg (String img) {
        this.img = img;
    }
    /*
   The method below shuffles the ArrayList of options attributes
    */
    private void Random(ArrayList<String> optionList) {
        Collections.shuffle ( optionList );
    }
}
