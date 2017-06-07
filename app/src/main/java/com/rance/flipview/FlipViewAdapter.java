package com.rance.flipview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.rance.flipview.bean.FamilyInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by think on 2017/6/2.
 */

public class FlipViewAdapter extends BaseAdapter {

    private static Context mContext;
    private List<List<FamilyInfo>> familyInfos;

    public FlipViewAdapter(Context mContext, List<List<FamilyInfo>> familyInfos) {
        this.mContext = mContext;
        this.familyInfos = familyInfos;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_family, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /**
         * 这里我们还要将每一页里面的数据分别分组到两个gridview里面
         * 这里有三种情况1：数据刚好一页8个，那就刚好左右两边一边4个
         *              2：数据大于4个小于8个，那左边4个，右边就是数据长度 - 左边4个
         *              3：数据小于4个，那左边就是数据长度，右边没有数据
         */
        List<FamilyInfo> familyInfosLeft;
        List<FamilyInfo> familyInfosRight;
        if (familyInfos.get(position).size() == 8) {
            familyInfosLeft = familyInfos.get(position).subList(0, 4);
            familyInfosRight = familyInfos.get(position).subList(4, 8);
        } else if (familyInfos.get(position).size() > 4 && familyInfos.get(position).size() < 8) {
            familyInfosLeft = familyInfos.get(position).subList(0, 4);
            familyInfosRight = familyInfos.get(position).subList(4, familyInfos.get(position).size());
        } else {
            familyInfosLeft = familyInfos.get(position).subList(0, familyInfos.get(position).size());
            familyInfosRight = null;
        }
        viewHolder.familyAdapterLeft.setDate(familyInfosLeft);
        viewHolder.gridViewLeft.setAdapter(viewHolder.familyAdapterLeft);
        viewHolder.familyAdapterRight.setDate(familyInfosRight);
        viewHolder.gridViewRight.setAdapter(viewHolder.familyAdapterRight);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.grid_view_left)
        GridView gridViewLeft;
        @BindView(R.id.grid_view_right)
        GridView gridViewRight;
        FamilyAdapter familyAdapterLeft;
        FamilyAdapter familyAdapterRight;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            familyAdapterLeft = new FamilyAdapter(mContext);
            familyAdapterRight = new FamilyAdapter(mContext);
        }
    }
}
