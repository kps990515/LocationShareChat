package android.daehoshin.com.locationsharechat.room;

import android.daehoshin.com.locationsharechat.R;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2017-11-10.
 */

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.Holder> {

    private Map<String, Uri> profiles = new HashMap<>();
    private List<String> mem_names = new ArrayList<>();
    private List<String> profiles_uid = new ArrayList<>();
    private List<Uri> profiles_uri = new ArrayList<>();


    public MemberAdapter(Map<String, Uri> profiles,List<String> mem_names){
        this.profiles = profiles;
        this.mem_names = mem_names;
        seperateMap(profiles);
    }

    public void seperateMap(Map<String, Uri> profiles){
        for(String key : profiles.keySet()){
            profiles_uid.add(key);
            profiles_uri.add(profiles.get(key));
        }
    }

    public void addMember(String uid, Uri uri, String name){
        profiles.put(uid, uri);
        mem_names.add(name);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_member, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Member member = new Member();
        member.name = mem_names.get(position);
        member.uid = profiles_uid.get(position);
        member.uri = profiles_uri.get(position);
        holder.txt_membername.setText(member.name);
        holder.image_memberProfile.setImageURI(profiles.get(member.uid));
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView txt_membername;
        ImageView image_memberProfile;
        public Holder(View itemView) {
            super(itemView);
            txt_membername = itemView.findViewById(R.id.txt_membername);
            image_memberProfile = itemView.findViewById(R.id.image_memerProfile);
        }
    }
    class Member{
        String uid;
        Uri uri;
        String name;
    }
}
