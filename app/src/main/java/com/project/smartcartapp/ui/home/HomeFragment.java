package com.project.smartcartapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.paperdb.Paper;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.smartcartapp.HomeActivity;
import com.project.smartcartapp.Model.Lists;
import com.project.smartcartapp.NewListActivity;
import com.project.smartcartapp.Prevalent.Prevalent;
import com.project.smartcartapp.R;
import com.project.smartcartapp.ViewHolder.ListsViewHolder;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private View ListView;
    private RecyclerView recyclerView;
    DatabaseReference ListRef;
    private Button newListButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        ListView = inflater.inflate(R.layout.fragment_home, container, false);

        Paper.init(getActivity());
        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);

        recyclerView = (RecyclerView) ListView.findViewById(R.id.list_display_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ListRef = FirebaseDatabase.getInstance().getReference().child("Shopping Lists").child(UserPhoneKey);

        newListButton = (Button) ListView.findViewById(R.id.create_new_list_button);

        return ListView;
    }

    @Override
    public void onStart() {
        super.onStart();

        newListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewListActivity.class);
                intent.putExtra("status","new");
                startActivity(intent);
            }
        });

        FirebaseRecyclerOptions<Lists> options = new FirebaseRecyclerOptions.Builder<Lists>()
                .setQuery(ListRef, Lists.class).build();
        FirebaseRecyclerAdapter<Lists, ListsViewHolder> adapter = new FirebaseRecyclerAdapter<Lists, ListsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ListsViewHolder holder, int position, @NonNull final Lists model) {
                holder.txtListName.setText(model.getLname());
                holder.txtListDescription.setText(model.getDescription());

                holder.listCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), NewListActivity.class);
                        intent.putExtra("listID", model.getLid());
                        intent.putExtra("listName", model.getLname());
                        intent.putExtra("listDescription", model.getDescription());
                        intent.putExtra("status", "edit");
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ListsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
                ListsViewHolder viewHolder = new ListsViewHolder(view);
                return viewHolder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


}
