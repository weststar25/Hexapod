package ssu.deslab.hexapod;

/**
 * Created by critic on 2017. 7. 17..
 */

import android.support.v7.app.AppCompatActivity;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;

public abstract class BindActivity <B extends ViewDataBinding> extends AppCompatActivity {

    protected B binding;

    protected abstract int getLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

}
