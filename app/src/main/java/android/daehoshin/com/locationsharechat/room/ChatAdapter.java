package android.daehoshin.com.locationsharechat.room;

import android.daehoshin.com.locationsharechat.R;
import android.daehoshin.com.locationsharechat.domain.room.Msg;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 2017-11-09.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.Holder>{

    private List<Msg> data;
    private String currentUserUid;

    public ChatAdapter(String currentUserUid){
        this.currentUserUid = currentUserUid;
    }

    public void dataRefresh(List<Msg> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public ChatAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_chat,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(ChatAdapter.Holder holder, int position) {
        Msg msg = data.get(position);
        if(msg.getUid().equals(currentUserUid)){
            holder.rightLayout.setVisibility(View.GONE);
            holder.txt_rmsg.setText(msg.getMessage());
            holder.txt_rtime.setText(msg.getTime()+"");
        }else{
            holder.leftLayout.setVisibility(View.GONE);
            holder.txt_msg.setText(msg.getMessage());
            holder.txt_name.setText(msg.getName());
            holder.txt_time.setText(msg.getTime()+"");
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ConstraintLayout leftLayout;
        ConstraintLayout rightLayout;
        CardView cardView;
        CardView cardViewL;
        TextView txt_msg;
        TextView txt_name;
        TextView txt_count;
        TextView txt_time;
        ImageView image_profile;
        TextView txt_rmsg;
        TextView txt_rcount;
        TextView txt_rtime;
        public Holder(View itemView) {
            super(itemView);
            leftLayout = itemView.findViewById(R.id.leftLayout);
            rightLayout = itemView.findViewById(R.id.rightLayout);
            cardView = itemView.findViewById(R.id.cardView);
            cardViewL = itemView.findViewById(R.id.cardViewR);
            txt_msg = itemView.findViewById(R.id.txt_msg);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_count = itemView.findViewById(R.id.txt_count);
            txt_time = itemView.findViewById(R.id.txt_time);
            image_profile = itemView.findViewById(R.id.image_profile);
            txt_rmsg = itemView.findViewById(R.id.txt_rmsg);
            txt_rcount = itemView.findViewById(R.id.txt_rcount);
            txt_rtime = itemView.findViewById(R.id.txt_rtime);
        }
    }
}
