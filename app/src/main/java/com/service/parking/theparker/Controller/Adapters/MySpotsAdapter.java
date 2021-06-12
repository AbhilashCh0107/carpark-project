package com.service.parking.theparker.Controller.Adapters;import android.Manifest;import android.content.Context;import androidx.annotation.NonNull;import androidx.core.app.ActivityCompat;import androidx.recyclerview.widget.RecyclerView;import android.content.pm.PackageManager;import android.util.Log;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.ImageView;import android.widget.TextView;import com.google.android.gms.maps.CameraUpdateFactory;import com.google.android.gms.maps.GoogleMap;import com.google.android.gms.maps.MapView;import com.google.android.gms.maps.MapsInitializer;import com.google.android.gms.maps.OnMapReadyCallback;import com.google.android.gms.maps.model.LatLng;import com.google.android.gms.maps.model.LatLngBounds;import com.google.android.gms.maps.model.MarkerOptions;import com.service.parking.theparker.Model.LocationPin;import com.service.parking.theparker.R;import com.squareup.picasso.Picasso;import java.util.List;import butterknife.BindView;import butterknife.ButterKnife;public class MySpotsAdapter extends RecyclerView.Adapter<MySpotsAdapter.MySpotsViewHolder> {    private List<LocationPin> locationPinList;    private Context con;    public MySpotsAdapter(List<LocationPin> locationPinList, Context con) {        this.locationPinList = locationPinList;        this.con = con;    }    @NonNull    @Override    public MySpotsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {        con = parent.getContext();        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_spots_layout, parent, false);        return new MySpotsViewHolder(itemView);    }    @Override    public void onBindViewHolder(@NonNull final MySpotsViewHolder myViewHolder, int i) {//        GoogleMap thisMap = myViewHolder.googleMap;        LocationPin pin = locationPinList.get(i);        Log.d("RANDOM :", pin.getPinloc().toString());        myViewHolder.mPrice.setText(pin.getPrice());        myViewHolder.mNo_of_space.setText(pin.getNumberofspot());        myViewHolder.mSpot_type.setText(pin.getType());        Double lat = pin.getPinloc().get("lat");        Double lon = pin.getPinloc().get("long");        myViewHolder.addMarker(new LatLng(lat,lon));//        String api_key = "AIzaSyBLzCmtOL3wkgaerlQIMfC2-q1Rit10JfA";//        String api_key2 = "AIzaSyAzp2x7PefS9ikLJ6l8r3vYyXfe0430c7Y";//        String url ="https://maps.googleapis.com/maps/api/staticmap?";//        url+="&zoom=13";//        url+="&size=600x200";//        url+="&maptype=roadmap";//        url+="&markers=color:green%7Clabel:G%7C"+lat+", "+lon;//        url+="&key="+ api_key;//////        Log.d("RANDOM :",url);//        Picasso.get().load(url).into(myViewHolder.mSpot_map_image);//        Glide.with(con).load("https://www.google.com/maps/place/1350+Massachusetts+Ave,+Harvard+University,+Cambridge,+MA+02138/@42.37271,-71.1188557,17z/data=!3m1!4b1!4m2!3m1!1s0x89e37742dc1c29a7:0xc3dbda7a42311d03").into(myViewHolder.mSpot_map_image);    }    @Override    public void onViewRecycled(@NonNull MySpotsViewHolder holder) {        super.onViewRecycled(holder);        if (holder.googleMap != null) {            holder.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);        }    }    @Override    public int getItemCount() {        return locationPinList.size();    }    class MySpotsViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback    {        @BindView(R.id.spot_type)        TextView mSpot_type;        @BindView(R.id.spot_price)        TextView mPrice;        @BindView(R.id.no_of_spots)        TextView mNo_of_space;        @BindView(R.id.spotMapImageView)        ImageView mSpot_map_image;        @BindView(R.id.spotMap)        MapView mSpot_map;        GoogleMap googleMap;        MySpotsViewHolder(View view) {            super(view);            ButterKnife.bind(this, view);            if (mSpot_map != null) {                mSpot_map.onCreate(null);                mSpot_map.onResume();                mSpot_map.getMapAsync(this);            }        }        public void addMarker(LatLng latLng) {            MarkerOptions markerOptions = new MarkerOptions();            markerOptions.position(latLng);           if (this.googleMap!= null){                this.googleMap.clear();                this.googleMap.addMarker(markerOptions);//               //Get the width and height of the device's screen//               int width = con.getResources().getDisplayMetrics().widthPixels;//               int height = (int) ((con.getResources().getDisplayMetrics().heightPixels) * 0.85);//               int padding = (int) (width * 0.15);////               LatLngBounds.Builder builder = new LatLngBounds.Builder();////Insert your location//               builder.include(latLng);//               LatLngBounds bounds = builder.build();               this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));//               this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height/6, padding));            }        }        @Override        public void onMapReady(GoogleMap googleMap) {            MapsInitializer.initialize(con);            this.googleMap = googleMap;            if (ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {                return;            }            this.googleMap.setMyLocationEnabled(true);            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);            LocationPin pin = locationPinList.get(getAdapterPosition());            Double lat = pin.getPinloc().get("lat");            Double lon = pin.getPinloc().get("long");            addMarker(new LatLng(lat,lon));        }    }}