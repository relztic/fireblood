package com.arieldiax.codelab.fireblood.ui.registration.signup;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.arieldiax.codelab.fireblood.R;
import com.arieldiax.codelab.fireblood.loaders.PlacesAsyncTaskLoader;
import com.arieldiax.codelab.fireblood.models.pojos.Place;
import com.arieldiax.codelab.fireblood.models.widgets.ConfirmBottomSheetDialog;
import com.arieldiax.codelab.fireblood.utils.MapUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class PlacePickerActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LoaderManager.LoaderCallbacks<List<Place>> {

    /**
     * Properties of the activity.
     */
    public static final String PROP_IN_PROVINCE_NAME = "province_name";
    public static final String PROP_OUT_HOSPITAL_NAME = "hospital_name";
    public static final String PROP_OUT_HOSPITAL_LATITUDE = "hospital_latitude";
    public static final String PROP_OUT_HOSPITAL_LONGITUDE = "hospital_longitude";
    public static final String PROP_OUT_MESSAGE_RESOURCE_ID = "message_resource_id";

    /**
     * Views of the activity.
     */
    ProgressBar mMapProgressBar;

    /**
     * Instance of the GoogleMap class.
     */
    GoogleMap mGoogleMap;

    /**
     * Instance of the MapFragment class.
     */
    MapFragment mMapFragment;

    /**
     * Instance of the LoaderManager class.
     */
    LoaderManager mLoaderManager;

    /**
     * Instance of the Marker class.
     */
    Marker mMarker;

    /**
     * Instance of the ConfirmBottomSheetDialog class.
     */
    ConfirmBottomSheetDialog mConfirmBottomSheetDialog;

    /**
     * Name of the province.
     */
    String mProvinceName;

    /**
     * Width of the display.
     */
    int mDisplayWidth;

    /**
     * Height of the display.
     */
    int mDisplayHeight;

    /**
     * Whether or not the markers have been added.
     */
    boolean mMarkersHaveBeenAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);
        initUi();
        init();
        updateUi();
    }

    /**
     * Initializes the user interface view bindings.
     */
    void initUi() {
        mMapProgressBar = (ProgressBar) findViewById(R.id.map_progress_bar);
    }

    /**
     * Initializes the back end logic bindings.
     */
    void init() {
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        mLoaderManager = getLoaderManager();
        mConfirmBottomSheetDialog = new ConfirmBottomSheetDialog(this);
        mProvinceName = getIntent().getExtras().getString(PROP_IN_PROVINCE_NAME);
        mDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        mDisplayHeight = getResources().getDisplayMetrics().heightPixels;
        mMarkersHaveBeenAdded = false;
    }

    /**
     * Updates the user interface view bindings.
     */
    void updateUi() {
        setTitle(mProvinceName);
        mMapFragment.getMapAsync(this);
        View.OnClickListener positiveButtonListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mConfirmBottomSheetDialog.dismiss();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(PROP_OUT_HOSPITAL_NAME, mMarker.getTitle());
                resultIntent.putExtra(PROP_OUT_HOSPITAL_LATITUDE, mMarker.getPosition().latitude);
                resultIntent.putExtra(PROP_OUT_HOSPITAL_LONGITUDE, mMarker.getPosition().longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        };
        DialogInterface.OnDismissListener negativeButtonListener = new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                mMarker.hideInfoWindow();
                mMarker = null;
                MapUtils.setGoogleMapGestures(mGoogleMap);
            }
        };
        mConfirmBottomSheetDialog
                .setTitle(R.string.title_select_hospital)
                .setMessage(R.string.message_are_you_sure)
                .setPositiveButtonListener(positiveButtonListener)
                .setNegativeButtonListener(negativeButtonListener)
        ;
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(PROP_OUT_MESSAGE_RESOURCE_ID, R.string.message_action_canceled);
        setResult(RESULT_CANCELED, resultIntent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.google_maps_style));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(MapUtils.sDominicanRepublicGeographicalBoundaries, mDisplayWidth, mDisplayHeight, 0));
        mGoogleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
        mLoaderManager.initLoader(0, null, this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mMarker = marker;
        mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mConfirmBottomSheetDialog.show();
                mMarker.showInfoWindow();
            }
        }, DateUtils.SECOND_IN_MILLIS / 2);
        return false;
    }

    @Override
    public Loader<List<Place>> onCreateLoader(
            int id,
            Bundle args
    ) {
        return new PlacesAsyncTaskLoader(this, getString(R.string.configuration_google_places_search_query, mProvinceName));
    }

    @Override
    public void onLoadFinished(
            Loader<List<Place>> loader,
            final List<Place> places
    ) {
        if (places == null) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(PROP_OUT_MESSAGE_RESOURCE_ID, R.string.message_please_check_your_internet_connection);
            setResult(RESULT_CANCELED, resultIntent);
            finish();
            return;
        }
        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
        for (Place place : places) {
            latLngBoundsBuilder.include(place.getLocation());
        }
        mMapProgressBar.setVisibility(View.GONE);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(), mDisplayWidth, mDisplayHeight, MapUtils.GOOGLE_MAPS_BOUNDARIES_PADDING), new GoogleMap.CancelableCallback() {

            @Override
            public void onFinish() {
                Handler handler = new Handler();
                int delay = 0;
                if (!mMarkersHaveBeenAdded) {
                    for (final Place place : places) {
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                mGoogleMap.addMarker(new MarkerOptions().position(place.getLocation()).title(place.getName()));
                            }
                        }, delay);
                        delay += DateUtils.SECOND_IN_MILLIS / 10;
                    }
                    mGoogleMap.setOnMarkerClickListener(PlacePickerActivity.this);
                    mMarkersHaveBeenAdded = true;
                }
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        MapUtils.setGoogleMapGestures(mGoogleMap);
                    }
                }, delay);
            }

            @Override
            public void onCancel() {
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<List<Place>> loader) {
    }
}