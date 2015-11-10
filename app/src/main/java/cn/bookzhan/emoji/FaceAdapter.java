package cn.bookzhan.emoji;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import cn.bookzhan.library.R;
import cn.bookzhan.utils.DisplayUtil;

/**
 *表情填充器
  */
public class FaceAdapter extends BaseAdapter {

    private final Context context;
    private List<ChatEmoji> data;

    private LayoutInflater inflater;

    private int size=0;

    public FaceAdapter(Context context, List<ChatEmoji> list) {
        this.inflater= LayoutInflater.from(context);
        this.data=list;
        this.size=list.size();
        this.context=context;
    }

    @Override
    public int getCount() {
        return this.size;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatEmoji emoji=data.get(position);
        ViewHolder viewHolder=null;
        if(convertView == null) {
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.item_face, null);
            viewHolder.iv_face=(ImageView)convertView.findViewById(R.id.item_iv_face);
            convertView.setTag(viewHolder);
        } else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        if(emoji.getId() == R.drawable.selector_face_del_icon) {
            viewHolder.iv_face.setImageResource(emoji.getId());
            viewHolder.iv_face.setBackgroundResource(0);
        } else if(TextUtils.isEmpty(emoji.getCharacter())) {
            viewHolder.iv_face.setBackgroundResource(0);
            viewHolder.iv_face.setImageResource(0);
        } else {
            viewHolder.iv_face.setTag(emoji);
            viewHolder.iv_face.setImageResource(emoji.getId());
        }
        if(position==data.size()-1){
            convertView.setPadding(0,0, DisplayUtil.dip2px(context, 5),0);
        }

        return convertView;
    }

    class ViewHolder {
        public ImageView iv_face;
    }
}