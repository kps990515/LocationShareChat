package a.daehoshin.com.locationsharechat.room;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by user on 2017-11-10.
 */

public class CustomMemberPopup extends FrameLayout{
    private MemberAdapter adapter;

    public CustomMemberPopup(@NonNull Context context) {
        super(context);

        initView();
    }

    public void addMember(String uid, Uri uri, String name){
        adapter.addMember(uid, uri, name);
    }

    private void initView(){
        View view = LayoutInflater.from(getContext()).inflate(a.daehoshin.com.locationsharechat.R.layout.item_list_members, null);

        RecyclerView memberList = view.findViewById(a.daehoshin.com.locationsharechat.R.id.membersList);
        adapter = new MemberAdapter();
        memberList.setAdapter(adapter);
        memberList.setLayoutManager(new LinearLayoutManager(getContext()));

        addView(view);
    }
}
