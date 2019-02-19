package ru.pushapp.scan.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.navigation.Navigation;
import ru.pushapp.scan.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.rvAdapterHolder> {

    class rvAdapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView title;
        public TextView description;
        public TextView progressTv;
        public ProgressBar progressBar;
        public ImageView lockImage;

        CardView cardView;

        rvAdapterHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_lvl_card);
            description = itemView.findViewById(R.id.description_lvl);
            progressBar = itemView.findViewById(R.id.progress_bar);
            progressTv = itemView.findViewById(R.id.progress_tv);
            lockImage = itemView.findViewById(R.id.lock_image);

            cardView = itemView.findViewById(R.id.lvl_card);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //todo переход на экран с нужным уровнем
            if (list_items.get(getAdapterPosition()).getUnblocked()){
                Bundle bundle = new Bundle();
                bundle.putInt("id_scanword",getAdapterPosition());
                bundle.putString("file_name",list_items.get(getAdapterPosition()).getRes());

                Navigation.findNavController(view).navigate(R.id.action_levelFragment_to_gameFragment,bundle);
            } else {
                Toast.makeText(context, "Необходимо пройти предыдущие уровни", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Context context;
    private LayoutInflater inflater;
    private static ArrayList<LevelData> list_items;

    public RecyclerAdapter(Context context, ArrayList<LevelData> items) {
        this.context = context;
        this.list_items = items;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public rvAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.lvl_card, parent, false);
        return new rvAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final rvAdapterHolder holder, int position) {
        String title = list_items.get(position).getTitle();
        String description = list_items.get(position).getDescription();
        int progress = list_items.get(position).getProgress();
        String progressTV = progress + "%";

        holder.title.setText(title);
        holder.description.setText(description);
        holder.progressTv.setText(progressTV);
        holder.progressBar.setProgress(progress);

        if (list_items.get(position).getUnblocked()) {
            if (progress == 0){
                holder.lockImage.setVisibility(View.VISIBLE);
                holder.progressTv.setVisibility(View.GONE);
            }
        } else {
            holder.lockImage.setImageResource(R.drawable.ic_lock);
            holder.lockImage.setVisibility(View.VISIBLE);
            holder.progressTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list_items.size();
    }

}