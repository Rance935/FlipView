package com.rance.flipview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rance.flipview.bean.FamilyInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by think on 2017/6/2.
 */

public class FamilyAdapter extends BaseAdapter {
    private Context mContext;
    private List<FamilyInfo> familyInfos;

    public FamilyAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return familyInfos == null ? 0 : familyInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return familyInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setDate(List<FamilyInfo> familyInfos){
        this.familyInfos = familyInfos;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_family, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.itemFamilyName.setText(familyInfos.get(position).name);
        Glide.with(mContext)
                .load(familyInfos.get(position).header)
                .transform(new GlideRoundTransform(mContext, 10))
                .into(viewHolder.itemFamilyHeader);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.item_family_header)
        ImageView itemFamilyHeader;
        @BindView(R.id.item_family_name)
        TextView itemFamilyName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
