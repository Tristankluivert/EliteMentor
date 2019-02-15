package ng.com.hybrid.elitementor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {
    Animation frombottom, fromtop;
    CardView sign;
    ImageView logo;
    TextView next;
    CardView cardemail, cardpassword, cardpassword1;
    EditText email,password,password2;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);

        sign = findViewById(R.id.sign);
        logo = findViewById(R.id.logo);
        next = findViewById(R.id.next);
        cardemail = findViewById(R.id.cardemail);
        cardpassword = findViewById(R.id.cardpassword);
        cardpassword1 = findViewById(R.id.cardpassword1);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);


        //Animation Code
        sign.startAnimation(frombottom);
        next.startAnimation(frombottom);
        logo.startAnimation(fromtop);
        cardemail.startAnimation(fromtop);
        cardpassword.startAnimation(fromtop);
        cardpassword1.startAnimation(fromtop);



        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ally = new Intent(getApplicationContext(),Login.class);
                startActivity(ally);
                overridePendingTransition(R.anim.fromright, R.anim.fromleft);
            }
        });

    }

    private void sendUserIn() {
        Intent wel = new Intent(getApplicationContext(),MainActivity.class);
        wel.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(wel);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        if(currentuser != null){
            sendUserIn();
        }
    }

    private void createAccount() {

        String emailo = email.getText().toString();
        String passswordo = password.getText().toString();
        String password1o = password2.getText().toString();

        if(TextUtils.isEmpty(emailo)){
            email.setError("Email please");
            email.requestFocus();

        }else if(TextUtils.isEmpty(passswordo)){
            password.setError("Password please");
            password.requestFocus();

        }else if(TextUtils.isEmpty(password1o)){
            password2.setError("Confirm password please");
            password2.requestFocus();

        }else if(!password1o.equals(passswordo)){
            password2.setError("Password does not match");
            password2.requestFocus();
        }else{

            progressDialog.setTitle("Creating New Account");
            progressDialog.setMessage("Please wait while we register your email");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(emailo,passswordo).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                        sendUserToDetails();

                    }else{
                        progressDialog.dismiss();
                        String message = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(),"Error occured"+message,Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }



    }

    private void sendUserToDetails() {

        Intent usersetup = new Intent(getApplicationContext(),UserDetails.class);
        usersetup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(usersetup);
        finish();

    }



}
