package com.example.greenaura;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenaura.R;
import com.example.greenaura.Resource;
import com.example.greenaura.ResourceAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ResourceAdapter adapter;
    private List<Resource> resourceList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        recyclerView = findViewById(R.id.recycler_view_resources);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        resourceList = new ArrayList<>();
        adapter = new ResourceAdapter(this, resourceList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        fetchResources();
    }

    private void fetchResources() {
        db.collection("Educational Resources").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                if (snapshot != null) {
                    for (QueryDocumentSnapshot document : snapshot) {
                        String title = document.getString("ResourceHeader");
                        String description = document.getString("ResourceDescription");
                        String photoUrl = document.getString("ResourcePhoto");
                        System.out.println(title);
                        resourceList.add(new Resource(title, description, photoUrl));
                    }
                    adapter.notifyDataSetChanged();
                }
            } else {
                // Handle error
            }
        });
    }
}
