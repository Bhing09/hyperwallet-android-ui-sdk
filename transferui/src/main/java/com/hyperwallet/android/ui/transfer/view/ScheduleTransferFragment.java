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
package com.hyperwallet.android.ui.transfer.view;

import static com.hyperwallet.android.model.transfermethod.HyperwalletTransferMethod.TransferMethodFields.TRANSFER_METHOD_COUNTRY;
import static com.hyperwallet.android.model.transfermethod.HyperwalletTransferMethod.TransferMethodFields.TYPE;
import static com.hyperwallet.android.ui.common.view.TransferMethodUtils.getStringFontIcon;
import static com.hyperwallet.android.ui.common.view.TransferMethodUtils.getStringResourceByName;
import static com.hyperwallet.android.ui.common.view.TransferMethodUtils.getTransferMethodDetail;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyperwallet.android.model.transfer.ForeignExchange;
import com.hyperwallet.android.model.transfer.Transfer;
import com.hyperwallet.android.model.transfermethod.HyperwalletTransferMethod;
import com.hyperwallet.android.ui.common.view.OneClickListener;
import com.hyperwallet.android.ui.transfer.R;
import com.hyperwallet.android.ui.transfer.viewmodel.ScheduleTransferViewModel;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Schedule Transfer Fragment
 */
public class ScheduleTransferFragment extends Fragment {

    private static final String CURRENCY_NUMERIC_SEPARATOR = ",";
    private static final String EMPTY_STRING = "";

    private RecyclerView mForeignExchangeView;
    private ScheduleTransferViewModel mScheduleTransferViewModel;
    private Button mTransferConfirmButton;
    private View mTransferConfirmButtonProgress;

    public ScheduleTransferFragment() {
    }

    static ScheduleTransferFragment newInstance() {
        return new ScheduleTransferFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScheduleTransferViewModel = ViewModelProviders.of(requireActivity()).get(ScheduleTransferViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_transfer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mForeignExchangeView = view.findViewById(R.id.list_foreign_exchange);

        // transfer destination
        showTransferDestination(mScheduleTransferViewModel.getTransferDestination());

        // foreign exchange
        if (mScheduleTransferViewModel.getTransfer().getForeignExchanges() != null
                && !mScheduleTransferViewModel.getTransfer().getForeignExchanges().isEmpty()) {
            mForeignExchangeView.setNestedScrollingEnabled(false);
            mForeignExchangeView.setHasFixedSize(true);
            mForeignExchangeView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mForeignExchangeView.setAdapter(
                    new ForeignExchangeListAdapter(mScheduleTransferViewModel.getTransfer().getForeignExchanges()));
        } else {
            mForeignExchangeView.setVisibility(View.GONE);
        }

        // summary
        showSummary(mScheduleTransferViewModel.getTransfer());

        // notes
        showNotes(mScheduleTransferViewModel.getTransfer().getNotes());

        // transfer confirm button
        mTransferConfirmButtonProgress = view.findViewById(R.id.transfer_confirm_button_progress_bar);
        mTransferConfirmButton = view.findViewById(R.id.transfer_confirm_button);
        mTransferConfirmButton.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                mScheduleTransferViewModel.scheduleTransfer();
            }
        });

        registerObserver();
    }

    void retry() {
        mScheduleTransferViewModel.scheduleTransfer();
    }

    private void registerObserver() {
        mScheduleTransferViewModel.isScheduleTransferLoading().observe(getViewLifecycleOwner(),
                new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean loading) {
                        if (loading) {
                            mTransferConfirmButtonProgress.setVisibility(View.VISIBLE);
                        } else {
                            mTransferConfirmButtonProgress.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void showTransferDestination(@NonNull final HyperwalletTransferMethod transferMethod) {
        TextView transferIcon = getView().findViewById(R.id.transfer_destination_icon);
        TextView transferTitle = getView().findViewById(R.id.transfer_destination_title);
        TextView transferCountry = getView().findViewById(R.id.transfer_destination_description_1);
        TextView transferIdentifier = getView().findViewById(R.id.transfer_destination_description_2);

        String type = transferMethod.getField(TYPE);
        String transferMethodIdentification = getTransferMethodDetail(transferIdentifier.getContext(), transferMethod,
                type);
        Locale locale = new Locale.Builder().setRegion(
                transferMethod.getField(TRANSFER_METHOD_COUNTRY)).build();

        transferIdentifier.setText(transferMethodIdentification);
        transferTitle.setText(getStringResourceByName(transferTitle.getContext(), type));
        transferIcon.setText(getStringFontIcon(transferIcon.getContext(), type));
        transferCountry.setText(locale.getDisplayName());
    }

    private void showSummary(@NonNull final Transfer transfer) {
        TextView amount = getView().findViewById(R.id.amount_value);
        TextView fee = getView().findViewById(R.id.fee_value);
        TextView receiveAmount = getView().findViewById(R.id.transfer_value);
        View feeContainer = getView().findViewById(R.id.fee_container);
        View receiveAmountContainer = getView().findViewById(R.id.transfer_container);
        View amountHorizontalBar = getView().findViewById(R.id.amount_horizontal_bar);

        if (isFeeAvailable(transfer.getDestinationFeeAmount())) {
            feeContainer.setVisibility(View.VISIBLE);
            receiveAmountContainer.setVisibility(View.VISIBLE);
            amountHorizontalBar.setVisibility(View.VISIBLE);
            fee.setText(requireContext().getString(R.string.amount_currency_format,
                    transfer.getDestinationFeeAmount(), transfer.getDestinationCurrency()));

            //TODO this is temporarily adding fee + amount but this is not final we will wait on
            // Platform to add new field
            amount.setText(requireContext().getString(R.string.amount_currency_format,
                    getTotalAmount(transfer.getDestinationAmount(), transfer.getDestinationFeeAmount()),
                    transfer.getDestinationCurrency()));
            receiveAmount.setText(requireContext().getString(R.string.amount_currency_format,
                    transfer.getDestinationAmount(), transfer.getDestinationCurrency()));
        } else {
            feeContainer.setVisibility(View.GONE);
            receiveAmountContainer.setVisibility(View.GONE);
            amountHorizontalBar.setVisibility(View.GONE);
            amount.setText(requireContext().getString(R.string.amount_currency_format,
                    transfer.getDestinationAmount(), transfer.getDestinationCurrency()));
        }
    }

    private boolean isFeeAvailable(@Nullable final String feeAmount) {
        if (!TextUtils.isEmpty(feeAmount)) {
            BigDecimal fee = new BigDecimal(feeAmount.replace(CURRENCY_NUMERIC_SEPARATOR, EMPTY_STRING));
            return fee.doubleValue() != 0;
        }
        return false;
    }

    // TODO this will be removed when Platform can return the total amount field
    private String getTotalAmount(@NonNull final String amount, @NonNull final String fee) {
        // normalize
        BigDecimal normalizedAmount = new BigDecimal(amount.replace(CURRENCY_NUMERIC_SEPARATOR, EMPTY_STRING));
        BigDecimal normalizedFee = new BigDecimal(fee.replace(CURRENCY_NUMERIC_SEPARATOR, EMPTY_STRING));

        BigDecimal totalAmount = normalizedAmount.add(normalizedFee);

        // format in normal locale US this will change when we have formatting enabled
        return NumberFormat.getInstance(Locale.US).format(totalAmount);
    }

    private void showNotes(@Nullable final String notes) {
        View notesContainer = getView().findViewById(R.id.notes_container);
        TextView notesView = getView().findViewById(R.id.notes_value);

        if (TextUtils.isEmpty(notes)) {
            notesContainer.setVisibility(View.GONE);
        } else {
            notesContainer.setVisibility(View.VISIBLE);
            notesView.setText(notes);
        }
    }

    private static class ForeignExchangeListAdapter extends
            RecyclerView.Adapter<ForeignExchangeListAdapter.ViewHolder> {

        private static short DATA_WITH_HEADER = 0;
        private static short DATA_WITHOUT_HEADER = 1;

        private List<ForeignExchange> foreignExchanges;

        ForeignExchangeListAdapter(@NonNull final List<ForeignExchange> foreignExchanges) {
            this.foreignExchanges = foreignExchanges;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layout = LayoutInflater.from(parent.getContext());

            if (viewType == DATA_WITH_HEADER) {
                View viewDataWithHeader = layout.inflate(R.layout.item_foreign_exchange_with_header, parent, false);
                return new ViewHolder(viewDataWithHeader);
            }

            View viewDataOnly = layout.inflate(R.layout.item_foreign_exchange, parent, false);
            return new ViewHolder(viewDataOnly);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final ForeignExchange foreignExchange = foreignExchanges.get(position);
            if (foreignExchange != null) { // make sure its not view holder placeholder
                holder.bind(foreignExchange);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return DATA_WITH_HEADER;
            }
            return DATA_WITHOUT_HEADER;
        }

        @Override
        public int getItemCount() {
            return foreignExchanges.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ViewHolder(@NonNull final View itemView) {
                super(itemView);
            }

            void bind(@NonNull final ForeignExchange fx) {
                TextView sellValue = itemView.findViewById(R.id.sell_value);
                TextView buyValue = itemView.findViewById(R.id.buy_value);
                TextView exchangeRateValue = itemView.findViewById(R.id.exchange_rate_value);

                sellValue.setText(itemView.getContext().getString(R.string.amount_currency_format,
                        fx.getSourceAmount(), fx.getSourceCurrency()));
                buyValue.setText(itemView.getContext().getString(R.string.amount_currency_format,
                        fx.getDestinationAmount(), fx.getDestinationCurrency()));
                exchangeRateValue.setText(itemView.getContext().getString(R.string.exchange_rate_format,
                        fx.getSourceCurrency(), fx.getRate(), fx.getDestinationCurrency()));
            }
        }
    }
}