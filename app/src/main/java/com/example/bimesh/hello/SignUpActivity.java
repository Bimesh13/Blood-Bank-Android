package com.example.bimesh.hello;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    private static final String TAG = "SignUpActivity" ;
    private EditText inputName, inputNumber, inputAddress, inputEmail, inputPassword,inputDate;
    private Button btnSignIn, btnSignUp;
    private Spinner spinner;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private String Blood;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null) {
            //handle the already login user
        }
    }



    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        btnSignIn = (Button) findViewById(R.id.log_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputName = (EditText) findViewById(R.id.name);
        inputNumber = (EditText) findViewById(R.id.phone);
        inputAddress = (EditText) findViewById(R.id.address);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputDate = (EditText) findViewById(R.id.date);
        spinner = (Spinner) findViewById(R.id.spinner);
        auth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.blood,android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        inputDate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v){
               Calendar cal= Calendar.getInstance();
               int year = cal.get(Calendar.YEAR);
               int month = cal.get(Calendar.MONTH);
               int day = cal.get(Calendar.DAY_OF_MONTH);

               DatePickerDialog dialog =  new DatePickerDialog(SignUpActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
               dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
               dialog.show();
           }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyyy" + dayOfMonth + "/" + month + "/" + year);
                String date = dayOfMonth + "/" + month + "/" + year;
                inputDate.setText(date);
            }
        };


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          final String name = inputName.getText().toString().trim();
          final String phone = inputNumber.getText().toString().trim();
          final String address = inputAddress.getText().toString().trim();
          final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
          final String date = inputDate.getText().toString().trim();
          final String blood= Blood.trim();

                if (name.isEmpty()) {
                    inputName.setError(getString(R.string.input_error_name));
                    inputName.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    inputEmail.setError(getString(R.string.input_error_email));
                    inputEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.setError(getString(R.string.input_error_email_invalid));
                    inputEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    inputPassword.setError(getString(R.string.input_error_password));
                    inputPassword.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    inputPassword.setError(getString(R.string.input_error_password_length));
                    inputPassword.requestFocus();
                    return;
                }

                if (phone.isEmpty()) {
                    inputNumber.setError(getString(R.string.input_error_phone));
                    inputNumber.requestFocus();
                    return;
                }

                if (phone.length() != 10) {
                    inputNumber.setError(getString(R.string.input_error_phone_invalid));
                    inputNumber.requestFocus();
                    return;
                }
                if (address.isEmpty()) {
                    inputAddress.setError(getString(R.string.input_error_address));
                    inputAddress.requestFocus();
                    return;
                }
                if (date.isEmpty()) {
                    inputDate.setError(getString(R.string.input_error_date));
                    inputDate.requestFocus();
                    return;
                }




                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    User user = new User(
                                            name,
                                            email,
                                            phone,
                                            address,
                                            date,
                                            blood);
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                                finish();
                                            } else {
                                                //display a failure message
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });


}
});
}
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        TextView myText=(TextView) view;
        Blood= myText.getText().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}












