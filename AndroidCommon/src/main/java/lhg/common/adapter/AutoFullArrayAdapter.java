package lhg.common.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.ArrayRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoFullArrayAdapter<T> extends BaseAdapter implements Filterable {
    private final LayoutInflater mInflater;

    private final Context mContext;
    private final int mResource;
    private int mDropDownResource;
    private List<T> mObjects;
    private int mFieldId = 0;
    private List<T> mOriginalValues;
    private ArrayFilter mFilter;
    private String matchPrefix = "@";//停止自动补全的标志字符串

    public void setMatchPrefix(String matchPrefix) {
        this.matchPrefix = matchPrefix;
    }

    public AutoFullArrayAdapter(@NonNull Context context, @LayoutRes int resource) {
        this(context, resource, 0, new ArrayList<>());
    }


    public AutoFullArrayAdapter(@NonNull Context context, @LayoutRes int resource,
                        @IdRes int textViewResourceId) {
        this(context, resource, textViewResourceId, new ArrayList<>());
    }


    public AutoFullArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull T[] objects) {
        this(context, resource, 0, Arrays.asList(objects));
    }

    public AutoFullArrayAdapter(@NonNull Context context, @LayoutRes int resource,
                        @IdRes int textViewResourceId, @NonNull T[] objects) {
        this(context, resource, textViewResourceId, Arrays.asList(objects));
    }


    public AutoFullArrayAdapter(@NonNull Context context, @LayoutRes int resource,
                        @NonNull List<T> objects) {
        this(context, resource, 0, objects);
    }


    public AutoFullArrayAdapter(@NonNull Context context, @LayoutRes int resource,
                        @IdRes int textViewResourceId, @NonNull List<T> objects) {
        this(context, resource, textViewResourceId, objects, false);
    }

    private AutoFullArrayAdapter(@NonNull Context context, @LayoutRes int resource,
                         @IdRes int textViewResourceId, @NonNull List<T> objects, boolean objsFromResources) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = mDropDownResource = resource;
        mObjects = objects;
        mOriginalValues = objects;
        mFieldId = textViewResourceId;
    }

    public @NonNull Context getContext() {
        return mContext;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public @Nullable
    T getItem(int position) {
        return mObjects.get(position);
    }


    public int getPosition(@Nullable T item) {
        return mObjects.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView,
                                 @NonNull ViewGroup parent) {
        return createViewFromResource(mInflater, position, convertView, parent, mResource);
    }

    private @NonNull View createViewFromResource(@NonNull LayoutInflater inflater, int position,
                                                 @Nullable View convertView, @NonNull ViewGroup parent, int resource) {
        final View view;
        final TextView text;

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        try {
            if (mFieldId == 0) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                text = (TextView) view;
            } else {
                //  Otherwise, find the TextView field within the layout
                text = view.findViewById(mFieldId);

                if (text == null) {
                    throw new RuntimeException("Failed to find view with ID "
                            + mContext.getResources().getResourceName(mFieldId)
                            + " in item layout");
                }
            }
        } catch (ClassCastException e) {
            Log.e("AutoFullArrayAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "AutoFullArrayAdapter requires the resource ID to be a TextView", e);
        }

        final T item = getItem(position);
        if (item instanceof CharSequence) {
            text.setText((CharSequence) item);
        } else {
            text.setText(item.toString());
        }

        return view;
    }


    public void setDropDownViewResource(@LayoutRes int resource) {
        this.mDropDownResource = resource;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        final LayoutInflater inflater = mInflater;
        return createViewFromResource(inflater, position, convertView, parent, mDropDownResource);
    }

    /**
     * Creates a new AutoFullArrayAdapter from external resources. The content of the array is
     * obtained through {@link android.content.res.Resources#getTextArray(int)}.
     *
     * @param context The application's environment.
     * @param textArrayResId The identifier of the array to use as the data source.
     * @param textViewResId The identifier of the layout used to create views.
     *
     * @return An AutoFullArrayAdapter<CharSequence>.
     */
    public static @NonNull AutoFullArrayAdapter<CharSequence> createFromResource(@NonNull Context context,
                                                                                 @ArrayRes int textArrayResId, @LayoutRes int textViewResId) {
        final CharSequence[] strings = context.getResources().getTextArray(textArrayResId);
        return new AutoFullArrayAdapter<>(context, textViewResId, 0, Arrays.asList(strings), true);
    }

    @Override
    public @NonNull
    Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }


    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence __prefix) {
            FilterResults results = new FilterResults();
            if (__prefix == null || __prefix.length() == 0) {
                return results;
            }
            String prefix = __prefix.toString();
            ArrayList<String> newData = new ArrayList<>();
            if (TextUtils.isEmpty(matchPrefix)) {
                for(T item : mOriginalValues){
                    String itemText = item.toString();
                    if (itemText.startsWith(prefix)) {
                        newData.add(prefix);
                    }
                }
            } else if(prefix.endsWith(matchPrefix)){
                prefix = prefix.substring(0, prefix.length() - matchPrefix.length());
                for(T item : mOriginalValues){
                    newData.add(prefix+item.toString());
                }
            } else if(!prefix.contains(matchPrefix)) {
                for (T item : mOriginalValues) {
                    newData.add(prefix + item.toString());
                }
            } else {
                int i = prefix.lastIndexOf(matchPrefix);
                if (i >= 0) {
                    String temp = prefix.substring(i);
                    for(T item : mOriginalValues){
                        String itemText = item.toString();
                        if (itemText.startsWith(temp)) {
                            newData.add(prefix.substring(0, i) +itemText);
                        }
                    }
                }
            }
            results.values = newData;
            results.count = newData.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<T>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
