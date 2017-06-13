package ui.github.com;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import ui.github.com.lib.PrivacyLockView;

public class BaseUseActivity extends AppCompatActivity {

    private PrivacyLockView lock_view;
    private TextView userInput;
    private CheckBox chk;
    private SeekBar seekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_use);

        lock_view = (PrivacyLockView) findViewById(R.id.lock_view);
        userInput = (TextView) findViewById(R.id.userInput);
        chk = (CheckBox) findViewById(R.id.chk);
        seekbar = (SeekBar) findViewById(R.id.seekbar);


        lock_view.setOnTextSubmitListener(new PrivacyLockView.OnTextSubmitListener() {
            @Override
            public void onSubmit(CharSequence editable) {
                Toast.makeText(getApplicationContext(), "输入完毕：" + editable.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lock_view.setEncrypt(isChecked);
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress < 4) {
                    progress = 4;
                }
                lock_view.setItemCount(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
