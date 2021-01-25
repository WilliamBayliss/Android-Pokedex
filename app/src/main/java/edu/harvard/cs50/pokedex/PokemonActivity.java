package edu.harvard.cs50.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.SharedPreferences;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class PokemonActivity extends AppCompatActivity {
    // Pokemon name
    private TextView nameTextView;
    //Pokemon Pokedex #
    private TextView numberTextView;
    //Pokemon types
    private TextView type1TextView;
    private TextView type2TextView;
    //Pokemon Abilities
    private TextView ability1TextView;
    private TextView ability2TextView;
    // Pokemon URL from API
    private String url;
    private RequestQueue requestQueue;
    // Button to catch/release pokemon
    private Button catchButton;
    // Saves whether pokemon has been caught for SharedPreferences
    private boolean pokemonCaught = false;
    // Pokemon sprite
    private ImageView imageView;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        url = getIntent().getStringExtra("url");
        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        type1TextView = findViewById(R.id.pokemon_type1);
        type2TextView = findViewById(R.id.pokemon_type2);
        ability1TextView = findViewById(R.id.pokemon_ability1);
        ability2TextView = findViewById(R.id.pokemon_ability2);
        imageView = findViewById(R.id.pokemon_sprite);
        catchButton = findViewById(R.id.catch_button);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();



        load();
    }


    public void load() {
        type1TextView.setText("");
        type2TextView.setText("");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadSprites(response);
                loadText(response);
                loadTypes(response);
                loadAbilities(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Pokedex", "Pokemon details error", error);
            }
        });

        requestQueue.add(request);

    }
    public void loadText(JSONObject response) {
        try {
            String name = response.getString("name").substring(0, 1).toUpperCase() + response.getString("name").substring(1);

            if (preferences.contains(name)) {
                pokemonCaught = true;
            }
            nameTextView.setText(name);
            catchPokemon();

            numberTextView.setText(String.format("#%03d", response.getInt("id")));
        } catch (JSONException e) {
            Log.e("Pokedex", "Pokemon Text Error", e);
        }
    }

    public void loadAbilities(JSONObject response) {
        try {
            JSONArray abilityEntries = response.getJSONArray("abilities");
            for (int i = 0; i < abilityEntries.length(); i++) {
                JSONObject abilityEntry = abilityEntries.getJSONObject(i);
                int slot = abilityEntry.getInt("slot");
                String ability = abilityEntry.getJSONObject("ability").getString("name");
                if (slot == 1) {
                    String pokeAbility1 = ability.substring(0, 1).toUpperCase() + ability.substring(1);
                    ability1TextView.setText(pokeAbility1);
                } else if (slot == 2) {
                    String pokeAbility2 = ability.substring(0, 1).toUpperCase() + ability.substring(1);
                    ability2TextView.setText(pokeAbility2);
                }
            }

        } catch (JSONException e) {
            Log.e("Pokedex", "Pokemon Abilities Error", e);
        }
    }
    public void loadTypes(JSONObject response) {
        try {
            JSONArray typeEntries = response.getJSONArray("types");
            for (int i = 0; i < typeEntries.length(); i++) {
                JSONObject typeEntry = typeEntries.getJSONObject(i);
                int slot = typeEntry.getInt("slot");
                String type = typeEntry.getJSONObject("type").getString("name");
                if (slot == 1) {
                    String poke_type1 = type.toUpperCase();
                    type1TextView.setText(poke_type1);
                } else if (slot == 2) {
                    String poke_type2 = type.toUpperCase();
                    type2TextView.setText(poke_type2);
                }
            }
        } catch (JSONException e) {
            Log.e("Pokedex", "Pokemon Types Error", e);
        }
    }

    public void loadSprites(JSONObject response) {
        try {
                String imgUrl = response.getJSONObject("sprites").getString("front_default");
                new DownloadSpriteTask().execute(imgUrl);

        } catch (JSONException e) {
            Log.e("Pokedex", "Pokemon Sprite Error");
        }
    }

    private class DownloadSpriteTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                return BitmapFactory.decodeStream(url.openStream());
            } catch (IOException e) {
                Log.e("Pokedex", "Download SpriteError", e);
                return null;
            }
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //Load bitmap into ImageView
            imageView.setImageBitmap(bitmap);
        }
    }


    public void toggleCatch(View view) {
        // gotta catch 'em all!
        pokemonCaught = !pokemonCaught;
        catchPokemon();
        editor.apply();
        }

    public void catchPokemon() {
        String name = nameTextView.getText().toString();
        if (pokemonCaught) {
            catchButton.setText("Release");
            editor.putBoolean(name, true);
        } else {
            catchButton.setText("Catch");
            editor.remove(name);
        }
    }



}

