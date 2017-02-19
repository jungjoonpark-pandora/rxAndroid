package com.pandora.rxandroid.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pandora.rxandroid.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.subjects.PublishSubject;

class RecyclerViewAdapter extends RecyclerView.Adapter {
    private static final String TAG = RecyclerViewAdapter.class.getSimpleName();

    private List<RecyclerItem> mItems = new ArrayList<>();
    private PublishSubject<RecyclerItem> mPublishSubject;

    RecyclerViewAdapter() {
        this.mPublishSubject = PublishSubject.create();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SimpleViewHolder) {
            final RecyclerItem item = mItems.get(position);
            ((SimpleViewHolder) holder).update(item);
            ((SimpleViewHolder) holder).getClickObserver().subscribe(mPublishSubject);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void updateItems(List<RecyclerItem> items) {
        mItems.addAll(items);
    }

    public void updateItems(RecyclerItem item) {
        mItems.add(item);
    }


    public PublishSubject<RecyclerItem> getItemPublishSubject() {
        return mPublishSubject;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_image) ImageView mImage;
        @BindView(R.id.item_title) TextView mTitle;

        RecyclerItem mRecyclerItem;

//        private final Observable<RecyclerItem> mClickObserver = Observable.create(e ->
//                itemView.setOnClickListener(view -> e.onNext(mRecyclerItem))
//        );

        private final Observable<RecyclerItem> mClickObserver = Observable.create(new ObservableOnSubscribe<RecyclerItem>() {
            @Override
            public void subscribe(ObservableEmitter<RecyclerItem> e) throws Exception {
                itemView.setOnClickListener(view -> {
                    e.onNext(mRecyclerItem);
                });
            }
        });

        private SimpleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public Observable<RecyclerItem> getClickObserver() {
            return mClickObserver;
        }

        public void update(RecyclerItem item) {
            mRecyclerItem = item;
            mImage.setImageDrawable(item.getImage());
            mTitle.setText(item.getTitle());
        }
    }
}


