/**
 * Project 4, Task 2
 * Nicholas Thomas, nhthomas
 * Kelly McManus, kellymcm
 *
 * date: 2022.11.01
 * purpose: this class, while largely taken from stack overflow,
 * sets up a recycler view to display a list of trades based on user input
 */

package edu.heinz.ds.project4task2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Class: RecyclerViewAdapter
 *
 * This class was taken from this StackOverflow post.
 * Source: https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<CongressTrades> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    RecyclerViewAdapter(Context context, List<CongressTrades> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CongressTrades tradeFact = mData.get(position);

        // format string for view
        String reportString = "";
        reportString += "House: " + tradeFact.getHouse() + "\n";
        reportString += "Congressperson Name: " + tradeFact.getRepresentative() + "\n";
        reportString += "Trade Size: " + tradeFact.getRange() + "\n";
        reportString += "Trade Side: " + tradeFact.getTransaction() + "\n";
        reportString += "Trade Date: " + tradeFact.getTransactionDate();

        // set sting for view
        holder.myTextView.setText(reportString.toString());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.congressTradeFactView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
