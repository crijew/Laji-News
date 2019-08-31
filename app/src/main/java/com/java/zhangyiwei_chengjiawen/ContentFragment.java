package com.java.zhangyiwei_chengjiawen;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;

abstract class BaseAdapter
        extends RecyclerView.Adapter<AddedAdapter.ViewHolder>
        implements DraggableItemAdapter<AddedAdapter.ViewHolder> {
    BaseAdapter oppose;

    static class ViewHolder extends AbstractDraggableItemViewHolder {
        TextView categoryText;
        View dragHandle;

        ViewHolder(View view) {
            super(view);
            categoryText = view.findViewById(R.id.categoryText);
            dragHandle = view.findViewById(R.id.drag_handle);
        }
    }

    BaseAdapter() {
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_item, viewGroup, false));
    }

    @Override
    public boolean onCheckCanStartDrag(ViewHolder holder, int position, int x, int y) {
        View dragHandle = holder.dragHandle;
        int handleWidth = dragHandle.getWidth();
        int handleHeight = dragHandle.getHeight();
        int handleLeft = dragHandle.getLeft();
        int handleTop = dragHandle.getTop();
        return (x >= handleLeft) && (x < handleLeft + handleWidth) &&
                (y >= handleTop) && (y < handleTop + handleHeight);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(ViewHolder holder, int position) {
        return null;
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return false;
    }

    @Override
    public void onItemDragStarted(int position) {

    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {

    }

    void setOppose(BaseAdapter oppose) {
        this.oppose = oppose;
    }
}

class AddedAdapter extends BaseAdapter {
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.categoryText.setText(Common.category[Common.added.get(i + 1)]);
        viewHolder.categoryText.setTextColor(Color.rgb(0, 0, 0));
        viewHolder.categoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                int deleted = Common.added.remove(position + 1);
                Common.deleted.add(0, deleted);
                notifyItemRemoved(position);
                oppose.notifyItemInserted(0);
            }
        });
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        int item = Common.added.remove(fromPosition + 1);
        Common.added.add(toPosition + 1, item);
    }

    @Override
    public int getItemCount() {
        return Common.added.size() - 1;
    }

    @Override
    public long getItemId(int position) {
        return Common.added.get(position + 1);
    }
}

class DeletedAdapter extends BaseAdapter {
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.categoryText.setText(Common.category[Common.deleted.get(i)]);
        viewHolder.categoryText.setTextColor(Color.rgb(0xB6, 0xB6, 0xB6));
        viewHolder.categoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                int added = Common.deleted.remove(position);
                Common.added.add(1, added);
                notifyItemRemoved(position);
                oppose.notifyItemInserted(0);
            }
        });
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        int item = Common.deleted.remove(fromPosition);
        Common.deleted.add(toPosition, item);
    }

    @Override
    public int getItemCount() {
        return Common.deleted.size();
    }

    @Override
    public long getItemId(int position) {
        return Common.deleted.get(position);
    }
}

public class ContentFragment extends Fragment {
    private BottomSheetDialog categoryDialog;
    ViewPager newsViewPager;
    private LinearLayout categoryMenu;
    private CategoryTextView[] categories = new CategoryTextView[Common.category.length];
    private ArrayList<NewsFragment> fragmentList = new ArrayList<>();
    BaseAdapter addedAdapter, deletedAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.content_fragment, container, false);

        //Choose category dialog
        {
            categoryDialog = new BottomSheetDialog(inflater.getContext(), R.style.dialog);
            categoryDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Common.currentItem = 0;
                    categoryMenu.removeAllViews();
                    for (int index : Common.added) {
                        categoryMenu.addView(categories[index]);
                    }
                    newsViewPager.removeAllViews();
                    newsViewPager.setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
                        @Override
                        public Fragment getItem(int i) {
                            return fragmentList.get(Common.added.get(i));
                        }

                        @Override
                        public int getCount() {
                            return Common.added.size();
                        }
                    });
                    newsViewPager.setCurrentItem(0);
                    categoryMenu.post(new Runnable() {
                        @Override
                        public void run() {
                            ((HorizontalScrollView) categoryMenu.getParent()).smoothScrollTo(0, 0);
                        }
                    });
                    ((MainActivity)getActivity()).saveData();
                }
            });
            categoryDialog.setCancelable(false);
            View chooseCategory = inflater.inflate(R.layout.choose_category, container, false);
            categoryDialog.setContentView(chooseCategory);
            chooseCategory.findViewById(R.id.chooseClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryDialog.dismiss();
                }
            });
            RecyclerView added = chooseCategory.findViewById(R.id.categoryAdded);
            added.setLayoutManager(new GridLayoutManager(getContext(), 4));
            RecyclerViewDragDropManager addedManager = new RecyclerViewDragDropManager();
            addedAdapter = new AddedAdapter();
            added.setAdapter(addedManager.createWrappedAdapter(addedAdapter));
            ((SimpleItemAnimator) added.getItemAnimator()).setSupportsChangeAnimations(false);
            addedManager.attachRecyclerView(added);

            RecyclerView deleted = chooseCategory.findViewById(R.id.categoryDeleted);
            deleted.setLayoutManager(new GridLayoutManager(getContext(), 4));
            RecyclerViewDragDropManager deletedManager = new RecyclerViewDragDropManager();
            deletedAdapter = new DeletedAdapter();
            deleted.setAdapter(deletedManager.createWrappedAdapter(deletedAdapter));
            ((SimpleItemAnimator) deleted.getItemAnimator()).setSupportsChangeAnimations(false);
            deletedManager.attachRecyclerView(deleted);

            addedAdapter.setOppose(deletedAdapter);
            deletedAdapter.setOppose(addedAdapter);
        }

        //Change category button
        view.findViewById(R.id.categoryChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog.show();
            }
        });

        //Add category menu
        for (int i = 0; i < Common.category.length; ++i)
            categories[i] = new CategoryTextView(getContext(), i);
        categoryMenu = view.findViewById(R.id.categoryMenu);
        for (int index : Common.added) {
            categoryMenu.addView(categories[index]);
        }

        //combine category menu and news ViewPager
        newsViewPager = view.findViewById(R.id.newsViewPaper);
        newsViewPager.setOffscreenPageLimit(11);
        newsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(final int toItem) {
                if (toItem == Common.currentItem) return;
                int fromItem = Common.currentItem;
                Common.currentItem = toItem;
                categoryMenu.getChildAt(fromItem).invalidate();
                final TextView scrollTo = (TextView) categoryMenu.getChildAt(toItem);
                scrollTo.invalidate();
                scrollTo.post(new Runnable() {
                    @Override
                    public void run() {
                        int width = scrollTo.getWidth();
                        ((HorizontalScrollView) view.findViewById(R.id.categoryMenuScrollView)).
                                smoothScrollTo(toItem * width + MainActivity.dpToPx(getContext(), 20 * toItem), 0);
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        //bind category to news ViewPager
        for (int i = 0; i < Common.category.length; ++i) {
            NewsFragment fragment = new NewsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("word", "");
            bundle.putString("type", Common.category[i]);
            fragment.setArguments(bundle);
            fragmentList.add(fragment);
        }
        newsViewPager.setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragmentList.get(Common.added.get(i));
            }

            @Override
            public int getCount() {
                return Common.added.size();
            }
        });
        return view;
    }
}
