package com.example.proj1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.mypackage.R;

public class ViewPagerAdapter extends PagerAdapter {


    Context context;

    int images[] = {
            R.drawable.ob1,
            R.drawable.ob2,
            R.drawable.ob3,
            R.drawable.productlogo_old,
    };

    int headings[] = {
            R.string.obtitle1,
            R.string.obtitle2,
            R.string.obtitle3,
            R.string.obtitle4
    };

    int description[] = {
            R.string.description1,
            R.string.description2,
            R.string.description3,
            R.string.description4
    };

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)    {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout, container, false);

        ImageView slidetitleimage = (ImageView) view.findViewById(R.id.titleImage);
        TextView slideHeading = (TextView) view.findViewById(R.id.textTitle);
        TextView slideDescription = (TextView) view.findViewById(R.id.textDescription);

        slidetitleimage.setImageResource(images[position]);
        slideHeading.setText(headings[position]);
        slideDescription.setText(description[position]);

        container.addView(view);
        return view;
        //What it does here is it changes the image/title and description based on the array stated above to fill the page
        //Everything will then be added to the container view
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }

}
