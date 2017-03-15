package com.wubydax.romcontrol.v2_5_By_Zidni;

/*
 * Copyright (c) 2014-15 Anna Berkovitch & Roberto Mariani
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class SetDpiFragment extends DialogFragment {

    public SetDpiFragment() {

    }

    String dpi = "";
    List<String> values;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        values = Arrays.asList(getActivity().getResources().getStringArray(R.array.dpi_dialog_values));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Resources res = getResources();
        // retrieve lcd density from get.prop - it will update displayed after reboot.. is not reading from build.prop

        File file = new File("/system/build.prop");
        if (file.exists())
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    if (line.contains("ro.sf.lcd_density")) {
                        dpi = line.substring(line.indexOf("=") + 1, line.length());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        // we build title pushing inside value from buffer reader (dpi string)
        builder.setTitle(String.format(res.getString(R.string.show_current_dpi), dpi))
                .setSingleChoiceItems(R.array.dpi_dialog_items, getIndex(dpi), null)
                .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        String value = getValue(selectedPosition);
                        if (selectedPosition != 10) {

                            Command applyLive = new Command(0, "wm density " + value);
                            try {
                                RootTools.getShell(true).add(applyLive);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                            } catch (RootDeniedException e) {
                                e.printStackTrace();
                            }
                            Command applyToBuild = new Command(0, "busybox mount -o remount,rw /system", "cd /system", "sed -i '/ro.sf.lcd_density/c\\r ro.sf.lcd_density=" + value + "' build.prop");
                            try {
                                RootTools.getShell(true).add(applyToBuild);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                            } catch (RootDeniedException e) {
                                e.printStackTrace();
                            }
                            DialogReboot mDialogReboot = new DialogReboot();
                            FragmentTransaction mCustomTransaction = getFragmentManager().beginTransaction();
                            mCustomTransaction.add(mDialogReboot, "Reboot");
                            mCustomTransaction.commitAllowingStateLoss();
                            Toast.makeText(getActivity(), "Custom DPI on screen successfully applied",
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), "Please reboot your device for properly enable this feature",
                                    Toast.LENGTH_LONG).show();
                        }

                     else {

                            CustomDpiFragment mCustomDpiFragment = new CustomDpiFragment();
                            FragmentTransaction mCustomTransaction = getFragmentManager().beginTransaction();
                            mCustomTransaction.add(mCustomDpiFragment, "custom dpi");
                            mCustomTransaction.commitAllowingStateLoss();

                        }
                    }

                })
        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); //has to be false or else will hit invisible activity blocking ui. cannot use ondetach
        return dialog;
    }

    private int getIndex(String dpi) {
        return values.indexOf(dpi) != -1 ? values.indexOf(dpi) : 10;
    }

    private String getValue(int position) {
        return position != 10 ? values.get(position) : null;
    }


}

