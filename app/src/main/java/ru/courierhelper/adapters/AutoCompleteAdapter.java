package ru.courierhelper.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ru.courierhelper.R;

/**
 * Created by Ivan on 14.09.17.
 */

public class AutoCompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {

    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    /**
     * Current predictions return by this adapter.
     */
    private ArrayList<AutocompletePrediction> predictions;

    /**
     * Handles autocomplete request
     */
    private GoogleApiClient googleApiClient;

    /**
     * Uses to reduce the predictions by the chosen area
     */
    private LatLngBounds latLngBounds;

    /**
     * The autocomplete filter used to restrict queries to a specific set of place types.
     */
    private AutocompleteFilter filter;

    public AutoCompleteAdapter(
            Context context,
            GoogleApiClient googleApiClient,
            LatLngBounds latLngBounds,
            AutocompleteFilter filter) {
        super(context, R.layout.two_line_list_item, R.id.text1);
        this.googleApiClient = googleApiClient;
        this.latLngBounds = latLngBounds;
        this.filter = filter;
    }

    @Override
    public int getCount() {
        return predictions.size();
    }

    @Nullable
    @Override
    public AutocompletePrediction getItem(int position) {
        return predictions.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View row = super.getView(position, convertView, parent);

        AutocompletePrediction item = getItem(position);

        TextView textView1 = (TextView)row.findViewById(R.id.text1);
        TextView textView2 = (TextView)row.findViewById(R.id.text2);

        ImageView imageView = (ImageView)row.findViewById(R.id.powered_by_google_image);

        textView1.setText(item.getPrimaryText(STYLE_BOLD));
        textView2.setText(item.getSecondaryText(STYLE_BOLD));

        if (position == (predictions.size() - 1)){
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }

        return row;
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                // We need a separate list to store the results, since
                // this is run asynchronously
                ArrayList<AutocompletePrediction> autocompletePredictions = new ArrayList<>();

                // Skip the autocomplete query if no constraints are given
                if (constraint != null){
                    autocompletePredictions = getAutocomplete(constraint);
                }

                results.values = autocompletePredictions;
                if (autocompletePredictions != null){
                    results.count = autocompletePredictions.size();
                } else {
                    results.count = 0;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0){
                    predictions = (ArrayList<AutocompletePrediction>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (resultValue instanceof AutocompletePrediction){
                    return ((AutocompletePrediction)resultValue).getFullText(null);
                } else {
                    return super.convertResultToString(resultValue);
                }
            }
        };
    }

    /**
     * Submits an autocomplete query to the Places Geo Data Autocomplete API.
     * Results are returned as frozen AutocompletePrediction objects, ready to be cached.
     * @param constraint is autocomplete query string that a user passes
     * @return predictions from the Autocomplete API or null if the query was not successful.
     */
    private ArrayList<AutocompletePrediction> getAutocomplete (CharSequence constraint) {
        if (googleApiClient.isConnected()){
            PendingResult<AutocompletePredictionBuffer> result =
                    Places.GeoDataApi.
                            getAutocompletePredictions(googleApiClient, constraint.toString(),
                                latLngBounds, filter);

            /**
             * This method should be called of a UI thread.
             * Block and wait for at most 30s for a result from the API
             */
            AutocompletePredictionBuffer autocompletePredictions =
                    result.await(30, TimeUnit.SECONDS);

            Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                Toast.makeText(getContext(),
                        "Error contacting API. " + status.toString(),
                        Toast.LENGTH_SHORT).show();

                /**
                 * According to https://developers.google.com/places/android-api/buffers
                 * buffers should be released if they are no longer needed
                 */
                autocompletePredictions.release();
                return null;
            }

            // Freezes the results immutable, so they could be stored safely
            return DataBufferUtils.freezeAndClose(autocompletePredictions);
        }
        return null;
    }
}
