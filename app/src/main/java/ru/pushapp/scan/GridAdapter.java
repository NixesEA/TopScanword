package ru.pushapp.scan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {

    ArrayList<CellUnit> items;
    Context context;

    public GridAdapter(Context context, ArrayList<CellUnit> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;

        if (view == null) {
            gridView = inflater.inflate(R.layout.grid_item, null);

            TextView letterView = gridView.findViewById(R.id.grid_item_letter);
            EditText editText = gridView.findViewById(R.id.grid_edit_letter);

//            GridItem gridItem = items.get(i);
            CellUnit gridItem = items.get(i);
            if (gridItem.getQuestion()!= null){
                letterView.setText(gridItem.getQuestion());
                letterView.setBackground(context.getDrawable(R.drawable.grid_background_question));
            } else {
                letterView.setVisibility(View.GONE);
                editText.setVisibility(View.VISIBLE);
            }
        } else {
            gridView = view;
        }

        return gridView;
    }
}
