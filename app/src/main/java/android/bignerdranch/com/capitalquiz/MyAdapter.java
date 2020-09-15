package android.bignerdranch.com.capitalquiz;

import android.bignerdranch.com.capitalquiz.Model.Player;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Player> subList ;
    private Context context;

    MyAdapter(List<Player> subList, Context context) {
        this.subList = subList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 1:
                view = LayoutInflater.from ( context ).inflate ( R.layout.layout_first_player, parent, false );
                break;
            case 2:
                view = LayoutInflater.from ( context ).inflate ( R.layout.layout_second_player, parent, false );
                break;
            case 3:
                view = LayoutInflater.from ( context ).inflate ( R.layout.layout_third_player, parent, false );
                break;
            default:
                view = LayoutInflater.from ( context ).inflate ( R.layout.layout_list_top, parent, false );
        }
        return new MyViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       holder.playerName.setText (subList.get(position).getName ());
       holder.playerScore.setText (subList.get(position).getScore ());
    }

    @Override
    public int getItemViewType(int position) {
        int caseValue;
        switch (position) {
            case 0:
                caseValue = 1;
            break;
            case 1:
                caseValue = 2;
            break;
            case 2:
               caseValue = 3;
            break;
            default:
                caseValue = 4;
        }
        return caseValue;
    }

    @Override
    public int getItemCount() {
        return subList.size ();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView playerName;
        TextView playerScore;

        MyViewHolder(@NonNull View itemView) {
            super ( itemView );
            playerName = itemView.findViewById (R.id.playerInfo  );
            playerScore = itemView.findViewById (R.id.playerInfo2  );
        }
    }
}
