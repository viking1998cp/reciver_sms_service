package atis.vn.smsnotification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import atis.vn.smsnotification.model.SMS;


public class CommonItemAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private List<SMS> arrData;
    private Context context;

    private ClickAppItemListener clickListener;

    public CommonItemAdapter(Context context) {
        this.context = context;
        this.arrData = new ArrayList<>();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_common_app, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }


    public void setOnItemClickListener(ClickAppItemListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return arrData == null ? 0 : arrData.size();
    }

    public void addItems(ArrayList<SMS> arrAppMore) {
        arrData.addAll(arrAppMore);
        notifyDataSetChanged();
    }

    public void updateList(ArrayList<SMS> arrAppMore) {
        arrData = arrAppMore;
        notifyDataSetChanged();
    }


    public void clear() {
        arrData.clear();
        notifyDataSetChanged();
    }

    SMS getItem(int position) {
        return arrData.get(position);
    }

    public class ViewHolder extends BaseViewHolder implements View.OnClickListener {
        TextView tvAppTitle;
        TextView tvContent;

        ViewHolder(View itemView) {
            super(itemView);
            tvAppTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);

            itemView.setOnClickListener(this);
        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            SMS item = arrData.get(position);

            tvAppTitle.setText("From: " + item.getSender() + " | Attempt: " + item.getAttempt() + " | Stt: " + item.getStatus());
            tvContent.setText("Code: " +item.getCode() + " - Full: " + item.getContent());
        }

        @Override
        public void onClick(View v) {
            if(clickListener != null)
                clickListener.onClick(arrData.get(getCurrentPosition()));
        }
    }
}
