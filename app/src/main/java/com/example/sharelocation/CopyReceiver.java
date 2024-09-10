package com.example.sharelocation;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CopyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("COPY_CODE_ACTION".equals(intent.getAction())) {
            // Retrieve the code from the intent extras
            String code = intent.getStringExtra("code");

            // Check if the code is not null before copying it to the clipboard
            if (code != null) {
                // Copy the code to the clipboard
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("code", code);
                clipboard.setPrimaryClip(clip);

                // Show a toast message to indicate that the code has been copied
                Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show();
            } else {
                // Show a toast message indicating that the code is null
                Toast.makeText(context, "Code is null", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
