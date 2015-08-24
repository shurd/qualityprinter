package vandyapps.com.qualityprinter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Sam on 8/3/2015.
 */
public class RegisterFragment extends Fragment {
    private EditText emailField, passwordField, reenterField, numberField;
    private String email="", password1="", password2="", number="";
    private Button registerButton;
    private LinearLayout mLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.register_fragment, parent, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        emailField = (EditText)v.findViewById(R.id.register_email);
        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                email = s.toString();
            }
        });

        passwordField = (EditText)v.findViewById(R.id.create_password);
        passwordField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                password1 = s.toString();
            }
        });

        reenterField = (EditText)v.findViewById(R.id.reenter_password);
        reenterField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                password2 = s.toString();
            }
        });
        numberField = (EditText)v.findViewById(R.id.phone_number);
        numberField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                number = s.toString();
            }
        });

        registerButton = (Button)v.findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValues()){
                    final ProgressDialog dig = new ProgressDialog(getActivity());
                    dig.setTitle("Please Wait");
                    dig.setMessage("Signing up. Please wait");
                    dig.show();

                    //set up parse user
                    final ParseUser user = new ParseUser();
                    user.setUsername(email);
                    user.setPassword(password1);
                    if(number.length() !=0){
                        user.put("phoneNumber", number);
                    }
                    user.put("isPrinting", true);
                    //call signup method
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            dig.dismiss();
                            if(e == null){
                                //it worked
                                Intent i = new Intent(getActivity(), SetupActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra("userID", user.getObjectId());
                                startActivity(i);
                            } else {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                //it didnt work
                            }
                        }
                    });
                    //create new user
                }
            }
        });

        mLayout = (LinearLayout)v.findViewById(R.id.click_to_hide1);
        mLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(),0);
                return false;
            }
        });

        return v;
    }

    private boolean checkValues(){
        if(email.length() == 0){
            Toast.makeText(getActivity(), "Enter a valid email", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(!email.contains("@")){
            Toast.makeText(getActivity(), "Enter a valid email", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(password1.length() == 0){
            Toast.makeText(getActivity(), "Enter a password", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(password2.length() == 0){
            Toast.makeText(getActivity(), "Re-enter your password", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(!password1.equals(password2)){
            Toast.makeText(getActivity(), "The passwords do not match", Toast.LENGTH_LONG).show();
            return false;
        }
        else
            return true;
    }
}
