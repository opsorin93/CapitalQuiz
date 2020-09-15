package android.bignerdranch.com.capitalquiz.Model;

// Object Player, its attributes setters and  getters.
public class Player {
    private String name, score;

    public Player(String name, String score) {
        this.name = name;
        this.score =score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = " " + score;
    }
}
