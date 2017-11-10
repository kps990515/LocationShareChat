package android.daehoshin.com.locationsharechat.room;

import android.content.Context;
import android.daehoshin.com.locationsharechat.R;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2017-11-10.
 */

public class CustomMemberPopup extends FrameLayout{

    private Map<String, Uri> profiles = new HashMap<>();
    private List<String> mem_names = new ArrayList<>();
    RecyclerView memberList;
    private View view;
    MemberAdapter adapter;


    public CustomMemberPopup(@NonNull Context context) {
        super(context);
        initView();
    }

    public void addMember(String uid, Uri uri, String name){
        profiles.put(uid, uri);
        mem_names.add(name);
        adapter.addMember(uid,uri,name);
    }


    private void initView(){
        adapter = new MemberAdapter(profiles,mem_names);
        memberList.setAdapter(adapter);
        memberList.setLayoutManager(new LinearLayoutManager(getContext()));
        view = LayoutInflater.from(getContext()).inflate(R.layout.item_list_members,null);
        memberList = findViewById(R.id.membersList);
        addView(view);
    }


}
