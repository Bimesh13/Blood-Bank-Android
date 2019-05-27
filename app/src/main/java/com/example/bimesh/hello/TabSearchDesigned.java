package com.example.bimesh.hello;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bimesh.hello.R;
import com.example.bimesh.hello.UserInformation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class TabSearchDesigned extends Fragment {

    private UserInformation userInformation;
    private RecyclerView mUsersListRecyclerView;
    private Spinner mSpinner;
    private String bloodGroupSelected;
    private ImageView mSearchBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_search_designed, container, false);
        mUsersListRecyclerView = view.findViewById(R.id.result_recycler_view);
        mSpinner = view.findViewById(R.id.spinner);
        mSearchBtn = view.findViewById(R.id.search_btn);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUsersListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mUsersListRecyclerView.setNestedScrollingEnabled(false);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.blood,
                android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView myText = (TextView) view;
                bloodGroupSelected = myText.getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsersListRecyclerView.setVisibility(View.VISIBLE);
                firebaseBloodGroupSearch(bloodGroupSelected);
            }
        });

    }

    private void firebaseBloodGroupSearch(final String bloodGroupSelected) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.orderByChild("blood").equalTo(bloodGroupSelected).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ArrayList<UserInformation> users = new ArrayList<>();
                            users.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                userInformation = ds.getValue(UserInformation.class);
                                users.add(userInformation);
                            }
                            mUsersListRecyclerView.setAdapter(new UsersAdapter(users, getActivity()));
                            mUsersListRecyclerView.getAdapter().notifyDataSetChanged();
                        } else {
                            mUsersListRecyclerView.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "No user with " + bloodGroupSelected + " blood group found!!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseDatabase.getInstance().getReference().child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<UserInformation> users = new ArrayList<>();
                        users.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            userInformation = ds.getValue(UserInformation.class);
                            users.add(userInformation);
                        }
                        mUsersListRecyclerView.setAdapter(new UsersAdapter(users, getActivity()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }


    class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserItemHolder> {

        private ArrayList<UserInformation> userItems;
        private Activity activity;

        public UsersAdapter(ArrayList<UserInformation> userItems, Activity activity) {
            this.userItems = userItems;
            this.activity = activity;
        }

        @NonNull
        @Override
        public UserItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UserItemHolder(LayoutInflater.from(activity).inflate(R.layout.list_layout_designed, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull UserItemHolder holder, int position) {
            holder.setUserInfo(userItems.get(position).getName(),
                    userItems.get(position).getAddress(),
                    userItems.get(position).getBlood(),
                    userItems.get(position).getPhone());
        }

        @Override
        public int getItemCount() {
            return userItems.size();
        }


        class UserItemHolder extends RecyclerView.ViewHolder {

            TextView mUserName, mUserAddress, mUserBlood, mUserPhone;

            public UserItemHolder(View itemView) {
                super(itemView);
                mUserName = itemView.findViewById(R.id.name_text);
                mUserAddress = itemView.findViewById(R.id.address_text);
                mUserBlood = itemView.findViewById(R.id.blood_text);
                mUserPhone = itemView.findViewById(R.id.number_text);

            }

            void setUserInfo(String name, String address, String blood, String phone) {
                mUserName.setText(name);
                mUserAddress.setText(address);
                mUserBlood.setText(blood);
                mUserPhone.setText(phone);
            }

        }
    }

}
