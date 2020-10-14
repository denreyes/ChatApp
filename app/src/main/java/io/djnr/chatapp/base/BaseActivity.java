package io.djnr.chatapp.base;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kaopiz.kprogresshud.KProgressHUD;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class BaseActivity extends AppCompatActivity {
    private KProgressHUD mProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // This is for Calligraphy or the font library
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public void showProgress() {
        // For showing Progress Bar Dialog
        mProgress = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        mProgress.show();
    }

    public void hideProgress() {
        // For dismissing Progress Bar Dialog
        if (mProgress != null)
            mProgress.dismiss();
    }
}
