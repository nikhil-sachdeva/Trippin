package com.example.nikhil.roadsafety.Posts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.nikhil.roadsafety.MainActivity;
import com.example.nikhil.roadsafety.PlaceAutocompleteActivity;
import com.example.nikhil.roadsafety.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.libizo.CustomEditText;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class CreatePost extends AppCompatActivity {
    FloatingActionButton postit;
    private FusedLocationProviderClient mFusedLocationClient;
    Post post = new Post();
    private static final int SELECT_PHOTO = 1;
    private static final int AUTOCOMPLETE_LOCATION_ACTIVITY_REQUEST_CODE = 102;
    CustomEditText caption;
    DatabaseReference myRef;
    LinearLayout l1, l2;
    ImageView add;
    int n=10000,m=10000;
    Bitmap bm;
    FirebaseStorage storage;
    StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
   storage = FirebaseStorage.getInstance();

        caption = findViewById(R.id.caption);
        add = findViewById(R.id.add);
        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);
        postit=findViewById(R.id.postit);
        myRef= FirebaseDatabase.getInstance().getReference();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(CreatePost.this);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Image"), SELECT_PHOTO);
            }
        });

        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(CreatePost.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CreatePost.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(CreatePost.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    double lat = location.getLatitude();
                                    double lang = location.getLongitude();
                                    post.setLatitude(lat);
                                    post.setLongitude(lang);
                                    String name = null;
                                    Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    try {
                                        name = gcd.getFromLocation(lat, lang, 1).get(0).getLocality();
                                        Toast.makeText(CreatePost.this, ""+name+lang+lat, Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    post.setLocation(name);

                                }

                            }
                        });
                                }
        });

        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PlaceAutocompleteActivity.class);
                startActivityForResult(intent, AUTOCOMPLETE_LOCATION_ACTIVITY_REQUEST_CODE);
            }
        });
        postit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                add.setDrawingCacheEnabled(true);
                add.buildDrawingCache();
                Bitmap bitmap = add.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();
                storageRef=storage.getReference();
                Random generator = new Random();
                m = generator.nextInt(m);
                StorageReference imgRef=storageRef.child("image"+m+".jpg");
                UploadTask uploadTask = imgRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String photoStringLink = uri.toString();
                                Log.d("URL", "onSuccess: "+photoStringLink);
                                post.setImgURI(photoStringLink);
                                Log.d("get url", "onSuccess: "+post.getImgURI());
                                Map<String, Object> childUpdates = new HashMap<>();
                                Random generator = new Random();
                                n = generator.nextInt(n);
                                post.setCaption(caption.getText().toString());
                                Log.d("get url", "onSuccess: "+post.getImgURI());
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if(user!=null) {
                                    post.setName(user.getDisplayName());
                                }
                                childUpdates.put("/posts/" + n, post.toMap());
                                myRef.updateChildren(childUpdates);

                            }
                        });

                    }
                });










                Toast.makeText(CreatePost.this, ""+post.toMap().toString(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreatePost.this,MainActivity.class));

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO) {
            try {
                Log.d("s", "onActivityResult: worked");
                Uri imageUri=null;
                if(data!=null) {
                    imageUri = data.getData();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    bm = BitmapFactory.decodeStream(imageStream);
                    add.setImageBitmap(bm);

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == AUTOCOMPLETE_LOCATION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle intentBundle = data.getBundleExtra("LOCATION_BUNDLE");
                double lat = intentBundle.getDouble("LATITUDE");
                double lang = intentBundle.getDouble("LONGITUDE");
                String name = intentBundle.getString("NAME");
                post.setLatitude(lat);
                post.setLongitude(lang);
                post.setLocation(name);

            }
        }
    }

    public void getLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this,new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                post.setLatitude(location.getLatitude());
                post.setLongitude(location.getLongitude());
                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    String loc = gcd.getFromLocation(location.getLatitude(),location.getLongitude(),1).get(0).getLocality();
                    Toast.makeText(CreatePost.this, ""+loc, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

}
