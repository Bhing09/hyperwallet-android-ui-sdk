package com.hyperwallet.android.ui.receipt.repository;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.hyperwallet.android.model.HyperwalletErrors;
import com.hyperwallet.android.model.receipt.Receipt;
import com.hyperwallet.android.ui.common.repository.Event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class PrepaidCardReceiptRepositoryImplTest {


    private PrepaidCardReceiptRepository mPrepaidCardReceiptRepository = new PrepaidCardReceiptRepositoryImpl(
            "trm-aa308d58-75b4-432b-dec1-eb6b9e341111");

    @Test
    public void testLoadPrepaidCardReceipts_returnsLiveData() {
        LiveData<PagedList<Receipt>> result = mPrepaidCardReceiptRepository.loadPrepaidCardReceipts();
        assertThat(result, is(notNullValue()));
        LiveData<PagedList<Receipt>> result2 = mPrepaidCardReceiptRepository.loadPrepaidCardReceipts();
        assertTrue(result == result2);
    }

    @Test
    public void testLoadPrepaidCardReceipts_liveDataSingleInstantiation() {
        LiveData<PagedList<Receipt>> result = mPrepaidCardReceiptRepository.loadPrepaidCardReceipts();
        LiveData<PagedList<Receipt>> result2 = mPrepaidCardReceiptRepository.loadPrepaidCardReceipts();
        assertTrue(result == result2);
    }

    @Test
    public void testIsLoading_returnsLiveData() {
        LiveData<Boolean> result = mPrepaidCardReceiptRepository.isLoading();
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void testIsLoading_liveDataSingleInstantiation() {
        LiveData<Boolean> result = mPrepaidCardReceiptRepository.isLoading();
        LiveData<Boolean> result2 = mPrepaidCardReceiptRepository.isLoading();
        assertTrue(result == result2);

    }

    @Test
    public void testGetErrors_returnsLiveData() {
        LiveData<Event<HyperwalletErrors>> result = mPrepaidCardReceiptRepository.getErrors();
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void testGetErrors_liveDataSingleInstantiation() {
        LiveData<Event<HyperwalletErrors>> result = mPrepaidCardReceiptRepository.getErrors();
        LiveData<Event<HyperwalletErrors>> result2 = mPrepaidCardReceiptRepository.getErrors();
        assertTrue(result == result2);
    }

}