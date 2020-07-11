package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thenameless.model.Namelesser;
import com.example.thenameless.model.ProductDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProductPreview extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Product Preview";
    private Bundle bundle;

    private TextView priceTextView, detailsTextView, userNameTextView, titleTextView;
    private Button callButton, chatButton;
    private ImageButton previousImage, nextImage, favButton;
    private ImageView imageView;

    private int currentImageIndex = 0, mx = 1, flag = 0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection(Namelesser.getInstance().getUserId() + "Favourites");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_preview);

        bundle = getIntent().getExtras();

        priceTextView = findViewById(R.id.preview_price_textView);
        detailsTextView = findViewById(R.id.preview_details_textView);
        userNameTextView = findViewById(R.id.preview_account_textView);
        titleTextView = findViewById(R.id.preview_title_textView);
        callButton = findViewById(R.id.preview_call_button);
        favButton = findViewById(R.id.preview_fav_button);
        chatButton = findViewById(R.id.preview_chat_button);
        previousImage = findViewById(R.id.preview_previous_imageButton);
        nextImage = findViewById(R.id.preview_next_imageButton);
        imageView = findViewById(R.id.preview_imageView);

        for(int i=1;i<=4;i++){
            if(bundle.getString("image" + i + "_url") == null){
                break;
            }
            mx=i;
        }

        if(mx > 1){
            nextImage.setVisibility(View.VISIBLE);
        }

        Picasso.get()
                .load(bundle.getString("image1_url"))
                .placeholder(R.drawable.cool_backgrounds)
                .into(imageView);

        titleTextView.setText(bundle.getString("title"));
        detailsTextView.setText("Type: " + bundle.getString("type")
                                    + "\n\n" + "Description:\n"
                                    + bundle.getString("description")
                                    + "\n\n" + "Date Added On: "
                                    + bundle.getString("timeAdded"));
        detailsTextView.setMovementMethod(new ScrollingMovementMethod());
        priceTextView.setText(String.valueOf(bundle.getInt("price")));
        userNameTextView.setText(bundle.getString("userName"));

        if(bundle.getString("image1_url") != null)
        collectionReference.whereEqualTo("image1_url",bundle.getString("image1_url"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            if(task.getResult().size() == 1) {
                                flag = 1;
                                //Toast.makeText(context, String.valueOf(task.getResult().size()), Toast.LENGTH_SHORT).show();
                                favButton.setBackgroundResource(R.drawable.ic_baseline_star_24);
                            }
                        }
                    }
                });

        nextImage.setOnClickListener(this);
        previousImage.setOnClickListener(this);
        callButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);
        favButton.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu_bar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.cancel)
        {
            startActivity(new Intent(ProductPreview.this,HomePage.class));
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.preview_call_button:
                //call the user's whose product it is
                break;
            case R.id.preview_chat_button:
                //chat with the user
                break;
            case R.id.preview_next_imageButton:

                currentImageIndex++;

                if(currentImageIndex == mx-1) {
                    nextImage.setVisibility(View.INVISIBLE);
                }
                if(currentImageIndex > 0){
                    previousImage.setVisibility(View.VISIBLE);
                }

                Picasso.get()
                        .load(bundle.getString("image" + (currentImageIndex+1) + "_url"))
                        .placeholder(R.drawable.cool_backgrounds)
                        .into(imageView);

                break;
            case R.id.preview_previous_imageButton:

                currentImageIndex--;

                if(currentImageIndex < mx-1) {
                    nextImage.setVisibility(View.VISIBLE);
                }
                if(currentImageIndex == 0){
                    previousImage.setVisibility(View.INVISIBLE);
                }

                Picasso.get()
                        .load(bundle.getString("image" + (currentImageIndex+1) + "_url"))
                        .placeholder(R.drawable.cool_backgrounds)
                        .into(imageView);
                break;
            case R.id.preview_fav_button:
                if(flag == 0) {
                    addFavourite();
                }
                else {
                    removeFavourite();
                }
        }

    }

    private void addFavourite() {

        ProductDetails productDetails = new ProductDetails();

        productDetails.setUserName(bundle.getString("userName"));
        productDetails.setTitle(bundle.getString("title"));
        productDetails.setDescription(bundle.getString("description"));
        productDetails.setUserId(bundle.getString("userId"));
        productDetails.setPrice(Integer.parseInt(String.valueOf(Objects.requireNonNull(bundle.getInt("price")))));
        productDetails.setImage1_url(bundle.getString("image1_url"));
        productDetails.setType(bundle.getString("type"));
        productDetails.setTimeAdded(bundle.getString("timeAdded"));

        if(bundle.containsKey("image2_url"))
            productDetails.setImage2_url(bundle.getString("image2_url"));
        if(bundle.containsKey("image3_url"))
            productDetails.setImage3_url(bundle.getString("image3_url"));
        if(bundle.containsKey("image4_url"))
            productDetails.setImage4_url(bundle.getString("image4_url"));

        collectionReference.add(productDetails)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()) {
                                        favButton.setBackgroundResource(R.drawable.ic_baseline_star_24);
                                    }
                                }
                            });
        flag = 1;

    }

    private void removeFavourite() {

        collectionReference.whereEqualTo("image1_url",bundle.getString("image1_url"))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for(QueryDocumentSnapshot snapshot : task.getResult()) {
                                        snapshot.getReference().delete();
                                    }
                                    favButton.setBackgroundResource(R.drawable.ic_baseline_star_23);
                                }
                            }
                        });
        flag = 0;
    }
}