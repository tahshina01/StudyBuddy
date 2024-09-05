package com.example.frenbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    int images[] = {
            R.raw.academic,
            R.raw.event,
            R.raw.notice,
            R.raw.sos
    };
    int descs[]={
            R.string.des_academic,
            R.string.des_event,
            R.string.des_finance,
            R.string.des_sos
    };
    int headers[]={
            R.string.academic,
            R.string.event,
            R.string.finance,
            R.string.sos

    };


    @Override
    public int getCount() {
        return headers.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view= layoutInflater.inflate(R.layout.slides,container,false);

        LottieAnimationView imageView =view.findViewById(R.id.slider_image);
        TextView heading =view.findViewById(R.id.slider_title);
        TextView desc =view.findViewById(R.id.slider_desc);

        imageView.setAnimation(images[position]);
        imageView.playAnimation();
        heading.setText(headers[position]);
        desc.setText(descs[position]);

        container.addView(view);

        return view;
    }
}
