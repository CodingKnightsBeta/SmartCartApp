package com.project.smartcartapp.ui.notifications;

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
import io.paperdb.Paper;

import com.project.smartcartapp.MainActivity;
import com.project.smartcartapp.R;
import com.project.smartcartapp.Recommend;
import com.project.smartcartapp.RecommendationsActivity;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private Button logout, ViewRecommendation, GenerateRules;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        logout = (Button) root.findViewById(R.id.log_out_button);
        ViewRecommendation = (Button) root.findViewById(R.id.recommendations_btn);
        GenerateRules = (Button) root.findViewById(R.id.generate_rules_btn);
        GenerateRules.setVisibility(View.INVISIBLE);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        ViewRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecommendationsActivity.class);
                startActivity(intent);
            }
        });
        GenerateRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Recommend.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
