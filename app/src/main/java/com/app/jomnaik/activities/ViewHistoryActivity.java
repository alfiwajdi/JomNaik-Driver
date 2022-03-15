package com.app.jomnaik.activities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.jomnaik.R;
import com.app.jomnaik.models.BookingModelClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViewHistoryActivity extends BaseActivity {

    RecyclerView recyclerView;
    TextView textView, tvTotal;
    DatabaseReference databaseReference;
    String userId="";
    List<BookingModelClass> list;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        //Firebase and screen views initialization..
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("BookingRequests");
        list = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); ;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(gridLayoutManager);

        textView = findViewById(R.id.textView);
        tvTotal = findViewById(R.id.tvTotal);

    }

    @Override
    protected void onStart() {
        super.onStart();

        String str  = "Completed";
        showProgressDialog("Loading data..");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                textView.setText("");
                count = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    BookingModelClass model = snapshot.getValue(BookingModelClass.class);
                    if(userId.equals(model.getDriverId()) && str.equals(model.getStatus())){
                        list.add(model);
                        count= count+1;
                    }
                }
                tvTotal.setText("Total Trips : "+count);
                if(list.size()>0){
                    HistoryListAdapter adapter = new HistoryListAdapter(ViewHistoryActivity.this, list);
                    recyclerView.setAdapter(adapter);
                }else {
                    textView.setText("No Bookings in history!");
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressDialog();
                Toast.makeText(getApplicationContext(), "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<BookingModelClass> muploadList;

        public HistoryListAdapter(Context context , List<BookingModelClass> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.history_layout, parent , false);
            return (new ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder holder, final int position) {

            final BookingModelClass model = muploadList.get(position);

            holder.tvName.setText("Rider : "+model.getRiderName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(model.getDropOffLati(), model.getDropOffLongi(), 1);
                List<Address> listAddresses2 = geocoder.getFromLocation(model.getPickUpLati(), model.getPickUpLongi(), 1);

                if (listAddresses != null && listAddresses.size() > 0) {
                    String address = "";

                    if (listAddresses.get(0).getAddressLine(0) != null) {
                        address += listAddresses.get(0).getAddressLine(0);
                        holder.tvDropOff.setText(address);
                        holder.tvDropOff.setText("Drop Off : "+address);
                    }else {
                        if (listAddresses.get(0).getThoroughfare() != null) {
                            address += listAddresses.get(0).getThoroughfare() + ", ";
                        }
                        if (listAddresses.get(0).getLocality() != null) {
                            address += listAddresses.get(0).getLocality() + ", ";
                        }
                        if (listAddresses.get(0).getAdminArea() != null) {
                            address += listAddresses.get(0).getAdminArea()+", ";
                        }
                        if (listAddresses.get(0).getCountryName() != null) {
                            address += listAddresses.get(0).getCountryName();
                            holder.tvDropOff.setText("Drop Off : "+address);
                        }
                    }
                }

                if (listAddresses2 != null && listAddresses2.size() > 0) {
                    String address = "";

                    if (listAddresses2.get(0).getAddressLine(0) != null) {
                        address += listAddresses2.get(0).getAddressLine(0);
                        holder.tvPickUp.setText("Pick Up : "+address);
                    }else {
                        if (listAddresses2.get(0).getThoroughfare() != null) {
                            address += listAddresses2.get(0).getThoroughfare() + ", ";
                        }
                        if (listAddresses2.get(0).getLocality() != null) {
                            address += listAddresses2.get(0).getLocality() + ", ";
                        }
                        if (listAddresses2.get(0).getAdminArea() != null) {
                            address += listAddresses2.get(0).getAdminArea()+", ";
                        }
                        if (listAddresses2.get(0).getCountryName() != null) {
                            address += listAddresses2.get(0).getCountryName();
                            holder.tvPickUp.setText("Pick Up : "+address);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return muploadList.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder{
            public TextView tvName;
            public TextView tvPickUp;
            public TextView tvDropOff;

            public ImageViewHolder(View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tvName);
                tvPickUp = itemView.findViewById(R.id.tvPickUp);
                tvDropOff = itemView.findViewById(R.id.tvDropOff);

            }
        }
    }
}