package com.petpaw.fragments.screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.petpaw.R;
import com.petpaw.databinding.FragmentMapsBinding;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {
    FragmentMapsBinding binding;
    GoogleMap mMap;
    FirebaseFirestore db;
    FirebaseAuth auth;
    List<Marker> markerList = new ArrayList<>();

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
            String conversationID = requireArguments().getString("conversationID").toString();
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
                                            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                                            Marker marker = mMap.addMarker(markerOptions);
                                            marker.setTitle(doc.getId());
                                            markerList.add(marker);

                                        }
                                    }
                                }
                            }
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