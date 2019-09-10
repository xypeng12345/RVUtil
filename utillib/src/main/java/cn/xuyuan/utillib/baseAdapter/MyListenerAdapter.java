package cn.xuyuan.utillib.baseAdapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 项目名称：RecyclerViewUtil
 * 类描述：
 * 创建人：xyp
 * 创建时间：2017/5/31 16:06
 * 修改人：meyki-bear
 * 修改时间：2017/5/31 16:06
 * 修改备注：
 */

public abstract class MyListenerAdapter<VH extends MyListenerAdapter.ListenerViewHolder> extends MyHeaderFooterAdapter<MyListenerAdapter.ListenerViewHolder> {
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    protected OnItemViewClickListener onItemViewClickListener;

    public interface OnItemClickListener {
        void onItemClick ( int position );
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick ( int position );
    }

    public interface OnItemViewClickListener {
        void onItemViewClick ( View view , int position );
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @Override
    protected int getType(int position) {
        return 0;
    }

    @Override
    final protected void convert(ListenerViewHolder holder, int position, List<Object> payloads) {
        convertData((VH) holder, position, payloads);
    }

    /**
     * 复用布局
     *
     * @param holder
     * @param position
     * @param payloads
     */
    protected abstract void convertData(VH holder, int position, List<Object> payloads);

    protected void onItemClick(int position) {
        if (onItemClickListener == null) {
            return;
        }
        onItemClickListener.onItemClick(position);
    }

    protected boolean onItemLongClick(int position) {
        if (onItemLongClickListener == null) {
            return false;
        }
        return onItemLongClickListener.onItemLongClick(position);
    }

    protected boolean needsClickListener() {
        return onItemClickListener != null;
    }

    protected boolean needsLongClickListener() {
        return onItemLongClickListener != null;
    }

    @Override
    final protected ListenerViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        VH vh = onCreateListenerDataViewHolder(parent, viewType);
        registerClickListener(vh);
        return vh;
    }

    final protected void registerClickListener(final VH vh) {
        if (needsClickListener()) {
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(getRealPosition(vh.getLayoutPosition()));
                }
            });
        }
        if (needsLongClickListener()) {
            vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onItemLongClick(getRealPosition(vh.getLayoutPosition()));
                }
            });
        }
    }

    /**
     * 初始化布局
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract VH onCreateListenerDataViewHolder(ViewGroup parent, int viewType);

    public static class ListenerViewHolder extends MyHeaderFooterAdapter.HeaderAndFooterViewHolder {

        public ListenerViewHolder(View itemView) {
            super(itemView);
        }
    }


}
