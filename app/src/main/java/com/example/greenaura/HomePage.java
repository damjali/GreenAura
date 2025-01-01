// HomePage.java
package com.example.greenaura;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        resourceList = new ArrayList<>();
        adapter = new ResourceAdapter(this, resourceList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        fetchResources();

        //pass the Required Data from the Clicked Card to the Activity via Intent.
        adapter.setOnItemClickListener(resource -> {
            Intent intent = new Intent(HomePage.this, EducationalResourcesContainerActivity.class);
            intent.putExtra("ResourceHeader", resource.getResourceHeader());
            intent.putExtra("ResourceDescription", resource.getResourceDescription());
            intent.putExtra("ResourcePhoto", resource.getResourcePhoto());
            intent.putExtra("ResourceLink", resource.getResourceLink());
            intent.putExtra("ResourcePostDate", resource.getResourcePostDate());
            intent.putExtra("ResourceUpvote", resource.getResourceUpvote());
            startActivity(intent);
        });
    }

    private void fetchResources() {
        db.collection("Educational Resources").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                if (snapshot != null) {
                    for (QueryDocumentSnapshot document : snapshot) {
                        String ResourcePhoto = document.getString("ResourcePhoto");
                        String ResourceHeader = document.getString("ResourceHeader");
                        String ResourceDescription = document.getString("ResourceDescription");
                        String ResourcePostDate = document.getString("ResourcePostDate");
                        String ResourceLink = document.getString("ResourceLink");
                        String ResourceUpvote = document.getString("ResourceUpvote");

                        resourceList.add(new Resource(ResourceHeader, ResourceDescription, ResourcePhoto, ResourceLink, ResourcePostDate, 0));
                    }
                    adapter.notifyDataSetChanged();
                }
            } else {
                // Handle error
            }
        });
    }
}
