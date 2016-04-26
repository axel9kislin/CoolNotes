package com.axel.coolnotes;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Александр on 22.04.2016.
 */
public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {

    private OnItemClickListener mItemClickListener;
    private Cursor mCursor;

    public NotesRecyclerAdapter(Context context, Cursor cursor)
    {
        mCursor = cursor;
    }

    @Override
    public NotesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NotesRecyclerAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        TextView text = (TextView)holder.itemView.findViewById(R.id.textViewInItem);
        text.setText(mCursor.getString(1));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        View v1;
        public ViewHolder(View itemView) {
            super(itemView);

            v1 = itemView.findViewById(R.id.allItemViewID);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getLayoutPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }
    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void refreshData(Cursor cursor)
    {
        mCursor = cursor;
    }

}
