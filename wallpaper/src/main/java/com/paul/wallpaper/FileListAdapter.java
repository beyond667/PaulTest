package com.paul.wallpaper;

import static com.paul.wallpaper.WallUtils.DEFAULT_NAME;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    private final String TAG = "FileListAdapter";
    private List<String> mFileList = null;
    private OnItemClickListener mOnItemClickListener = null;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView fileNameView;
        ImageView ivSelect;

        public ViewHolder(View view) {
            super(view);
            fileNameView = (TextView) view.findViewById(R.id.tv_name);
            ivSelect = (ImageView) view.findViewById(R.id.iv_select);
        }
    }

    public FileListAdapter(List<String> fileList) {
        this.mFileList = fileList;
    }

    //加载item 的布局 创建ViewHolder实例
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_item,parent,false);//加载view布局文件
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //对RecyclerView子项数据进行赋值
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(null == holder)
        {
            return;
        }
        final String fileName= mFileList.get(position);
        holder.fileNameView.setText(fileName);
        holder.ivSelect.setVisibility(fileName.equals(WallUtils.selectPath)||(fileName.equals(DEFAULT_NAME)&& TextUtils.isEmpty(WallUtils.selectPath))?View.VISIBLE:View.GONE);

        final int tempPosition = position;

        if(null != mOnItemClickListener)
        {
            holder.fileNameView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(true);
                    mOnItemClickListener.onClickItem(tempPosition,fileName);
                }
            });
        }
    }

    //返回子项个数
    @Override
    public int getItemCount() {
        return mFileList.size();
    }


    public interface OnItemClickListener{
        void onClickItem( int position,String fileName);
//    void onLongClickItem( int position,String fileName);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this.mOnItemClickListener = onItemClickListener;
    }
}