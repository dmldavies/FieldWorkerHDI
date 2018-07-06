package com.example.titomi.workertrackerloginmodule.supervisor;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.titomi.workertrackerloginmodule.R;


/**
 * Created by NeonTetras on 01-Nov-17.
 */
public class ConfirmDialog extends AlertDialog implements View.OnClickListener{

    Button yes,no;
    Context cxt;
    Action action;
    TextView messageView;
    public ConfirmDialog(Context context, Action action) {
        super(context);
        cxt = context;
        this.action = action;
        View view =  View.inflate(cxt,R.layout.confirm_dialog_fragment_layout,null);
        yes = (Button)view.findViewById(R.id.yes);
        no = (Button)view.findViewById(R.id.no);
        messageView = (TextView)view.findViewById(R.id.message);


        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        setView(view);
    }

    protected ConfirmDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }


    @Override
    public void setMessage(CharSequence message) {
        //super.setMessage(message);
        messageView.setText(message);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.yes:
              action.execute();
                break;
            case R.id.no:
                action.negativeActionPerformed();
                break;
        }
        dismiss();
    }

    public void hideButton(int which){
        switch (which){
            case BUTTON_POSITIVE:
                yes.setVisibility(View.GONE);
                break;
            case BUTTON_NEGATIVE:
                no.setVisibility(View.GONE);
                break;
            case BOTH:
                yes.setVisibility(View.GONE);
                no.setVisibility(View.GONE);
                break;

        }
    }

    public void setButtonText(int whichButton,String text){
        switch (whichButton) {
            case BUTTON_POSITIVE:
                yes.setText(text);
                break;
            case BUTTON_NEGATIVE:
                no.setText(text);
                break;
        }
    }

    public static final int BOTH = -10;
}
