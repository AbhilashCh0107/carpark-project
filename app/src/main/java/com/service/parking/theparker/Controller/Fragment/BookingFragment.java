package com.service.parking.theparker.Controller.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.service.parking.theparker.Controller.Activity.MapDirectionActivity;
import com.service.parking.theparker.Controller.Adapters.BookingAdapter;
import com.service.parking.theparker.Model.ParkingBooking;
import com.service.parking.theparker.Model.Transaction;
import com.service.parking.theparker.R;
import com.service.parking.theparker.Services.NetworkServices;
import com.service.parking.theparker.View.BuySlotDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;


public class BookingFragment extends Fragment {

    @BindView(R.id.myBookingRecyclerView)
    RecyclerView myBookingRecyclerView;
    BookingAdapter mySpotsAdapter;
    List<ParkingBooking> parkingBookingList;

    public BookingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_booking, container, false);
        ButterKnife.bind(this, v);
        parkingBookingList = new ArrayList<>();
        mySpotsAdapter = new BookingAdapter(parkingBookingList, getContext(), this);

        RecyclerView.LayoutManager mLayoutmanager = new LinearLayoutManager(getContext());
        myBookingRecyclerView.setLayoutManager(mLayoutmanager);
        myBookingRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myBookingRecyclerView.setAdapter(mySpotsAdapter);

        NetworkServices.ParkingPin.getBookingList(parkingBookingList, mySpotsAdapter);
        return v;
    }


    public void setCompleted(ParkingBooking parkingBooking) {

        Calendar calendar = Calendar.getInstance();
        boolean isRefund=false;
        int hours=calendar.getTime().getHours();
        if (parkingBooking.getSlotNo().equalsIgnoreCase("slot0") && hours >= 7 && hours <= 9) {
            isRefund = true;
        } else if (parkingBooking.getSlotNo().equalsIgnoreCase("slot1") && hours >= 9 && hours <= 11) {
            isRefund = true;
        } else if (parkingBooking.getSlotNo().equalsIgnoreCase("slot2") && hours >= 11 && hours <= 13) {
            isRefund = true;
        } else if (parkingBooking.getSlotNo().equalsIgnoreCase("slot3") && hours >= 13 && hours <= 15) {
            isRefund = true;
        } else if (parkingBooking.getSlotNo().equalsIgnoreCase("slot4") && hours >= 15 && hours <= 17) {
            isRefund = true;
        } else if (parkingBooking.getSlotNo().equalsIgnoreCase("slot5") && hours >= 17 && hours <= 19) {
            isRefund = true;
        } else if (parkingBooking.getSlotNo().equalsIgnoreCase("slot6") && hours >= 19 && hours <= 21) {
            isRefund = true;
        } else {
            isRefund = false;
        }

        String refundBook=isRefund?"Parking Ended Successfully And 50% of your payment Refund To your wallet on Early booking end":"Parking Ended Successfully";

        Transaction parkingBookingTransation = new Transaction();
        parkingBookingTransation.setAmount(parkingBooking.getPrice());
        if (parkingBooking.isPayLater()) {
            BuySlotDialog slotBuyDialog;
            Runnable funcBuyPackege;
            Runnable funcPayLater;
            boolean finalIsRefund = isRefund;
            funcBuyPackege = () -> {
                NetworkServices.TransactionData.payForParkingSlot(parkingBookingTransation, parkingBooking, parkingBooking.isPayLater(), finalIsRefund);
                Toasty.success(getActivity(), refundBook).show();
            };

            funcPayLater = () -> {

            };
            slotBuyDialog = new BuySlotDialog(parkingBookingTransation, parkingBooking, getActivity(), funcBuyPackege, funcPayLater, R.style.PackageBuyAnimation_SmileWindow, true);
            slotBuyDialog.setCancellable(true);
            slotBuyDialog.showAlertDialog();
        } else {
            NetworkServices.TransactionData.payForParkingSlot(parkingBookingTransation, parkingBooking, parkingBooking.isPayLater(),isRefund);
            Toasty.success(getActivity(), refundBook).show();
        }

    }

    public void setDirection(ParkingBooking parkingBooking) {
        Intent intent = new Intent(getContext(), MapDirectionActivity.class);
        intent.putExtra("object", parkingBooking);
        startActivity(intent);

    }
}
