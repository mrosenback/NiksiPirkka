package com.example.shareyourbestadvice;

import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private List<Advice> adviceList;

    public RecyclerAdapter(List<Advice> adviceList) {
        this.adviceList = adviceList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView adviceText;
        private final TextView authorText;
        private final TextView categoryText;

        public MyViewHolder(final View view) {
            super(view);
            adviceText = view.findViewById(R.id.adviceText);
            authorText = view.findViewById(R.id.authorText);
            categoryText = view.findViewById(R.id.categoryText);
        }
    }

    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {
        String advice = adviceList.get(getItemCount() - position - 1).getAdvice();
        String author = adviceList.get(getItemCount() - position - 1).getAuthor();
        String category = adviceList.get(getItemCount() - position - 1).getCategory();

        holder.adviceText.setText(advice);
        holder.authorText.setText(author);
        holder.categoryText.setText(category);
    }

    @Override
    public int getItemCount() {
        return adviceList.size();
    }
}
