package envisionanalytics.example.enactus.envisionanalytics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    FirebaseAuth mAuth;
    SharedPreferences sp;
    String email1;
    String pass1;

    public void forget(View view)
    {
        Intent i = new Intent(this,forgot.class);
        startActivity(i);
    }

    public void login(View view)
    {
        email1 = email.getText().toString();
        sp.edit().putString("email",email1).apply();

        pass1 = password.getText().toString();

        if(!email1.isEmpty() && !pass1.isEmpty()) {
            Toast.makeText(MainActivity.this, "Signing in...", Toast.LENGTH_SHORT).show();
            mAuth.signInWithEmailAndPassword(email1, pass1)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("success", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent i = new Intent(MainActivity.this, loading.class);
                                i.putExtra("email",email1);
                                startActivity(i);

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();

                            }

                            // ...
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "Email and Password required", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        sp = this.getSharedPreferences("envisionanalytics.example.enactus.envisionanalytics", Context.MODE_PRIVATE);
        email = (EditText)findViewById(R.id.editText);
        password = (EditText)findViewById(R.id.editText3);
        mAuth = FirebaseAuth.getInstance();

        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser!=null)
            {
                Intent i = new Intent(MainActivity.this,loading.class);
                startActivity(i);
            }
        }
        catch(Exception e)
        {}

    }
}
