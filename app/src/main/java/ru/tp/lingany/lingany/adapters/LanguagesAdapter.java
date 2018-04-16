package ru.tp.lingany.lingany.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.tp.lingany.lingany.R;
import ru.tp.lingany.lingany.sdk.languages.Language;


public class LanguagesAdapter extends RecyclerView.Adapter<LanguagesAdapter.LanguageViewHolder> {

    private List<Language> data;
    ItemClickListener itemClickListener;



    public LanguagesAdapter(List<Language> data, ItemClickListener listener) {
        this.data = data;
        this.itemClickListener = listener;
    }


    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lang, parent, false);
        return new LanguageViewHolder(itemView, itemClickListener);
    }

    @Override
    public void onBindViewHolder(LanguageViewHolder holder, int position) {
        Language lang = data.get(position);
        holder.title.setText(lang.getTitle());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface ItemClickListener {

        public void onClick(View view, int position);
    }

    public static class LanguageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;

        private ItemClickListener listener;

        LanguageViewHolder(View itemView, ItemClickListener listener){
            super(itemView);
            this.listener = listener;
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.lang_title);
        }

        @Override
        public void onClick(View view) {
            Log.i("tag", "onClick");
            listener.onClick(view, getAdapterPosition());
        }
    }
}