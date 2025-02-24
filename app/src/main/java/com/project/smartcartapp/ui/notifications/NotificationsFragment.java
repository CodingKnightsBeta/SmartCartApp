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

import com.project.smartcartapp.AddProductActivity;
import com.project.smartcartapp.AdminActivity;
import com.project.smartcartapp.MainActivity;
import com.project.smartcartapp.R;
import com.project.smartcartapp.Recommend;
import com.project.smartcartapp.RecommendationsActivity;
import com.project.smartcartapp.ViewProductsAdminActivity;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private Button logout, ViewRecommendation, admin_panel_btn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        logout = (Button) root.findViewById(R.id.log_out_button);
        admin_panel_btn = (Button) root.findViewById(R.id.go_to_admin_btn);
        ViewRecommendation = (Button) root.findViewById(R.id.recommendations_btn);

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

        admin_panel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdminActivity.class);
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
