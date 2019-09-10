package cn.xuyuan.utillib.baseAdapter;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：RecyclerViewUtil
 * 类描述：多布局类型的adapter
 * 创建人：xyp
 * 创建时间：2017/6/1 10:12
 * 修改人：meyki-bear
 * 修改时间：2017/6/1 10:12
 * 修改备注：
 */

public abstract class MyGroupAdapter<VH extends MyListenerAdapter.ListenerViewHolder> extends MyListenerAdapter<VH> {

    private OnGroupClickListener onGroupClickListener;
    private OnChildClickListener onChildClickListener;

    public OnChildClickListener getOnChildClickListener() {
        return onChildClickListener;
    }

    public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
        this.onChildClickListener = onChildClickListener;
    }

    public ArrayList<Pair<Integer, Integer>> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Pair<Integer, Integer>> groups) {
        this.groups = groups;
    }

    public OnGroupClickListener getOnGroupClickListener() {
        return onGroupClickListener;
    }

    public void setOnGroupClickListener(OnGroupClickListener onGroupClickListener) {
        this.onGroupClickListener = onGroupClickListener;
    }

    /**
     * 获取这个position在recyclerView里的绝对位置
     *
     * @param groupPosition
     * @param position
     * @return
     */
    public int getItemRealPosition(int groupPosition, int position) {
        Pair<Integer, Integer> integerIntegerPair = groups.get(groupPosition);
        return getHeaderSize()+(integerIntegerPair.second+1 + position);
    }

    /**
     * 记录每个group以及这个group的第一个position在recyclerView里的绝对索引
     */
    private ArrayList<Pair<Integer, Integer>> groups;

    protected Pair<Integer, Integer> getGroupAndChildPosition(int position) {
        /**
         * 遍历每个groups的索引，如果当前position小于当前group第一个数据的绝对位置，那么这个
         * position就属于上一组,如果遍历完了都找不到那就能确定这个position属于最后一组了
         */
        for (int i = 0; i < groups.size(); i++) {
            Pair<Integer, Integer> integerIntegerPair = groups.get(i);
            if (position < integerIntegerPair.second) {
                return groups.get(i - 1);
            }
        }
        return groups.get(groups.size() - 1);
    }

    @Override
    final protected int getViewHolderSpanSize(int position, int spanCount) {
        Pair<Integer, Integer> pair = getGroupAndChildPosition(position);
        int groupListPosition = getGroupListPosition(pair, position);
        if (groupListPosition == 0) { //是每一组的标题位,传这组的位置,标题占满一行显示，而内容视情况而定
            return spanCount;
        } else {
            return getChildViewHolderSpanSize(pair.first, groupListPosition - 1, spanCount);
        }

    }

    @Override
    protected boolean needsClickListener() {
        return onChildClickListener != null || onGroupClickListener != null;
    }

    protected int getChildViewHolderSpanSize(int groupPosition, int childPosition, int spanCount) {
        return 1;
    }

    @NonNull
    @Override
    final protected VH onCreateListenerDataViewHolder(ViewGroup parent, int viewType) {
        return onCreateItemViewHolder(parent, viewType);
    }

    /**
     * 计算position在集合中的相对位置，注意，这个位置是包含了头布局的，所以数据的实际位置可能需要看情况减一
     * 如果是0，则是头布局，如果大于0则是list里的布局，这样的话就需要再对数据进行减1处理
     *
     * @param pair
     * @param position
     * @return
     */
    private int getGroupListPosition(Pair<Integer, Integer> pair, int position) {
        int listPosition;
        listPosition = position - pair.second;
        return listPosition;
    }

    @Override
    protected void convertData(VH holder, int position, List<Object> payloads) {
        Pair<Integer, Integer> pair = getGroupAndChildPosition(position);
        int groupListPosition = getGroupListPosition(pair, position);
        if (groupListPosition == 0) { //是每一组的标题位,传这组的位置
            converGroupData(holder, pair.first, payloads);
        } else {
            //需要减去标题的位置
            converChildData(holder, pair.first, groupListPosition - 1, payloads);
        }
    }

    @Override
    protected int getType(int position) {
        int type = 0;
        Pair<Integer, Integer> groupAndChildPosition = getGroupAndChildPosition(position);
        int groupListPosition = getGroupListPosition(groupAndChildPosition, position);
        if (groupListPosition == 0) { //是每一组的标题位,传这组的位置
            type = getGroupItemType(groupAndChildPosition.first);
        } else {
            //需要减去标题的位置
            type = getGroupChildItemType(groupAndChildPosition.first, groupListPosition - 1);
        }
        return type;
    }

    protected abstract int getGroupItemType(int groupPosition);

    protected abstract int getGroupChildItemType(int groupPosition, int childPosition);

    protected abstract VH onCreateItemViewHolder(ViewGroup parent, int viewType);

    protected abstract void converGroupData(VH holder, int groupPosition, List<Object> payloads);

    protected abstract void converChildData(VH holder, int groupPosition, int childPosition, List<Object> payloads);

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        super.setOnItemClickListener(onItemClickListener);
    }

    @Override
    final protected void onItemClick(int position) {
        Pair<Integer, Integer> pair = getGroupAndChildPosition(position);
        int groupListPosition = getGroupListPosition(pair, position);
        if (groupListPosition == 0) { //是每一组的标题位,传这组的位置
            if (onGroupClickListener != null) {
                onGroupClickListener.onGroupClickListener(pair.first);
            }
        } else {
            if (onChildClickListener != null) {
                onChildClickListener.onChildClickListener(pair.first, groupListPosition - 1);
            }
        }
    }

    @Override
    protected int getDataCount() {
        if (groups == null) {
            groups = new ArrayList<>();
        } else {
            groups.clear();
        }
        int count = 0;
        for (int groupPosition = 0; groupPosition < getGroupCount(); groupPosition++) {
            Pair<Integer, Integer> pair = new Pair<>(groupPosition, count);
            //每个group都有标题位，count++以示尊重
            count++;
            groups.add(pair);
            count += getChildrenCount(groupPosition);
        }
        return count;
    }

    protected abstract int getGroupCount();

    protected abstract int getChildrenCount(int groupPosition);

    public interface OnGroupClickListener {
        void onGroupClickListener ( int groupPosition );
    }

    public interface OnChildClickListener {
        void onChildClickListener ( int groupPosition , int childPosition );
    }
}
