package com.java.zhangyiwei_chengjiawen;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
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
    Context context;

    static class ViewHolder extends AbstractDraggableItemViewHolder {
        TextView categoryText;
        View dragHandle;

        ViewHolder(View view) {
            super(view);
            categoryText = view.findViewById(R.id.categoryText);
            dragHandle = view.findViewById(R.id.drag_handle);
        }
    }

    BaseAdapter(Context context) {
        setHasStableIds(true);
        this.context = context;
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
    public AddedAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.categoryText.setText(Common.category[Common.added.get(i + 1)]);
        viewHolder.categoryText.setTextColor(ContextCompat.getColor(context, R.color.categoryAdded));
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
    public DeletedAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.categoryText.setText(Common.category[Common.deleted.get(i)]);
        viewHolder.categoryText.setTextColor(ContextCompat.getColor(context, R.color.categoryDeleted));
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

class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<NewsFragment> fragmentList = new ArrayList<>();

    ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        for (int index : Common.added) {
            NewsFragment fragment = new NewsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("word", "");
            bundle.putString("type", Common.category[index]);
            fragment.setArguments(bundle);
            fragmentList.add(fragment);
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}

public class MainFragment extends Fragment {
    private View rootView;
    private ViewPager newsViewPager;
    private BottomSheetDialog categoryDialog;
    private LinearLayout categoryMenu;
    private CategoryTextView[] categories = new CategoryTextView[Common.category.length];
    private EditText searchText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_fragment, container, false);

        //Search box
        searchText = rootView.findViewById(R.id.searchText);
        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    startActivity(new Intent(getContext(), SearchActivity.class));
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    searchText.clearFocus();
                }
            }
        });

        rootView.findViewById(R.id.searchIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText.requestFocus();
            }
        });

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
                    newsViewPager.setAdapter(new ViewPagerAdapter(getFragmentManager()));
                    newsViewPager.setCurrentItem(0);
                    categoryMenu.post(new Runnable() {
                        @Override
                        public void run() {
                            ((HorizontalScrollView) categoryMenu.getParent()).smoothScrollTo(0, 0);
                        }
                    });
                    Common.saveData(getContext());
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
            BaseAdapter addedAdapter = new AddedAdapter(getContext());
            added.setAdapter(addedManager.createWrappedAdapter(addedAdapter));
            ((SimpleItemAnimator) added.getItemAnimator()).setSupportsChangeAnimations(false);
            addedManager.attachRecyclerView(added);

            RecyclerView deleted = chooseCategory.findViewById(R.id.categoryDeleted);
            deleted.setLayoutManager(new GridLayoutManager(getContext(), 4));
            RecyclerViewDragDropManager deletedManager = new RecyclerViewDragDropManager();
            BaseAdapter deletedAdapter = new DeletedAdapter(getContext());
            deleted.setAdapter(deletedManager.createWrappedAdapter(deletedAdapter));
            ((SimpleItemAnimator) deleted.getItemAnimator()).setSupportsChangeAnimations(false);
            deletedManager.attachRecyclerView(deleted);

            addedAdapter.setOppose(deletedAdapter);
            deletedAdapter.setOppose(addedAdapter);
        }

        //Change category button
        rootView.findViewById(R.id.categoryChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog.show();
            }
        });

        //Add category menu
        for (int i = 0; i < Common.category.length; ++i)
            categories[i] = new CategoryTextView(getContext(), i);
        categoryMenu = rootView.findViewById(R.id.categoryMenu);
        for (int index : Common.added) {
            categoryMenu.addView(categories[index]);
        }

        //combine category menu and news ViewPager
        newsViewPager = rootView.findViewById(R.id.newsViewPaper);
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
                        ((HorizontalScrollView) rootView.findViewById(R.id.categoryMenuScrollView)).
                                smoothScrollTo(toItem * width + MainActivity.dpToPx(getContext(), 20 * toItem), 0);
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        newsViewPager.setCurrentItem(Common.currentItem);
        newsViewPager.setAdapter(new ViewPagerAdapter(getFragmentManager()));
        return rootView;
    }
}
