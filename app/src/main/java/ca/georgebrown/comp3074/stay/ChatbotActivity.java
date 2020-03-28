package ca.georgebrown.comp3074.stay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

public class ChatbotActivity extends AppCompatActivity {

    private ImageButton sendMessageButton, sendImageButton;
    private EditText messageInput;
    private RecyclerView userMessageList;

    private String messageReceiverId, messageReceiverName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        InitializeFields();
        
    }

    private void InitializeFields() {
        sendMessageButton = (ImageButton) findViewById(R.id.btnSendMsg);
        sendImageButton = (ImageButton) findViewById(R.id.btnTakePicture);
        messageInput = (EditText) findViewById(R.id.txtTypeMessage);
    }
}
