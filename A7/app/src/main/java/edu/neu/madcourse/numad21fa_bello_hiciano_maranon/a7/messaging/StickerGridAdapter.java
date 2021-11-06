package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.R;

public class StickerGridAdapter extends BaseAdapter {
    private String TAG = "StickerGridAdapter";

    private Context context;
    private ArrayList<Sticker> stickerList;
    private LayoutInflater layoutInflater;
    private ImageView gridImage;

    public StickerGridAdapter(Context context, ArrayList<Sticker> stickerList) {
        this.context = context;
        this.stickerList = stickerList;
    }


    @Override
    public int getCount() {
        return this.stickerList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.stickerList.get(i);
    }

    @Override
    public long getItemId(int i) {
        // TODO
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.v(TAG, "Getting view: " + i);
        Log.v(TAG, "List length: " + this.getCount());
        layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            view = layoutInflater.inflate(R.layout.sticker_layout, viewGroup, false);
        }
        Sticker currSticker = stickerList.get(i);
        int id = context.getResources().getIdentifier(currSticker.getLocation(), "drawable",
                context.getPackageName());

        gridImage = view.findViewById(R.id.stickerGridLayout);
        gridImage.setImageResource(id);

        return view;
    }
}
