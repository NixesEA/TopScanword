package ru.pushapp.scan.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import ru.pushapp.scan.JsonUtil.CellUnit;
import ru.pushapp.scan.R;

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
    public CellUnit getItem(int i) {
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
        ViewHolder viewHolder = null;

        if (view == null) {
            viewHolder = new ViewHolder();
            gridView = inflater.inflate(R.layout.grid_item, null);

//            TextView letterView = gridView.findViewById(R.id.grid_item_letter);
//            EditText editText = gridView.findViewById(R.id.grid_edit_letter);

            viewHolder.editText = gridView.findViewById(R.id.grid_edit_letter);
            viewHolder.textLetter = gridView.findViewById(R.id.grid_item_letter);

            CellUnit gridItem = items.get(i);
            if (gridItem.getQuestion() != null) {
                viewHolder.textLetter.setText(gridItem.getQuestion());
                viewHolder.textLetter.setBackground(context.getDrawable(R.drawable.grid_background_question));

//                letterView.setText(gridItem.getQuestion());
//                letterView.setBackground(context.getDrawable(R.drawable.grid_background_question));
            } else {
                viewHolder.textLetter.setVisibility(View.GONE);
                viewHolder.editText.setVisibility(View.VISIBLE);

//                letterView.setVisibility(View.GONE);
//                editText.setVisibility(View.VISIBLE);
            }
        } else {
            gridView = view;
        }

//        final EditText[] editText = {gridView.findViewById(R.id.grid_edit_letter)};
//        final String[] edLetter = {""};
//        editText[0].addTextChangedListener(new TextWatcher() {
//
//            public void afterTextChanged(Editable s) {}
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                edLetter[0] = (String) s;
//            }
//        });

        return gridView;
    }

    private static class ViewHolder {
        public TextView textLetter;
        public EditText editText;
    }



}
