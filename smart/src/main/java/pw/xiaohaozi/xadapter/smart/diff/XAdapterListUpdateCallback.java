package pw.xiaohaozi.xadapter.smart.diff;

/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter;

/**
 * ListUpdateCallback that dispatches update events to the given adapter.
 *
 * @see DiffUtil.DiffResult#dispatchUpdatesTo(RecyclerView.Adapter)
 */
public final class XAdapterListUpdateCallback implements ListUpdateCallback {
    @NonNull
    private final XAdapter mAdapter;

    /**
     * Creates an AdapterListUpdateCallback that will dispatch update events to the given adapter.
     *
     * @param adapter The Adapter to send updates to.
     */
    public XAdapterListUpdateCallback(@NonNull XAdapter adapter) {
        mAdapter = adapter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onInserted(int position, int count) {
        mAdapter.notifyItemRangeInserted(mAdapter.getAdapterPosition(position), count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRemoved(int position, int count) {
        mAdapter.notifyItemRangeRemoved(mAdapter.getAdapterPosition(position), count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMoved(int fromPosition, int toPosition) {
        mAdapter.notifyItemMoved(mAdapter.getAdapterPosition(fromPosition), mAdapter.getAdapterPosition(toPosition));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressLint("UnknownNullness") // b/240775049: Cannot annotate properly
    public void onChanged(int position, int count, Object payload) {
        mAdapter.notifyItemRangeChanged(mAdapter.getAdapterPosition(position), count, payload);
    }
}
