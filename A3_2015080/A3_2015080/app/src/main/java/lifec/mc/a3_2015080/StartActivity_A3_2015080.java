package lifec.mc.a3_2015080;

// Android Libraries

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartActivity_A3_2015080 extends AppCompatActivity {

    // onCreate() Method

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start__a3_2015080);
    }

    // Intent Function

    public void startRecordingButton(View view) {
        startActivity(new Intent(StartActivity_A3_2015080.this, MainActivity_A3_2015080.class));
        finish();
    }

}
