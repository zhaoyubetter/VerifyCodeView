package ui.github.com;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ui.github.com.lib.PrivacyLockView;


public class MainActivity extends AppCompatActivity {

    private PrivacyLockView lock_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lock_view = (PrivacyLockView) findViewById(R.id.lock_view);

        lock_view.addOnTextChangedListener(new PrivacyLockView.OnTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence editable, int lastLength, int length) {
            }
        });
        lock_view.setOnTextSubmitListener(new PrivacyLockView.OnTextSubmitListener() {
            @Override
            public void onSubmit(CharSequence editable) {

            }
        });
    }
}
