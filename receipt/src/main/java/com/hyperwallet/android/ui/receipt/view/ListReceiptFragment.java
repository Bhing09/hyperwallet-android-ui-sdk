/*
 * The MIT License (MIT)
 * Copyright (c) 2019 Hyperwallet Systems Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hyperwallet.android.ui.receipt.view;

import static android.text.format.DateUtils.FORMAT_NO_MONTH_DAY;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_YEAR;
import static android.text.format.DateUtils.formatDateTime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyperwallet.android.model.receipt.Receipt;
import com.hyperwallet.android.ui.common.util.DateUtils;
import com.hyperwallet.android.ui.common.view.OneClickListener;
import com.hyperwallet.android.ui.receipt.R;
import com.hyperwallet.android.ui.receipt.viewmodel.ListReceiptViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ListReceiptFragment extends Fragment {

    private ListReceiptAdapter mListReceiptAdapter;
    private RecyclerView mListReceiptsView;
    private ListReceiptViewModel mListReceiptViewModel;
    private View mProgressBar;

    /**
     * Please don't use this constructor this is reserved for Android Core Framework
     *
     * @see {@link ListReceiptFragment#newInstance()} instead.
     */
    public ListReceiptFragment() {
        setRetainInstance(true);
    }

    static ListReceiptFragment newInstance() {
        ListReceiptFragment fragment = new ListReceiptFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListReceiptViewModel = ViewModelProviders.of(requireActivity()).get(
                ListReceiptViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_receipt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = view.findViewById(R.id.list_receipt_progress_bar);
        mListReceiptAdapter = new ListReceiptAdapter(mListReceiptViewModel, new ListReceiptItemDiffCallback());
        mListReceiptsView = view.findViewById(R.id.list_receipts);
        mListReceiptsView.setHasFixedSize(true);
        mListReceiptsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListReceiptsView.addItemDecoration(new ReceiptItemDividerDecorator(requireContext(), false));
        mListReceiptsView.setAdapter(mListReceiptAdapter);
        registerObservers();
    }

    private void registerObservers() {
        mListReceiptViewModel.getReceiptList().observe(getViewLifecycleOwner(), new Observer<PagedList<Receipt>>() {
            @Override
            public void onChanged(PagedList<Receipt> receipts) {
                mListReceiptAdapter.submitList(receipts);
            }
        });

        mListReceiptViewModel.isLoadingData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                if (loading) {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    void retry() {
        mListReceiptViewModel.retryLoadReceipts();
    }

    private static class ListReceiptItemDiffCallback extends DiffUtil.ItemCallback<Receipt> {

        @Override
        public boolean areItemsTheSame(@NonNull final Receipt oldItem, @NonNull final Receipt newItem) {
            return oldItem.hashCode() == newItem.hashCode()
                    && Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull final Receipt oldItem, @NonNull final Receipt newItem) {
            return oldItem.hashCode() == newItem.hashCode()
                    && Objects.equals(oldItem, newItem);
        }
    }

    private static class ListReceiptAdapter
            extends PagedListAdapter<Receipt, ListReceiptAdapter.ReceiptViewHolder> {

        private static final String HEADER_DATE_FORMAT = "MMMM yyyy";
        private static final int HEADER_VIEW_TYPE = 1;
        private static final int DATA_VIEW_TYPE = 0;
        private final ListReceiptViewModel mReceiptViewModel;

        ListReceiptAdapter(@NonNull final ListReceiptViewModel receiptViewModel,
                @NonNull final DiffUtil.ItemCallback<Receipt> diffCallback) {
            super(diffCallback);
            mReceiptViewModel = receiptViewModel;
        }

        @Override
        public int getItemViewType(final int position) {
            if (position != 0) {
                Receipt previous = getItem(position - 1);
                Receipt current = getItem(position);
                if (isDataViewType(previous, current)) {
                    return DATA_VIEW_TYPE;
                }
            }
            return HEADER_VIEW_TYPE;
        }

        boolean isDataViewType(@NonNull final Receipt previous, @NonNull final Receipt current) {
            Calendar prev = Calendar.getInstance();
            prev.setTime(DateUtils.fromDateTimeString(previous.getCreatedOn()));
            Calendar curr = Calendar.getInstance();
            curr.setTime(DateUtils.fromDateTimeString(current.getCreatedOn()));

            return prev.get(Calendar.MONTH) == curr.get(Calendar.MONTH)
                    && prev.get(Calendar.YEAR) == curr.get(Calendar.YEAR);
        }

        @NonNull
        @Override
        public ReceiptViewHolder onCreateViewHolder(final @NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater layout = LayoutInflater.from(viewGroup.getContext());

            if (viewType == HEADER_VIEW_TYPE) {
                View headerView = layout.inflate(R.layout.item_receipt_with_header, viewGroup, false);
                return new ReceiptViewHolderWithHeader(mReceiptViewModel, headerView);
            }
            View dataView = layout.inflate(R.layout.item_receipt, viewGroup, false);
            return new ReceiptViewHolder(mReceiptViewModel, dataView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ReceiptViewHolder holder, final int position) {
            final Receipt receipt = getItem(position);
            if (receipt != null) {
                holder.bind(receipt);
            }
        }

        class ReceiptViewHolder extends RecyclerView.ViewHolder {

            private ListReceiptViewModel mListReceiptViewModel;
            private View mView;

            ReceiptViewHolder(@NonNull final ListReceiptViewModel receiptViewModel,
                    @NonNull final View item) {
                super(item);
                mView = item.findViewById(R.id.receipt_item);
                mListReceiptViewModel = receiptViewModel;
            }

            void bind(@NonNull final Receipt receipt) {
                ReceiptViewUtil util = new ReceiptViewUtil();
                util.setTransactionView(receipt, itemView);
                mView.setOnClickListener(new OneClickListener() {
                    @Override
                    public void onOneClick(View v) {
                        mListReceiptViewModel.setDetailNavigation(receipt);
                    }
                });
            }
        }

        class ReceiptViewHolderWithHeader extends ReceiptViewHolder {

            private final TextView mTransactionHeaderText;

            ReceiptViewHolderWithHeader(@NonNull final ListReceiptViewModel receiptViewModel,
                    @NonNull final View item) {
                super(receiptViewModel, item);
                mTransactionHeaderText = item.findViewById(R.id.item_date_header_title);
            }

            @Override
            void bind(@NonNull final Receipt receipt) {
                super.bind(receipt);
                Date date = DateUtils.fromDateTimeString(receipt.getCreatedOn());
                mTransactionHeaderText.setText(formatDateTime(mTransactionHeaderText.getContext(), date.getTime(),
                        FORMAT_SHOW_DATE | FORMAT_SHOW_YEAR | FORMAT_NO_MONTH_DAY));
            }
        }
    }
}
