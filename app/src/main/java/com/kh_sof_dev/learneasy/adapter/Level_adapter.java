package com.kh_sof_dev.learneasy.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kh_sof_dev.learneasy.Activities.MainActivity;
import com.kh_sof_dev.learneasy.R;
import com.kh_sof_dev.learneasy.modul.Level;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 26/02/2020.
 */

public class Level_adapter extends RecyclerView.Adapter<Level_adapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    //vars
    private List<Level> mItems = new ArrayList<>();
    private Context mContext;
    private View mview;
    public boolean isOrder=false;

    public interface Selected_item{
        void Onselcted(Level level);
    }
    private  int item_select=-1;
    Selected_item listenner;
    public Level_adapter(Context context, List<Level> names, Selected_item listenner) {
        mItems = names;
        mContext = context;
        this.listenner=listenner;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { //parent = theme type

        View view;

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_row, parent, false);

        mview=view;
        return new ViewHolder(view); // Inflater means reading a layout XML
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
holder.name.setText(mItems.get(position).getName());
        Picasso.with(mContext)
                .load(mItems.get(position).getImg())
                .into(holder.img);

        if (MainActivity.My_level!=position && position!=0){
            holder.lock.setVisibility(View.VISIBLE);
        }else {
            holder.lock.setVisibility(View.GONE);
        }
        mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.lock.getVisibility()==View.GONE){
                    listenner.Onselcted(mItems.get(position));
                }else {
                    Toast.makeText(mContext,"You should complete previous level before...!"
                    ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

     ImageView img;
      TextView name;
      LinearLayout lock;


        public ViewHolder(View itemView) {
            super(itemView);
          name = itemView.findViewById(R.id.name);
            img = itemView.findViewById(R.id.img);
            lock = itemView.findViewById(R.id.lock);


        }
    }
}