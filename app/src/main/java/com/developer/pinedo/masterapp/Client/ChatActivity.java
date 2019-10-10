package com.developer.pinedo.masterapp.Client;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.developer.pinedo.masterapp.ListChefActivity;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.adapter.ChatAdapterRecicleView;
import com.developer.pinedo.masterapp.adapter.FilterAdapterRecicleView;
import com.developer.pinedo.masterapp.models.CardCategory;
import com.developer.pinedo.masterapp.models.CardOrders;
import com.developer.pinedo.masterapp.models.Chat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;

    ArrayList<Chat> listChat;
    RecyclerView chatRecycler;

    Button send;
    EditText ed_message;
    TextView content_order;

    CardOrders cardOrders;
    private ChatAdapterRecicleView chatAdapterRecicleView;

    int type_stakeholder=1;
    String name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        showToolbar("Soporte",true);

        Bundle param = getIntent().getExtras();

        content_order = findViewById(R.id.content_order);

        if(param != null){
            cardOrders = (CardOrders) param.getSerializable("order");
            Log.d("JORGE",cardOrders.toString());
            if(param.getInt("type_stakeholder")==2){
                type_stakeholder = 2;
            }
            content_order.setText("Client "+cardOrders.getClient() + " Orden #"+cardOrders.getId());

        }
        chatRecycler = findViewById(R.id.chatRecycler);
        send = findViewById(R.id.send);
        ed_message = findViewById(R.id.ed_message);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("chat_"+cardOrders.getId());
        myRef.orderByChild("created_at");

        LinearLayoutManager linearLayoutManagerF=new LinearLayoutManager(this);
        linearLayoutManagerF.setOrientation(LinearLayoutManager.VERTICAL);
        chatRecycler.setLayoutManager(linearLayoutManagerF);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                LocalDateTime now = LocalDateTime.now();

                Chat data = new Chat();
                data.setDescription(ed_message.getText().toString());
                data.setCreated_at(dtf.format(now));
                data.setName(Utils.getItem(ChatActivity.this,"name"));
                data.setType_stakeholder(type_stakeholder);
                myRef.push().setValue(data);
                //myRef.child(String.valueOf(new Date().getTime())).setValue(data);
                ed_message.setText("");


                View view = getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(getApplicationContext());
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listChat = new ArrayList<>();
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    Chat chat = data.getValue(Chat.class);
                    listChat.add(chat);
                }

                chatAdapterRecicleView = new ChatAdapterRecicleView(listChat,R.layout.card_view_chat, ChatActivity.this);
                chatRecycler.setAdapter(chatAdapterRecicleView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showToolbar(String titulo,boolean upButton){
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titulo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }

    @Override
    protected void onStart() {
        super.onStart();
        ;

    }
}
