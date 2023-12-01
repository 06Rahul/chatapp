package com.example1.chathub;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatwindow extends AppCompatActivity {

    String reciverimage,reciveruid,recivername,SenderUID;
    CircleImageView profile;
    TextView reciverNName;
    CardView sendbtn;
    EditText textmsg;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    public static String senderimg;
    public  static  String reciveriimg;
    String senderRoom,reciverRoom;
    RecyclerView mmessagesAdapter;
    ArrayList<msgmodelclass>messagesArrayList;
    messagesAdapter messagesAdapter;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatwindow);
        getSupportActionBar().hide();


        mmessagesAdapter=findViewById(R.id.msgsdapter);

        LinearLayoutManager linerlayoutManager =new LinearLayoutManager(this);
        linerlayoutManager.setStackFromEnd(true);
        mmessagesAdapter.setLayoutManager(linerlayoutManager);
        messagesAdapter =new messagesAdapter(chatwindow.this,messagesArrayList);
        mmessagesAdapter.setAdapter(messagesAdapter);




        recivername =getIntent().getStringExtra("nameee");
        reciverimage =getIntent().getStringExtra("reciverimage");
        reciveruid =getIntent().getStringExtra("uid");

        messagesArrayList=new ArrayList<>();
        sendbtn =findViewById(R.id.sendbtnn);
        textmsg=findViewById(R.id.textmsg);

        profile=findViewById(R.id.profilechat);
        reciverNName=findViewById(R.id.recivername);

        Picasso.get().load(reciverimage).into(profile);
        reciverNName.setText(""+recivername);
        DatabaseReference refrence = database.getReference().child("user").child(firebaseAuth.getUid());

        DatabaseReference chatrefrnce=database.getReference().child("user").child(senderRoom).child("message");

        chatrefrnce.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    msgmodelclass messages=dataSnapshot.getValue(msgmodelclass.class);
                    messagesArrayList.add(messages);

                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                senderimg=snapshot.child("profilepic").getValue().toString();
                reciveriimg=reciverimage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SenderUID=firebaseAuth.getUid();
        senderRoom =SenderUID+reciveruid;
        reciverRoom=reciveruid+SenderUID;

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=textmsg.getText().toString();
                if (message.isEmpty()){
                    Toast.makeText(chatwindow.this, "Enter message first", Toast.LENGTH_SHORT).show();

                }
                textmsg.setText("");
                java.util.Date date = new java.util.Date();



                msgmodelclass messages =new msgmodelclass(message,SenderUID,date.getTime());
                database =FirebaseDatabase.getInstance();
                database.getReference().child("chats").child("senderRoom").child("message").push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("chats").child("reciverRoom").child("messages").push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });


            }
        });

    }
}