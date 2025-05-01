package com.example.cloudnd;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ItemFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyItemRecyclerViewAdapter adapter;
    private List<PlaceholderItem> itemList = new ArrayList<>();
    private static final String ARG_STOP_ID = "stop_id";
    private String stopId;
    private SharedPreferences sharedPreferences;
    private String BASE_URL;
    private String API_URL;

    public static ItemFragment newInstance(String stopId) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STOP_ID, stopId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            stopId = getArguments().getString(ARG_STOP_ID);
        }

        sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        BASE_URL = sharedPreferences.getString("base_url", "https://api.com");
        API_URL = BASE_URL + "/api/v1/magic_lookup";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyItemRecyclerViewAdapter(itemList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (itemList.isEmpty()) {
            fetchDataFromServer();
        }
    }

    private void fetchDataFromServer() {
        if (stopId == null || stopId.isEmpty()) {
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = API_URL + "?stop_id=" + stopId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        itemList.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject stopData = response.getJSONObject(i);
                            String arrivalTime = stopData.getString("arrival_time");
                            JSONObject routeInfo = stopData.getJSONObject("route_info");
                            String busName = routeInfo.getString("route_short_name");

                            itemList.add(new PlaceholderItem(busName, arrivalTime));
                        }

                        adapter.notifyDataSetChanged();

                        if (getParentFragment() instanceof StopsBottomSheetDialog) {
                            ((StopsBottomSheetDialog) getParentFragment()).hideLoadingIndicator();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace());

        queue.add(jsonArrayRequest);
    }
}
