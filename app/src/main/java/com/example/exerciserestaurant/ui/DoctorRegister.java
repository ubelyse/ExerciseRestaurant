package com.example.tnac.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.tnac.Fragments.DatePickerFragment;
import com.example.tnac.Models.Doctor;
import com.example.tnac.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DoctorRegister extends AppCompatActivity implements View.OnFocusChangeListener {
    private EditText txtUserName, txtPassWord,txtRePassWord,txtFullName,txtAddress,txtPhoneNumber,txtRole,txtDateofBirth;
    private Button btnRegister, btnCancel;
    private RadioGroup radioGroupGender;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Date date;
    private String dateOfBirth;
    private boolean gender=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getInstance().getReference();

        btnRegister = findViewById(R.id.buttonRegister);
        txtUserName = findViewById(R.id.editTextUsername);
        txtPassWord = findViewById(R.id.editTextPassword);
        txtRePassWord = findViewById(R.id.editTextRetypePassword);
        txtFullName = findViewById(R.id.editTextFullName);
        txtAddress = findViewById(R.id.editTextAddress);
        txtPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        txtRole = findViewById(R.id.editRole);
        txtDateofBirth = findViewById(R.id.editTextDateOfBirth);
        txtDateofBirth.setOnFocusChangeListener(this);

        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonID = radioGroupGender.getCheckedRadioButtonId();
                View radioButton = radioGroupGender.findViewById(radioButtonID);
                int idx = radioGroupGender.indexOfChild(radioButton);
                RadioButton r = (RadioButton) radioGroupGender.getChildAt(idx);
                String selectedtext = r.getText().toString();
                if (selectedtext.equals("male")) {
                    gender = true;
                } else {
                    gender = false;
                }
            }
        });

        btnCancel = findViewById(R.id.buttonCancelRegister);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iLogin = new Intent(DoctorRegister.this, Admin.class);
                startActivity(iLogin);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = txtUserName.getText().toString().trim();
                final String password = txtPassWord.getText().toString().trim();
                final String repassword = txtRePassWord.getText().toString().trim();
                final String fullname = txtFullName.getText().toString().trim();
                final String address = txtAddress.getText().toString().trim();
                final String phonenumber = txtPhoneNumber.getText().toString().trim();
                final String role = txtRole.getText().toString().trim();

                date = new Date();
                // ngay thang
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    dateOfBirth = txtDateofBirth.getText().toString().trim();
                    date = formatter.parse(dateOfBirth);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter your email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter a password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password must be longer than 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(repassword)) {
                    Toast.makeText(getApplicationContext(), "password incorrect!", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {


                                if (!task.isSuccessful()) {
                                    Toast.makeText(DoctorRegister.this, "Registration error, please check again. Each email only registered 1 time only!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DoctorRegister.this, "Registration is complete!",
                                            Toast.LENGTH_SHORT).show();

                                    Doctor account = new Doctor(email,phonenumber,address,dateOfBirth,"",fullname,role,gender);
                                    String uid = firebaseAuth.getCurrentUser().getUid();
                                    databaseReference.child("doctors").child(uid).setValue(account);
                                    startActivity(new Intent(DoctorRegister.this, Admin.class));
                                    finish();
                                }
                            }

                        });
            }
        });
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.editTextDateOfBirth:
                if(hasFocus){
                    DatePickerFragment datePickerFragment = new DatePickerFragment();
                    datePickerFragment.show(getSupportFragmentManager(),"Date of birth");
                }
                break;
        }
    }
}