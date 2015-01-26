package com.yahoo.liyli.instagramviewer;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by liyli on 1/25/15.
 */
public class InstagramPhotoAdapter extends ArrayAdapter<InstagramPhoto> {
    public InstagramPhotoAdapter (Context context, List<InstagramPhoto> photos){
        super(context, R.layout.item_photo, photos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstagramPhoto photo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
        }

        TextView tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
        ImageView imgPhoto = (ImageView) convertView.findViewById(R.id.imgPhoto);

        // add both username and caption into one single caption with the Html.
        // I know it's a bit hacky...
        tvCaption.setText(Html.fromHtml("<b>"+photo.username +"</b>: "+photo.caption));

        imgPhoto.getLayoutParams().height = photo.imageHeight;
        imgPhoto.setImageResource(0);
        Picasso.with(getContext()).load(photo.imageUrl).into(imgPhoto);

        ImageView userPhoto = (ImageView) convertView.findViewById(R.id.ivUserPhoto);

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.WHITE)
                .borderWidthDp(1)
                .cornerRadiusDp(30)
                .oval(false)
                .build();

        // add a placeholder image to the size doesn't seem right...
        //Picasso.with(getContext()).load(photo.userPhotoUrl).placeholder(getContext().getResources().getDrawable(R.drawable.marissa_150_150)).into(userPhoto);
        Picasso.with(getContext()).load(photo.userPhotoUrl).transform(transformation).into(userPhoto);

        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        tvUserName.setText(photo.username);

        TextView tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
        String likeStr = photo.likesCount + " likes";
        tvLikes.setText(likeStr);

        return convertView;
    }
}
