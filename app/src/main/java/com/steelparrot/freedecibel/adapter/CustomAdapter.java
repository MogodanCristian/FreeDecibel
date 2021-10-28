package com.steelparrot.freedecibel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.annotations.SerializedName;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.steelparrot.freedecibel.R;
import com.steelparrot.freedecibel.model.YTItem;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private List<YTItem> itemsList;
    private Context mContext;

    public CustomAdapter(Context context, List<YTItem> itemsList) {
        this.mContext = context;
        this.itemsList = itemsList;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        private ImageView thumbnailImage;
        private TextView txtTitle, txtUploader, txtViews, txtTimeUpload, txtDuration;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            thumbnailImage = mView.findViewById(R.id.thumbnailImage);
            txtTitle = mView.findViewById(R.id.title);
            txtUploader = mView.findViewById(R.id.uploader);
            txtViews = mView.findViewById(R.id.views);
            txtTimeUpload = mView.findViewById(R.id.time_upload);
            txtDuration = mView.findViewById(R.id.duration);
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.custom_row_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        YTItem currItem = itemsList.get(position);

        Picasso.Builder builder = new Picasso.Builder(mContext);
        builder.downloader(new OkHttp3Downloader(mContext));
        builder.build()
                .load(currItem.getM_thumbnail())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.thumbnailImage);

        holder.txtTitle.setText(currItem.getM_title());
        holder.txtUploader.setText(currItem.getM_uploader());
        holder.txtViews.setText(updateViews(currItem.getM_views().floatValue()));
        holder.txtTimeUpload.setText(currItem.getM_time_upload());
        holder.txtDuration.setText(currItem.getM_duration());
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public static String updateViews(Float views)
    {
        String ret=new String();
        if(views>1000 & views <1000000)
        {
            views/=1000;
            ret=String.format("%.02f",views)+"K";
        }
        else if(views>1000000 && views< 1000000000)
        {
            views/=1000000;
            ret=String.format("%.02f",views)+"M";
        }
        else if(views> 1000000000)
        {
            views/=1000000000;
            ret=String.format("%.02f",views)+"B";
        }
        else
        {
            ret=views.toString();
        }
        return ret;
    }
}
