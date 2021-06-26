package com.hackathon.recumeet.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hackathon.recumeet.R;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class MeetFragment extends Fragment {
    EditText code;
    Button join;
    Button host;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_meet, container, false);

        code = v.findViewById(R.id.code);
        join = v.findViewById(R.id.join);
        host = v.findViewById(R.id.host);
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();

        URL serverURL;

        try{
            serverURL = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverURL)
                    .setWelcomePageEnabled(false).build();
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }



        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                        .setRoom(uuidAsString).setWelcomePageEnabled(false).build();
                JitsiMeetActivity.launch(getContext(),options);
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                        .setRoom(code.getText().toString()).setWelcomePageEnabled(false).build();
                JitsiMeetActivity.launch(getContext(),options);
            }
        });


        return v;
    }
}