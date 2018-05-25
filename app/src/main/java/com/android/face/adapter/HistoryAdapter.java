package com.android.face.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.face.R;
import com.android.face.bean.PersonInformation;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author suhu
 * @data 2018/5/15 0015.
 * @description
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{
    private Context context;
    private List<PersonInformation> list;

    public HistoryAdapter(Context context, List<PersonInformation> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_history,parent,false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int position) {
        PersonInformation information = list.get(position);
        holder.circleImageView.setImageBitmap(information.getBitmap());
        holder.name.setText(information.getName());
        holder.time.setText(information.getTime());
        if (information.getPass()){
            holder.pass.setTextColor(Color.parseColor("#00FF00"));
            holder.pass.setText("验证通过");
        }else {
            holder.pass.setText("验证失败");
            holder.pass.setTextColor(Color.parseColor("#FF0000"));
        }

    }

    @Override
    public int getItemCount() {
        return list!=null?list.size():0;
    }

    public void insert(PersonInformation information){
        if (list.size()>1){
            list.remove(1);
            notifyItemRemoved(1);
        }

        list.add(0,information);
        notifyItemInserted(0);

    }


    class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView circleImageView;
        private TextView name;
        private TextView time;
        private TextView pass;

        public ViewHolder(View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            pass = itemView.findViewById(R.id.pass);
        }
    }
}
