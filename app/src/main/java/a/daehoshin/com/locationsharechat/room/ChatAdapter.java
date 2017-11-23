package a.daehoshin.com.locationsharechat.room;

import a.daehoshin.com.locationsharechat.domain.room.Msg;
import a.daehoshin.com.locationsharechat.util.FormatUtil;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2017-11-09.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.Holder>{

    private List<Msg> data = new ArrayList<>();
    private String currentUserUid;
    private Map<String, Uri> profiles = new HashMap<>();

    public ChatAdapter(String currentUserUid){
        this.currentUserUid = currentUserUid;
    }

    public void addMsg(Msg msg){
        this.data.add(msg);
        notifyDataSetChanged();
    }

    public void addProfile(String uid, Uri uri){
        profiles.put(uid, uri);
    }

    @Override
    public ChatAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(a.daehoshin.com.locationsharechat.R.layout.item_list_chat, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(ChatAdapter.Holder holder, int position) {
        Msg msg = data.get(position);

        if(msg.getUid().equals(currentUserUid)){
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.txt_rmsg.setText(msg.getMessage());
            holder.txt_rtime.setText(FormatUtil.changeTimeFormatLongToString(msg.getTime()));
        }
        else{
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.txt_msg.setText(msg.getMessage());
            holder.txt_name.setText(msg.getName());
            holder.txt_time.setText(FormatUtil.changeTimeFormatLongToString(msg.getTime()));
            Uri uri = profiles.get(msg.getUid());
            if(uri != null) Glide.with(holder.itemView.getContext()).load(uri).apply(RequestOptions.circleCropTransform()).into(holder.image_profile);
            else holder.image_profile.setImageResource(a.daehoshin.com.locationsharechat.R.drawable.ic_action_name);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ConstraintLayout leftLayout, rightLayout;
        CardView cardView, cardViewL;
        TextView txt_msg, txt_name, txt_count, txt_time;
        ImageView image_profile;
        TextView txt_rmsg, txt_rcount, txt_rtime;

        public Holder(View itemView) {
            super(itemView);

            leftLayout = itemView.findViewById(a.daehoshin.com.locationsharechat.R.id.leftLayout);
            rightLayout = itemView.findViewById(a.daehoshin.com.locationsharechat.R.id.rightLayout);
            cardView = itemView.findViewById(a.daehoshin.com.locationsharechat.R.id.cardView);
            cardViewL = itemView.findViewById(a.daehoshin.com.locationsharechat.R.id.cardViewR);
            txt_msg = itemView.findViewById(a.daehoshin.com.locationsharechat.R.id.txt_msg);
            txt_name = itemView.findViewById(a.daehoshin.com.locationsharechat.R.id.txt_membername);
            txt_count = itemView.findViewById(a.daehoshin.com.locationsharechat.R.id.txt_count);
            txt_time = itemView.findViewById(a.daehoshin.com.locationsharechat.R.id.txt_time);
            image_profile = itemView.findViewById(a.daehoshin.com.locationsharechat.R.id.image_memerProfile);
            txt_rmsg = itemView.findViewById(a.daehoshin.com.locationsharechat.R.id.txt_rmsg);
            txt_rcount = itemView.findViewById(a.daehoshin.com.locationsharechat.R.id.txt_rcount);
            txt_rtime = itemView.findViewById(a.daehoshin.com.locationsharechat.R.id.txt_rtime);
        }
    }
}
