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
import android.os.Bundle;



public class IllegalValueFragment extends DialogFragment {
    private String mIllegalValue;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder mIllegalBuilder = new AlertDialog.Builder(getActivity());
        mIllegalBuilder.setTitle(R.string.illegal_value_title);
        mIllegalBuilder.setMessage(String.format(getString(R.string.text_illegal_value), mIllegalValue));
        mIllegalBuilder.setNegativeButton(R.string.exit,
        new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            getActivity().finish();
            }
        });
        mIllegalBuilder.setPositiveButton(R.string.back_button_custom, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //go back to custom dpi dialog upon positive button click
                CustomDpiFragment mCustom = new CustomDpiFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(mCustom, "custom popup");
                ft.commitAllowingStateLoss();

            }
        });

        mIllegalBuilder.create();
        Dialog mIllegalDialog = mIllegalBuilder.create();
        return mIllegalDialog;
    }
    public void mUpdateText (String inputValue){
        //recieva value from the EditText on the custom dpi dialog. this value will be uised in string format in the message of the dialog as an illegal value that was entered
        mIllegalValue=inputValue;
    }
}
