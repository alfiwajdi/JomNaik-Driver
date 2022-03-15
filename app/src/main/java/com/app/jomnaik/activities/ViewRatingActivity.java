package com.app.jomnaik.activities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.jomnaik.R;
import com.app.jomnaik.models.RatingModelClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ViewRatingActivity extends BaseActivity {

    RecyclerView recyclerView;
    TextView textView, tvTotal;
    RatingBar ratingBar;
    List<RatingModelClass> list;
    String userId="";
    float totalRating = 0;
    int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rating);

        //Firebase and screen views initialization..
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        list = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); ;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(gridLayoutManager);

        textView = findViewById(R.id.textView);
        tvTotal = findViewById(R.id.tvTotal);
        ratingBar = findViewById(R.id.ratingBar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        showProgressDialog("Calculating Rating..");
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("DriverRatings").child(userId);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalRating = 0;
                counter = 0;
                list.clear();
                recyclerView.setAdapter(null);
                textView.setText("");
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    RatingModelClass model = snapshot1.getValue(RatingModelClass.class);
                    list.add(model);
                    totalRating = totalRating + model.getRating();
                    counter = counter + 1;
                }
                if(counter>0){
                    float rating = (totalRating / counter);
                    ratingBar.setRating(rating);

                    Collections.reverse(list);
                    RatingListAdapter adapter = new RatingListAdapter(ViewRatingActivity.this, list);
                    recyclerView.setAdapter(adapter);
                }else {
                    textView.setText("Not rated yet!");
                }

                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressDialog();
            }
        });
    }

    public class RatingListAdapter extends RecyclerView.Adapter<RatingListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<RatingModelClass> muploadList;

        public RatingListAdapter(Context context , List<RatingModelClass> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.rating_layout, parent , false);
            return (new ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder holder, final int position) {

            final RatingModelClass model = muploadList.get(position);

            holder.tvName.setText("Rider : "+model.getRiderName());
            holder.tvRating.setText(model.getRating()+" out of 5");
            holder.ratingBar.setRating(model.getRating());
        }

        @Override
        public int getItemCount() {
            return muploadList.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder{
            public TextView tvName;
            public TextView tvRating;
            public RatingBar ratingBar;

            public ImageViewHolder(View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tvName);
                tvRating = itemView.findViewById(R.id.tvRating);
                ratingBar = itemView.findViewById(R.id.ratingBar);

            }
        }
    }
}