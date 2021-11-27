package com.steelparrot.freedecibel.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.steelparrot.freedecibel.R;
import com.steelparrot.freedecibel.activities.DownloadLaterActivity;
import com.steelparrot.freedecibel.activities.YTItemActivity;
import com.steelparrot.freedecibel.database.DatabaseHelper;
import com.steelparrot.freedecibel.model.YTItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DownloadLaterAdapter extends RecyclerView.Adapter<DownloadLaterAdapter.CustomViewHolder> {

    public interface onMenuItemDataPass {
        public void onMenuItemsDeleted(ArrayList<Integer> positions);
    }

    onMenuItemDataPass mOnMenuItemDataPass;

    private ArrayList<YTItem> itemsList;
    private Context mContext;
    ArrayList<Integer> mPositions;
    private static boolean startDelete=false;
    /*private static Integer clickCounter=0;*/

    public DownloadLaterAdapter(Context context, ArrayList<YTItem> itemsList) {
        this.mContext = context;
        this.itemsList = itemsList;
        this.mOnMenuItemDataPass = (onMenuItemDataPass) context;
        this.mPositions=new ArrayList<>();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        private ImageView thumbnailImage;
        private TextView txtTitle, txtUploader, txtViews, txtTimeUpload, txtDuration;
        private LinearLayout mainLayout;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            thumbnailImage = mView.findViewById(R.id.thumbnailImage);
            txtTitle = mView.findViewById(R.id.title);
            txtUploader = mView.findViewById(R.id.uploader);
            txtViews = mView.findViewById(R.id.views);
            txtTimeUpload = mView.findViewById(R.id.time_upload);
            txtDuration = mView.findViewById(R.id.duration);
            mainLayout = mView.findViewById(R.id.mainLayout);
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
        holder.mainLayout.setBackgroundColor(currItem.isSelected() ? Color.GRAY : Color.TRANSPARENT);
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!startDelete) {
                      Intent intent = new Intent(mContext, YTItemActivity.class);
                      intent.putExtra("url", currItem.getM_url());
                      intent.putExtra("title", currItem.getM_title());
                      intent.putExtra("thumbnail", currItem.getM_thumbnail());
                      intent.putExtra("duration", currItem.getM_duration());
                      intent.putExtra("views", currItem.getM_views());
                      intent.putExtra("uploader", currItem.getM_uploader());
                      intent.putExtra("time_upload", currItem.getM_time_upload());
                      mContext.startActivity(intent);

                }
                else {
                    currItem.setSelected(!currItem.isSelected());
                    holder.mainLayout.setBackgroundColor(currItem.isSelected() ? Color.GRAY : Color.TRANSPARENT);
                    if (currItem.isSelected()) {
                        mPositions.add(position);
                    } else {
                        int index = mPositions.indexOf(position);
                        mPositions.remove(index);
                    }
                    if (mPositions.isEmpty()) {
                        DownloadLaterActivity.deleteOne.setVisible(false);
                        DownloadLaterActivity.isOnLongClick = false;
                        startDelete = false;
                        DownloadLaterActivity.selectionCounter.setVisible(false);
                    } else {
                        DownloadLaterActivity.selectionCounter.setTitle(mPositions.size() + " items selected");
                    }
                }
            }
        });
        holder.mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!startDelete) {
                    startDelete=true;
                    mPositions.add(position);
                    MenuItem item= DownloadLaterActivity.deleteOne;
                    DownloadLaterActivity.isOnLongClick=true;
                    DownloadLaterActivity.selectionCounter.setTitle("1 item selected");
                    DownloadLaterActivity.selectionCounter.setVisible(true);
                    holder.mainLayout.setBackgroundColor(Color.GRAY);
                    currItem.setSelected(true);
                    item.setVisible(true);
                    item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            mOnMenuItemDataPass.onMenuItemsDeleted(mPositions);
                            DownloadLaterActivity.selectionCounter.setVisible(false);
                            DownloadLaterActivity.deleteOne.setVisible(false);
                            DownloadLaterActivity.isOnLongClick=false;
                            startDelete=false;
                            mPositions.clear();
                            return true;
                        }
                    });
                }
                return true;
            }
        });


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

    public void setItemsList(ArrayList<YTItem> newItems)
    {
        this.itemsList=newItems;
    }
}
