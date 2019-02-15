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

public class Login extends AppCompatActivity {

    Animation frombottom, fromtop;
    CardView btnlogin;
    ImageView logo;
    TextView next;
    CardView cardemail, cardpassword;
    EditText email, password;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);

        btnlogin = findViewById(R.id.btnlogin);
        logo = findViewById(R.id.logo);
        next = findViewById(R.id.next);
        cardemail = findViewById(R.id.cardemail);
        cardpassword = findViewById(R.id.cardpassword);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);


        btnlogin.startAnimation(frombottom);
        next.startAnimation(frombottom);
        logo.startAnimation(fromtop);
        cardemail.startAnimation(fromtop);
        cardpassword.startAnimation(fromtop);



        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInUser();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendUserToSign();
                overridePendingTransition(R.anim.fromleft, R.anim.fromright);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentuser = mAuth.getCurrentUser();
        if(currentuser != null){
            sendUserIn();
        }
    }

    private void logInUser() {

        String emailo = email.getText().toString();
        String passswordo = password.getText().toString();


        if (TextUtils.isEmpty(emailo)) {
            email.setError("Email please");
            email.requestFocus();

        } else if (TextUtils.isEmpty(passswordo)) {
            password.setError("Password please");
            password.requestFocus();

        }else{
            progressDialog.setTitle("Welcome");
            progressDialog.setMessage("Please wait while we log you in");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(emailo,passswordo).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                      Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                        sendUserIn();

                    }else{
                        progressDialog.dismiss();
                        String message = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(),"Error occured: "+message,Toast.LENGTH_SHORT).show();
                    }

                }
            });


        }

    }

    private void sendUserIn() {
        Intent wel = new Intent(getApplicationContext(),MainActivity.class);
        wel.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(wel);
        finish();
    }


    private void sendUserToSign () {
        Intent newact = new Intent(getApplicationContext(), SignUp.class);
        startActivity(newact);

    }


}

