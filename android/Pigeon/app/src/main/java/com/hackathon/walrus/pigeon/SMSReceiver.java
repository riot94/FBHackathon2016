package com.hackathon.walrus.pigeon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by riot94 on 3/9/2016.
 */
public class SMSReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent){
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = null;
        String str = "";
        if (bundle != null){
            Object[] pdus = (Object[]) bundle.get("pdus"); //protocol description unit
            messages = new SmsMessage[pdus.length];
            for (int i=0; i<messages.length;i++){
                messages[i]=SmsMessage.createFromPdu((byte[])pdus[i]);
                str += "Message from " + messages[i].getOriginatingAddress();
                str += " :";
                str += messages[i].getMessageBody().toString();
                str += "\n";
            }
            //display the message
            Toast.makeText(context,str,Toast.LENGTH_SHORT).show();

            //send a broadcast intent to update the SMS received in a textview
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("SMS_RECEIVED_ACTION");
            broadcastIntent.putExtra("sms",str);
            context.sendBroadcast(broadcastIntent);
        }
    }
}