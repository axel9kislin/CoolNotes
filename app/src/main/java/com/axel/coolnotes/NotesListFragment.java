package com.axel.coolnotes;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class NotesListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private Cursor mCursor;
    private NotesRecyclerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mCursor = DBHelper.getAllNotes(getContext());
        mAdapter = new NotesRecyclerAdapter(getContext(),mCursor);
        mAdapter.SetOnItemClickListener(new NotesRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(MainActivity.LOG_TAG, "OnItemClickListener, click on " + position);
                Intent intent = new Intent(getContext(),DetailNote.class);
                mCursor.moveToPosition(position);
                intent.putExtra(DetailNote.EXTRA_ID, mCursor.getString(0));
                Log.d(MainActivity.LOG_TAG, "in extra data we have: " + intent.getStringExtra(DetailNote.EXTRA_ID));
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        EventBus.getDefault().register(this);
    }
    @Subscribe
    public void OnEvent(UpdateData event)
    {
        String recievedData = event.getData();
        Log.d(MainActivity.LOG_TAG,"we received from AddNote is:"+recievedData);
        switch (recievedData) {
            case "addNote":
            {
                mCursor = DBHelper.getAllNotes(getContext());
                mAdapter.refreshData(mCursor);
                mAdapter.notifyItemInserted(mCursor.getCount());
            }
            case "deleteNote":
            {
                mCursor = DBHelper.getAllNotes(getContext());
                mAdapter.refreshData(mCursor);
                mAdapter.notifyDataSetChanged();
                //mAdapter.notifyItemRemoved(); что сюда передать пока хз
            }
            case "editNote" :
            {
                mCursor = DBHelper.getAllNotes(getContext());
                mAdapter.refreshData(mCursor);
                mAdapter.notifyDataSetChanged();
                //mAdapter.notifyItemChanged(); тоже самое, хз что передать
            }
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

        mRecyclerView = null;
        mAdapter = null;
        mCursor = null;

        super.onDestroy();
    }

}
