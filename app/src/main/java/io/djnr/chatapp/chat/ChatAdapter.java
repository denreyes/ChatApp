package io.djnr.chatapp.chat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import io.djnr.chatapp.R;
import io.djnr.chatapp.api.models.ChatMessage;

public class ChatAdapter extends FirebaseRecyclerAdapter<ChatMessage, ChatAdapter.ViewHolder> {
    private static final int TYPE_SELF = 0;
    private static final int TYPE_OTHERS = 1;
    private String mUsername;

    public ChatAdapter(@NonNull FirebaseRecyclerOptions<ChatMessage> options, String username) {
        super(options);
        mUsername = username;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_SELF)
            return new ViewHolder(inflater.inflate(R.layout.item_chat_self, parent, false));
        else
            return new ViewHolder(inflater.inflate(R.layout.item_chat_others, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        String name = getItem(position).getName();
        if (name.equals(mUsername))
            return TYPE_SELF;
        else
            return TYPE_OTHERS;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChatMessage chat) {
        holder.tvMessage.setText(chat.getText());
        if (holder.getItemViewType() == TYPE_SELF)
            holder.tvName.setText(R.string.general_you);
        else
            holder.tvName.setText(chat.getName());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvName;

        public ViewHolder(View view) {
            super(view);
            tvMessage = view.findViewById(R.id.tv_message);
            tvName = view.findViewById(R.id.tv_name);
        }
    }

}
