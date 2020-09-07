package com.kh_sof_dev.learneasy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.kh_sof_dev.learneasy.R;
import com.kh_sof_dev.learneasy.modul.Course;
import com.kh_sof_dev.learneasy.modul.Level;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 26/02/2020.
 */

public class Course_adapter extends RecyclerView.Adapter<Course_adapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    //vars
    private List<Course> mItems = new ArrayList<>();
    private Context mContext;
    private View mview;

    public interface Selected_item{
        void Onselcted(Course course,int position);
        void OnPlay(Course course,int position);
        void OnDelete(Course course,int position);
    }
    private  int item_select=-1;
    Selected_item listenner;
    public Course_adapter(Context context, List<Course> names, Selected_item listenner) {
        mItems = names;
        mContext = context;
        this.listenner=listenner;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { //parent = theme type

        View view;

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course, parent, false);

        mview=view;
        return new ViewHolder(view); // Inflater means reading a layout XML
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
holder.name.setText(mItems.get(position).getName());
        Picasso.with(mContext)
                .load(mItems.get(position).getImage())
                .placeholder(R.drawable.ic_logo)
                .into(holder.img);

////add action when you click
        mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenner.Onselcted(mItems.get(position),position);
            }
        });

        holder.sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenner.OnPlay(mItems.get(position),position);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenner.OnDelete(mItems.get(position),position);
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
      ImageButton delete,sound;


        public ViewHolder(View itemView) {
            super(itemView);
          name = itemView.findViewById(R.id.name);
            img = itemView.findViewById(R.id.img);
            delete = itemView.findViewById(R.id.delete);
            sound = itemView.findViewById(R.id.sound);


        }
    }
}