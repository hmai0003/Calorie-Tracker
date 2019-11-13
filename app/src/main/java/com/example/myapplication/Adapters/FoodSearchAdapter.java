package com.example.myapplication.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.SearchActivity;

import java.util.ArrayList;

public class FoodSearchAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> data;

    private TextView lblFoodName;
    private Button btnConsume;

    public FoodSearchAdapter(Context context, ArrayList<String> data) {
        super(context, 0, data);
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_foodsearch, parent, false);
        }

        lblFoodName = convertView.findViewById(R.id.lblFoodSearch);
        lblFoodName.setText(data.get(position));
        lblFoodName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDescription(position);
            }
        });

        btnConsume = convertView.findViewById(R.id.btnAdd);
        btnConsume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFoodDetails(position);
            }
        });

        return convertView;
    }

    public void getDescription(int index) {
        SearchActivity searchActivity = (SearchActivity)context;
        searchActivity.getFoodDescription(index);
    }

    public void getFoodDetails(int index) {
        SearchActivity searchActivity = (SearchActivity)context;
        searchActivity.getFoodDetails(index);
    }

}
