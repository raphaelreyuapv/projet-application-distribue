package projet.application;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    RecyclerView recyclerView;

    MusicListe<Music> musicArrayList = new MusicListe<>();
    RecyclerViewAdapter adapter;
    Context context;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.layout,container,false);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager((new LinearLayoutManager(view.getContext())));
        context = this.getContext();

        this.musicArrayList.add(new Music("titre 1","artiste 1"));

        adapter = new RecyclerViewAdapter(this.getContext(), musicArrayList.getList());
        recyclerView.setAdapter(adapter);

        return view;

    }

    public void addToMusicArrayList(String title, String artist) {
        this.musicArrayList.add(new Music(title,artist));
        adapter = new RecyclerViewAdapter(context, musicArrayList.getList());
        recyclerView.setAdapter(adapter);
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public MusicListe getMusicList() {
        return musicArrayList;
    }

    class MusicListe<Music>{
        private ArrayList<Music> list;

        public MusicListe(){
            list = new ArrayList<>();
        }

        public void add(Music t){

            list.add(t);
        }

        public void remove(Music t){
            list.remove(t);

        }

        public ArrayList<Music> getList(){
            return list;

        }

    }


}