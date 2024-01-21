package com.petpaw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.petpaw.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PetGridAdapter extends BaseAdapter {
    private Context context;
    private List<String> images;
    private LayoutInflater inflater;

    public PetGridAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }


    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_pet_grid, null);
        }

        ImageView imageView = convertView.findViewById(R.id.pet_grid_image);

        String imageUrl = images.get(position);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .tag(System.currentTimeMillis())
                    .into(imageView);
        }

        return convertView;
    }
}
