package ssu.deslab.hexapod.history;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ssu.deslab.hexapod.R;
import ssu.deslab.hexapod.databinding.ActivityHistoryItemBinding;
import ssu.deslab.hexapod.history.common.model.HistoryData;
import ssu.deslab.hexapod.history.common.view.BaseRecyclerViewAdapter;

/**
 * Created by critic on 2017. 7. 17..
 */

public class HistoryAdapter extends BaseRecyclerViewAdapter<HistoryData, HistoryAdapter.HistoryViewHolder> {

    public HistoryAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindView(HistoryViewHolder holder, int position) {
        HistoryData data = getItem(position);
        holder.binding.setData(data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        ActivityHistoryItemBinding binding;
        public HistoryViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
