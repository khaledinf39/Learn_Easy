package com.kh_sof_dev.learneasy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.kh_sof_dev.learneasy.R;
import com.kh_sof_dev.learneasy.modul.Course;
import com.kh_sof_dev.learneasy.modul.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 26/02/2020.
 */

public class Users_adapter extends RecyclerView.Adapter<Users_adapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    //vars
    private List<User> mItems = new ArrayList<>();
    private Context mContext;
    private View mview;

    public interface Selected_item{
        void Onselcted(Course course, int position);
        void OnPlay(Course course, int position);
        void OnDelete(Course course, int position);
    }
    private  int item_select=-1;
    Selected_item listenner;
    public Users_adapter(Context context, List<User> names, Selected_item listenner) {
        mItems = names;
        mContext = context;
        this.listenner=listenner;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { //parent = theme type

        View view;

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users, parent, false);

        mview=view;
        return new ViewHolder(view); // Inflater means reading a layout XML
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText(mItems.get(position).getFullName());
        holder.phone.setText(mItems.get(position).getPhone());
        holder.level.setText("My Level : "+mItems.get(position).getMy_level());

////add action when you click
        mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


      TextView name,phone,level;



        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            level = itemView.findViewById(R.id.level);



        }
    }
}