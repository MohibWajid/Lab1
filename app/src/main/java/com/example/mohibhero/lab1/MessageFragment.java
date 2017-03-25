package com.example.mohibhero.lab1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by MohibHero on 3/13/2017.
 */
public class MessageFragment extends Fragment {

    private int deviceTypeUsed;
    public MessageFragment() {
        deviceTypeUsed = 0;
    }

    public MessageFragment(int d) {
        deviceTypeUsed = d;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_fragment, container, false);

        Bundle data = this.getArguments();

        TextView messageText = (TextView)v.findViewById(R.id.messageTextBox);
        final TextView messageID = (TextView)v.findViewById(R.id.messageIDBox);

        messageText.setText(data.getString("messageText"));
        messageID.setText(data.getString("messageID"));

        Button deleteMessage = (Button)v.findViewById(R.id.deleteMessageBox);
        deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(deviceTypeUsed == 0) {
                    Intent i = new Intent();
                    i.putExtra("id", messageID.getText());
                    getActivity().setResult(5, i);
                    getActivity().finish();
                } else {
                    ((ChatWindow)getActivity()).deleteMessage(Integer.parseInt(messageID.getText().toString()));
                }
            }
        });


        return v;
    }
}
