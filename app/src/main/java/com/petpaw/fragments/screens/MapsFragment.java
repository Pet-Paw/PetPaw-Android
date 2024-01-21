package com.petpaw.fragments.screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.zzad;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.petpaw.R;
import com.petpaw.databinding.FragmentMapsBinding;
import com.petpaw.models.Conversation;
import com.petpaw.models.User;
import com.petpaw.utils.BubbleTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsFragment extends Fragment {
    FragmentMapsBinding binding;
    GoogleMap mMap;
    FirebaseFirestore db;
    FirebaseAuth auth;
    List<Marker> markerList = new ArrayList<>();
    private String conversationID;

    HashMap<String, User> userSet = new HashMap<>();

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            db.collection("Conversations")
                    .document(conversationID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot doc) {
                            Conversation con = doc.toObject(Conversation.class);
                            List<String> userIdList =  con.getMemberIdList();
                            db.collection("users")
                                    .whereIn(FieldPath.documentId(), userIdList)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (DocumentSnapshot doc : queryDocumentSnapshots){
                                                userSet.put(doc.getId(), doc.toObject(User.class));
                                            }
                                            db.collection("Conversations")
                                                    .document(conversationID)
                                                    .collection("locations")
                                                    .whereEqualTo("isSharing", true)
                                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable QuerySnapshot query, @Nullable FirebaseFirestoreException error) {
                                                            for (Marker marker : markerList){
                                                                marker.remove();
                                                            }
                                                            markerList.clear();
                                                            if(query != null){
                                                                for (DocumentSnapshot doc: query){
                                                                    User curUser = userSet.get(doc.getId());
                                                                    if ((boolean) doc.get("isSharing")){
                                                                        GeoPoint geo = (GeoPoint) doc.get("location");
                                                                        if(!(geo.getLatitude() == 0 && geo.getLongitude() == 0)){
                                                                            LatLng latLng = new LatLng(geo.getLatitude(), geo.getLongitude());
                                                                            if(query.size() > 1){
                                                                                if(doc.getId() != auth.getCurrentUser().getUid()){
                                                                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                                                                                }
                                                                            } else {
                                                                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                                                                            }
                                                                            String imgURL = userSet.get(doc.getId()).getImageURL();
                                                                            if(imgURL != null){
                                                                                Target target = new Target() {
                                                                                    @Override
                                                                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                                                        MarkerOptions options = new MarkerOptions().position(latLng).title(curUser.getName())
                                                                                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                                                                        Marker marker = mMap.addMarker(options);
                                                                                        marker.setTitle(curUser.getUid());
                                                                                        markerList.add(marker);
                                                                                    }

                                                                                    @Override
                                                                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                                                                    }

                                                                                    @Override
                                                                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                                                    }
                                                                                };

                                                                                Picasso.get()
                                                                                        .load(curUser.getImageURL())
                                                                                        .resize(120,120)
                                                                                        .transform(new BubbleTransformation(5))
                                                                                        .centerCrop()
                                                                                        .into(target);
                                                                            } else {
                                                                                Target target = new Target() {
                                                                                    @Override
                                                                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                                                        MarkerOptions options = new MarkerOptions().position(latLng).title(curUser.getName())
                                                                                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                                                                        Marker marker = mMap.addMarker(options);
                                                                                        marker.setTitle(curUser.getUid());
                                                                                        markerList.add(marker);
                                                                                    }

                                                                                    @Override
                                                                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                                                                    }

                                                                                    @Override
                                                                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                                                    }
                                                                                };

                                                                                Picasso.get()
                                                                                        .load(R.drawable.default_avatar)
                                                                                        .resize(120,120)
                                                                                        .transform(new BubbleTransformation(5))
                                                                                        .centerCrop()
                                                                                        .into(target);
                                                                            }

                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });
                                        }
                                    });

                        }
                    });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        conversationID = requireArguments().getString("conversationID").toString();
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }


}