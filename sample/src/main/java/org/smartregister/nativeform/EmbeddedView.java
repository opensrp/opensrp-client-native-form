package org.smartregister.nativeform;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.vijay.jsonwizard.views.NativeForm;

public class EmbeddedView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_embedded_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final NativeForm nativeForm = findViewById(R.id.native_form);
        nativeForm.setJsonFrom("sample_form");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        findViewById(R.id.tvNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nativeForm.nextClick();
            }
        });

        findViewById(R.id.tvPrev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nativeForm.backClick();
            }
        });
    }

}
