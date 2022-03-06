package projet.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Music> musicList;
    private LayoutInflater layoutInflater;
    //private ItemClickListener itemClickListener;//decomenter pour activer nav


    RecyclerViewAdapter(Context context, List<Music> ml){
        this.layoutInflater = LayoutInflater.from(context);
        this.musicList = ml;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.fragment_first,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        String tit = musicList.get(position).title;
        String art = musicList.get(position).artist;
        holder.title.setText(tit);
        holder.artist.setText(art);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        TextView title;
        TextView artist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            artist = itemView.findViewById(R.id.subtitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
                //pas de navigation parceque on a pas de fonctionalité pour stream la music
        }
    }
}
