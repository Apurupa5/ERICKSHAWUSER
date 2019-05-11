package com.iiitd.apurupa.mcproject.bookmyrickshaw;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by NB VENKATESHWARULU on 11/28/2016.
 */
public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder>  {


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mDateTextView;
        public TextView mpickupTextView;
        public TextView mdestTextView;
        public TextView mdriverTextView;
        public LinearLayout l1;

        public ViewHolder(View itemView, final Context context) {
            super(itemView);
            mDateTextView = (TextView) itemView.findViewById(R.id.item_date);
            mpickupTextView = (TextView) itemView.findViewById(R.id.item_pickup);
            mdestTextView = (TextView) itemView.findViewById(R.id.item_dest);
            mdriverTextView = (TextView) itemView.findViewById(R.id.item_driver);


        }


    }

    private List<RideList> mrideList;

    private Context mContext;


    public ItemListAdapter(Context context, List<RideList> rideLists) {
        mrideList = rideLists;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }
    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View todoitemView = inflater.inflate(R.layout.item_todolist, parent, false);

        ViewHolder viewHolder = new ViewHolder(todoitemView,context);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ItemListAdapter.ViewHolder holder, int position) {

         RideList rdlist = mrideList.get(position);

        // Set item views based on your views and data model
        TextView textView =holder.mdriverTextView;
        textView.setText(rdlist.getDrivername());
        TextView textView1 =holder.mpickupTextView;
        textView1.setText(rdlist.getPickup());
        TextView textView2 =holder.mdestTextView;
        textView2.setText(rdlist.getDestination());
       // TextView textView3 =holder.mDateTextView;
       // textView3.setText((CharSequence) rdlist.getRidedate());



    }

    @Override
    public int getItemCount() {
        return mrideList.size();
    }
}
