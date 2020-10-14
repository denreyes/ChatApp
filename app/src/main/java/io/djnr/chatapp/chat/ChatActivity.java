package io.djnr.chatapp.chat;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.djnr.chatapp.R;
import io.djnr.chatapp.auth.SignUpActivity;
import io.djnr.chatapp.base.BaseActivity;
import io.djnr.chatapp.api.models.ChatMessage;

public class ChatActivity extends BaseActivity {
    private RecyclerView rvConversation;
    private EditText etMessage;
    private Button btnSend, btnLogout;

    private DatabaseReference mFirebaseDatabaseReference;

    private ChatAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rvConversation = findViewById(R.id.rv_conversation);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        btnLogout = findViewById(R.id.btn_logout);

        btnLogout.setVisibility(View.VISIBLE);
        mLinearLayoutManager = new LinearLayoutManager(this);

        mUsername = getIntent().getStringExtra("username");

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        fetchMessages();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMessage.getText().toString();
                if (!message.trim().isEmpty()) {
                    ChatMessage chatMessage = new ChatMessage(mUsername, message);
                    mFirebaseDatabaseReference.child("messages")
                            .push().setValue(chatMessage);

                    etMessage.setText("");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null)
            mAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null)
            mAdapter.stopListening();
    }

    private void fetchMessages() {
        showProgress();

        SnapshotParser<ChatMessage> parser = new SnapshotParser<ChatMessage>() {
            @Override
            public ChatMessage parseSnapshot(DataSnapshot dataSnapshot) {
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                return chatMessage;
            }
        };

        DatabaseReference messagesRef = mFirebaseDatabaseReference.child("messages");
        FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(messagesRef, parser)
                .build();

        mAdapter = new ChatAdapter(options, mUsername);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = mAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (messageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rvConversation.scrollToPosition(positionStart);
                }
                hideProgress();
            }
        });

        rvConversation.setLayoutManager(mLinearLayoutManager);
        rvConversation.setAdapter(mAdapter);
    }

    /**
     * Logout user and proceed to {@link SignUpActivity}
     */
    private void logout() {
        FirebaseAuth.getInstance().signOut();

        Intent i = new Intent(ChatActivity.this, SignUpActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
