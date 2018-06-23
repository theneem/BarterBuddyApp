package com.theneem.barterbuddy;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

public class ImageListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final String[] itemdesc;
    private final String[] imgid;
    private final String[] itemRating;

    public ImageListAdapter(Activity context, String[] itemname,String[] itemdesc, String[] imgid, String[] itemRating) {
        super(context, R.layout.imagelist, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
        this.itemdesc = itemdesc;
        this.itemRating = itemRating;
    }

    public View getView(int position,View view,ViewGroup parent) {
        try {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.imagelist, null, true);

            TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);


            TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);
            TextView txRating = (TextView) rowView.findViewById(R.id.txtRating);


            txtTitle.setText(itemname[position]);
            //c.setImageResource(imgid[position]);

            Picasso.with(context).load(imgid[position]).into(imageView);



            //URL url = new URL(imgid[position]);
            //Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            //imageView.setImageBitmap(bmp);





            extratxt.setText(itemdesc[position]);
            txRating.setText("Rating : " + itemRating[position]);
            return rowView;
        }
        catch (Exception ex){}
        return null;
    };
}