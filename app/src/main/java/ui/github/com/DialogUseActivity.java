package ui.github.com;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import ui.github.com.lib.PrivacyLockView;

public class DialogUseActivity extends AppCompatActivity {

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_use);

        dialog = new Dialog(this);

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View content = inflater.inflate(R.layout.dialog_input, null);
        final PrivacyLockView lockView = (PrivacyLockView) content.findViewById(R.id.lock_view);

        content.findViewById(R.id.title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(content);
        final WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
        attributes.width = d.getWidth();
        dialog.getWindow().setAttributes(attributes);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        lockView.setOnTextSubmitListener(new PrivacyLockView.OnTextSubmitListener() {
            @Override
            public void onSubmit(CharSequence editable) {
                if(editable.toString().equals("000000")) {
                    Toast.makeText(getApplicationContext(), "密码正确", Toast.LENGTH_SHORT).show();
//                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(lockView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "密码错误：密码为：000000", Toast.LENGTH_SHORT).show();
                    lockView.clearEditText();
                }
            }
        });
    }

}
