package com.example.burakcan.fe;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.burakcan.fe.Common.Common;
import com.example.burakcan.fe.Database.Database;
import com.example.burakcan.fe.Interface.ItemClickListener;
import com.example.burakcan.fe.Model.Food;
import com.example.burakcan.fe.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference foodList;
    String katagoriId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    //searchbar
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    Database localDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Firebase bagla

        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Food");
        //sql db
        localDb =  new Database(this);

        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //inteni alicamiz yer

        if (getIntent() != null)
            katagoriId = getIntent().getStringExtra("KatagoriId");
        if (!katagoriId.isEmpty()) {

            loadListFood(katagoriId);

        }
        //search
        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Size nasil yardimci olabilirim?");
      //  materialSearchBar.setSpeechMode(false);
        loadSuggest();

        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for (String search:suggestList)
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        Query listSearch = foodList.orderByChild("name").equalTo(text.toString());
        FirebaseRecyclerOptions<Food> options2 = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(listSearch,Food.class).build();

                searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options2) {
                    @Override
                    protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                        holder.food_name.setText(model.getName());
                        Picasso.get().load(model.getImage()).into(holder.food_image);

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                // katagori id sinde istek gonder
                                Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                                foodDetail.putExtra("FoodId",searchAdapter.getRef(position).getKey());
                                startActivity(foodDetail);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
                        return new FoodViewHolder(itemView);
                    }
                };
        searchAdapter.startListening();


        searchAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(searchAdapter);

    }



    private void loadSuggest() {

        foodList.orderByChild("MenuId").equalTo(katagoriId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                        {
                            Food item = postSnapShot.getValue(Food.class);
                            suggestList.add(item.getName());// menu adini ekliyoruz
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadListFood(katagoriId);

    }

    private void loadListFood(String katagoriId) {
        Query listFoodByKatagoriId = foodList.orderByChild("menuId").equalTo(katagoriId);
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(listFoodByKatagoriId,Food.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder holder, final int position, @NonNull final Food model) {
                holder.food_name.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.food_image);

                if (localDb.isFavorites(adapter.getRef(position).getKey()))
                    holder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                //click to change
                holder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!localDb.isFavorites(adapter.getRef(position).getKey()))
                        {
                            localDb.addtoFovorites(adapter.getRef(position).getKey());
                            holder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this,""+model.getName()+" Favorilere eklendi",Toast.LENGTH_SHORT).show();
                        }else
                        {
                            localDb.removeFovorites(adapter.getRef(position).getKey());
                            holder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this,""+model.getName()+" Favorilere cikarildi",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // katagori id sinde istek gonder
                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }


            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(itemView);
            }
        };
        adapter.startListening();


        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();

    }
}
