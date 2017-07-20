package ssu.deslab.hexapod.history;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import ssu.deslab.hexapod.R;
import ssu.deslab.hexapod.databinding.ActivityHistoryBodyBinding;
import ssu.deslab.hexapod.history.common.util.DataUtil;
import ssu.deslab.hexapod.history.common.SimpleDividerItemDecoration;

/**
 * Created by critic on 2017. 7. 4..
 */

public class HistoryActivity extends AppCompatActivity {
    ActivityHistoryBodyBinding ahib;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ahib = DataBindingUtil.setContentView(this, R.layout.activity_history_body);
        ahib.setActivity(this);

        setRecyclerView();
    }

    private void setRecyclerView() {
        ahib.historyRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ahib.historyRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        HistoryAdapter adapter = new HistoryAdapter(this);
        ahib.historyRecyclerView.setAdapter(adapter);

        adapter.updateItems(DataUtil.getDatas());
    }
}
