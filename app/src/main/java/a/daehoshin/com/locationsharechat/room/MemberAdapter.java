package a.daehoshin.com.locationsharechat.room;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017-11-10.
 */

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.Holder> {
    private List<String> mem_names = new ArrayList<>();
    private List<String> profiles_uid = new ArrayList<>();
    private List<Uri> profiles_uri = new ArrayList<>();

    public void addMember(String uid, Uri uri, String name){
        profiles_uid.add(uid);
        profiles_uri.add(uri);
        mem_names.add(name);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(a.daehoshin.com.locationsharechat.R.layout.item_list_member, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        String name = mem_names.get(position);
        String uid = profiles_uid.get(position);
        Uri uri = profiles_uri.get(position);

        holder.txt_membername.setText(name);

        if(uri != null) Glide.with(holder.itemView.getContext()).load(uri).apply(RequestOptions.circleCropTransform()).into(holder.image_memberProfile);
        else holder.image_memberProfile.setImageResource(a.daehoshin.com.locationsharechat.R.drawable.ic_action_name);
    }

    @Override
    public int getItemCount() {
        return profiles_uid.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView txt_membername;
        ImageView image_memberProfile;

        public Holder(View itemView) {
            super(itemView);

            txt_membername = itemView.findViewById(a.daehoshin.com.locationsharechat.R.id.txt_membername);
            image_memberProfile = itemView.findViewById(a.daehoshin.com.locationsharechat.R.id.image_memerProfile);
        }
    }
}
