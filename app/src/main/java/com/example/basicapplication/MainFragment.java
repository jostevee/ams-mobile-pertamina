package com.example.basicapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {
    DatabaseHelper myDB;

    //int[] colorIntArray = {R.color.walking,R.color.running,R.color.biking,R.color.paddling,R.color.golfing};
    /* VARIABLE USED INSIDE THE ACTIVITY
    private final int[] iconIntArray = {
            R.drawable.ic_chats,
            R.drawable.ic_new_chat,
            R.drawable.ic_menu_share};
    private TabLayout tabLayout;
    private final int[] tabIcons = {
            R.drawable.friends_and_groups,
            R.drawable.messages,
            R.drawable.recent_call
    };
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //layout untuk fragment
        return inflater.inflate(R.layout.activity_main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myDB = new DatabaseHelper(getActivity());
    }
}
