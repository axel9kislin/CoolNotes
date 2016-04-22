package layout;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.axel.coolnotes.MainActivity;
import com.axel.coolnotes.NotesRecyclerAdapter;
import com.axel.coolnotes.R;

public class NotesListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private Cursor mCursor;
    public NotesRecyclerAdapter mAdapter;


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

        //что сюда добавить:
        //вместо try - getAllNotes
        // mCursos = getAllNotes();
        mAdapter = new NotesRecyclerAdapter(getContext(),mCursor);
        mAdapter.SetOnItemClickListener(new NotesRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(MainActivity.LOG_TAG,"OnItemClickListener, click on "+position);
                //дописать логику, открыть активность с экстра параметром Id.
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
        //пока не знаю, нужно ли это мне, потом удалить или реализовать нормально
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mRecyclerView = null;
        mAdapter = null;
        //так же добавить все остальные элементы и занулить их
    }

}
