package com.example.bimesh.hello;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


@RequiresApi(api = Build.VERSION_CODES.O)
public class TabProfile extends Fragment{
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private TextView name, blood, address, phone, age;
    private String userID, date;
    private int yourAge;





    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        View RootView = inflater.inflate(R.layout.tab_profile, container, false);
        name = (TextView) RootView.findViewById(R.id.name_detail);
        blood = (TextView) RootView.findViewById(R.id.blood_detail);
        address = (TextView) RootView.findViewById(R.id.address_detail);
        phone = (TextView) RootView.findViewById(R.id.phone_detail);
        age = (TextView) RootView.findViewById(R.id.age_detail);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//            View rootView = inflater.inflate(R.layout.tab_profile, container, false);
            return RootView;
    }

    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            UserInformation uInfo = new UserInformation();
            uInfo.setName(ds.child(userID).getValue(UserInformation.class).getName());
            uInfo.setEmail(ds.child(userID).getValue(UserInformation.class).getEmail());
            uInfo.setPhone(ds.child(userID).getValue(UserInformation.class).getPhone());
            uInfo.setAddress(ds.child(userID).getValue(UserInformation.class).getAddress());
            uInfo.setBlood(ds.child(userID).getValue(UserInformation.class).getBlood());
            uInfo.setDate(ds.child(userID).getValue(UserInformation.class).getDate());
            date=uInfo.getDate();
            calculateAge();

            name.setText(uInfo.getName());
            blood.setText(uInfo.getBlood());
            address.setText(uInfo.getAddress());
            phone.setText(uInfo.getPhone());
            age.setText(String.valueOf(yourAge));


        }
    }

    public void calculateAge(){
        String[] dateParts = date.split("/");
        String year=dateParts[2];
        int then = Integer.parseInt(year);
        Calendar calendar = Calendar.getInstance();
        int now = calendar.get(Calendar.YEAR);
        yourAge = now-then;


    }


}