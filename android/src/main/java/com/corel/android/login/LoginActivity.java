package com.corel.android.login;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.corel.android.BaseButterKnifeActivity;
import com.corel.android.R;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.regex.Pattern;

import butterknife.Bind;
import rx.Observable;

/**
 * 登录
 */
public class LoginActivity extends BaseButterKnifeActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Pattern emailPattern = Pattern.compile(
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        final Pattern telRegexPattern = Pattern.compile("^13\\d{9}|14[57]\\d{8}|15[012356789]\\d{8}|18[01256789]\\d{8}|17[0678]\\d{8}$");
        Observable<Boolean> userNameValid = RxTextView.textChanges(loginName)
                .map(t -> telRegexPattern.matcher(t).matches());
        Observable<Boolean> passwordValid = RxTextView.textChanges(password)
                .map(t -> t.length() > 6);

        userNameValid.doOnNext(b -> Log.d("[Rx]", "loginName " + (b ? "Valid" : "Invalid")))
                .doOnCompleted(() -> {Log.d("[Rx]","loginName Completed");})
                .map(b -> b ? Color.BLACK : Color.RED)
                .subscribe(color -> loginName.setTextColor(color));

        passwordValid.doOnNext(b -> Log.d("[Rx]", "password " + (b ? "Valid" : "Invalid")))
                .doOnCompleted(() -> {Log.d("[Rx]","loginName Completed");})
                .map(b -> b ? Color.BLACK : Color.RED)
                .subscribe(color -> password.setTextColor(color));

        Observable<Boolean> registerEnabled =
                Observable.combineLatest(userNameValid, passwordValid, (a,b) -> a && b);
        registerEnabled.distinctUntilChanged()
                .doOnNext( b -> Log.d("[Rx]", "Button " + (b ? "Enabled" : "Disabled")))
                .subscribe( enabled -> loginBtn.setEnabled(enabled));
    }

    @Bind(R.id.login_activity_edittext_username) EditText loginName;
    @Bind(R.id.login_activity_edittext_password) EditText password;

    @Bind(R.id.login_activity_button_login) Button loginBtn;
}
