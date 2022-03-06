package projet.application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    RecyclerViewAdapter adapter;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.layout,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager((new LinearLayoutManager(view.getContext())));
        ArrayList<Music> musicArrayList = new ArrayList<>();
        musicArrayList.add(new Music("titre 1","artiste 1"));
        musicArrayList.add(new Music("titre 2","artiste 2"));
        musicArrayList.add(new Music("titre 3","artiste 3"));
        musicArrayList.add(new Music("blabla","blabla"));
        adapter = new RecyclerViewAdapter(this.getContext(), musicArrayList);
        recyclerView.setAdapter(adapter);

        return view;


    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}