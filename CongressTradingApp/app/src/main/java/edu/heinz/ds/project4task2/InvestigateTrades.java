/**
 * Project 4, Task 2
 * Nicholas Thomas, nhthomas
 * Kelly McManus, kellymcm
 *
 * date: 2022.11.01
 * purpose: this class handles gui elements and performs actions at different states of the android app lifecycle
 */

package edu.heinz.ds.project4task2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InvestigateTrades extends AppCompatActivity {

    InvestigateTrades me = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final InvestigateTrades ma = this;

        // Extract submit button from layout
        Button submitButton = (Button)findViewById(R.id.submit);

        // Add a listener to the submit button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                String ticker = ((EditText)findViewById(R.id.searchTerm)).getText().toString();
                System.out.println("searchTerm = " + ticker);
                GetTrades gt = new GetTrades();
                gt.search(ticker, me, ma);
            }
        });
    }

    public void tradesReady(List<CongressTrades> trades) {
        // send trades to recycler view adapter
        RecyclerViewAdapter adapter;
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.tradeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, trades);
        recyclerView.setAdapter(adapter);

        // set text view with trade count and brief summary
        TextView tv = (TextView)findViewById(R.id.numberOfTradesSummary);
        tv.setText("Returned " + trades.size() + " trades for " + ((EditText)findViewById(R.id.searchTerm)).getText().toString());
    }

}


