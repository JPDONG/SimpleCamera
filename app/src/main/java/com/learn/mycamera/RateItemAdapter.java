package com.learn.mycamera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongjiangpeng on 2016/8/23 0023.
 */
public class RateItemAdapter extends ArrayAdapter<RateItem> {
    private int resourceId;
    private List<RateItem> rateItemList = new ArrayList<>();

    public RateItemAdapter(Context context, int resource, List<RateItem> objects) {
        super(context, resource, objects);
        resourceId = resource;
        rateItemList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RateItem rateItem = rateItemList.get(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.rateImage = (ImageView) view.findViewById(R.id.iv_rate);
            viewHolder.rateText = (TextView) view.findViewById(R.id.tv_rate);
            viewHolder.rateRadioButton = (RadioButton) view.findViewById(R.id.radio_rate_select);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.rateImage.setImageResource(rateItem.getImageId());
        viewHolder.rateText.setText(rateItem.getRateName());
        if(MainActivity.selectPosition == position){
            viewHolder.rateRadioButton.setChecked(true);
        }
        else{
            viewHolder.rateRadioButton.setChecked(false);
        }
        return view;
    }

    class ViewHolder{
        private ImageView rateImage;
        private TextView rateText;
        private RadioButton rateRadioButton;
    }
}
