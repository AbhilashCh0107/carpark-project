package com.service.parking.theparker.View;


import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.service.parking.theparker.Model.ParkingBooking;
import com.service.parking.theparker.Model.Transaction;
import com.service.parking.theparker.R;
import com.service.parking.theparker.Services.NetworkServices;

import es.dmoral.toasty.Toasty;

public class BuySlotDialog {
    Activity dialogContext;
    AlertDialog alertDialog;
    int style;

    //    Context con;
    Transaction transaction;
    ParkingBooking parkingBooking;
    boolean cancellable = false;
    Runnable funcRun;
    Runnable funcPayLater;
    boolean isPayLater;

//    public TextView dia_package_name;
//    public TextView dia_package_price;
//    public TextView dia_after_balance;
////    public Button dia_confirm_purchase;
//    public TextView dia_wallet_balance;

    public BuySlotDialog(Transaction transaction, ParkingBooking parkingBooking, Activity activity, Runnable funcRun, Runnable funcPayLater, int style,boolean isPayLater) {
        this.dialogContext = activity;
        this.transaction = transaction;
        this.parkingBooking = parkingBooking;
        this.funcRun = funcRun;
        this.funcPayLater = funcPayLater;
        this.style = style;
        this.isPayLater = isPayLater;
    }

    public void showAlertDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(dialogContext);
        View v = dialogContext.getLayoutInflater().inflate(R.layout.slot_buy_dialog, null);
        TextView dia_after_balance = v.findViewById(R.id.dia_after_balance);
        TextView dia_package_name = v.findViewById(R.id.dia_package_name);
        TextView dia_package_price = v.findViewById(R.id.dia_package_price);
        Button dia_confirm_purchase = v.findViewById(R.id.dia_confirm_Purchase_btn);
        Button dia_pay_later_btn = v.findViewById(R.id.dia_pay_later_btn);
        View viewPay = v.findViewById(R.id.viewPay);
        TextView dia_wallet_balance = v.findViewById(R.id.dia_wallet_balance);


        if (isPayLater){
            dia_pay_later_btn.setVisibility(View.GONE);
            viewPay.setVisibility(View.GONE);
        }
        int wallet_balance = Integer.parseInt(NetworkServices.userProfile.Balance);
        int package_price = Integer.parseInt(transaction.getAmount());

        adb.setView(v);
        alertDialog = adb.create();
        alertDialog.getWindow().getAttributes().windowAnimations = style;

        dia_package_name.setText(parkingBooking.getParkingArea() + " " + parkingBooking.getSlotNo());
        dia_package_price.setText(transaction.getAmount());
        dia_wallet_balance.setText("" + wallet_balance);

        if (wallet_balance > package_price || wallet_balance == package_price) {
            //function for purchasing package
            dia_confirm_purchase.setText("Pay Now");
            dia_after_balance.setText("Balance after buying this slot: $" + (wallet_balance - package_price));
        } else if (wallet_balance < package_price) {
            dia_after_balance.setText("You need to add : $" + (package_price - wallet_balance) + " to buy this slot");
//            dia_after_balance.setTextColor(ContextCompat.getColor(con,R.color.colorRed));
            dia_confirm_purchase.setText("Add Balance");

            Runnable runnable = () -> {
//                Fragment fragment = new Fragment();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.wallet_fragment, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
            };

            Toasty.error(dialogContext, "You have insufficient wallet balance", Toast.LENGTH_LONG).show();
        }

        alertDialog.setCancelable(isCancellable());
        alertDialog.setCanceledOnTouchOutside(isCancellable());
        alertDialog.show();

        dia_confirm_purchase.setOnClickListener(v1 -> {

            if (dia_confirm_purchase.getText().toString().equalsIgnoreCase("Pay Now")) {
                funcRun.run();
                Toasty.info(dialogContext, "Buy Slot Clicked", Toast.LENGTH_LONG).show();
                closeSpinerDialog();
            } else {
                Toasty.info(dialogContext, "You have insufficient wallet balance", Toast.LENGTH_LONG).show();
                closeSpinerDialog();
            }

        });

        dia_pay_later_btn.setOnClickListener(v1 -> {
            funcPayLater.run();
//                xcv
            Toasty.info(dialogContext, "Pay Later", Toast.LENGTH_LONG).show();
            closeSpinerDialog();
        });

    }

    public void closeSpinerDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    private boolean isCancellable() {
        return cancellable;
    }

    public void setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
    }


}