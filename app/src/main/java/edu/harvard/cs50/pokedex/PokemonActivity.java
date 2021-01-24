package edu.harvard.cs50.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PokemonActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView numberTextView;
    private TextView type1TextView;
    private TextView type2TextView;
    private TextView ability1TextView;
    private TextView ability2TextView;
    private String url;
    private RequestQueue requestQueue;
    private Button catchButton;
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
        catchButton = findViewById(R.id.catch_button);

        load();
    }

    public void load() {
        type1TextView.setText("");
        type2TextView.setText("");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    nameTextView.setText(response.getString("name"));
                    numberTextView.setText(String.format("#%03d", response.getInt("id")));

                    JSONArray abilityEntries = response.getJSONArray("abilities");
                    for (int i = 0; i < abilityEntries.length(); i++) {
                        JSONObject abilityEntry = abilityEntries.getJSONObject(i);
                        int slot = abilityEntry.getInt("slot");
                        String ability = abilityEntry.getJSONObject("ability").getString("name");

                        if (slot == 1) {
                            ability1TextView.setText(ability);
                        }
                        else if (slot == 2) {
                            ability2TextView.setText(ability);
                        }
                    }

                    JSONArray typeEntries = response.getJSONArray("types");
                    for (int i = 0; i < typeEntries.length(); i++) {
                        JSONObject typeEntry = typeEntries.getJSONObject(i);
                        int slot = typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");

                        if (slot == 1) {
                            type1TextView.setText(type);
                        }
                        else if (slot == 2) {
                            type2TextView.setText(type);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("cs50", "Pokemon json error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon details error", error);
            }
        });

        requestQueue.add(request);
    }
    public boolean pokemonCaught = false;
    public void toggleCatch(View view) {
        // gotta catch 'em all!rue;
         if (!pokemonCaught) {
             pokemonCaught = true;
             catchButton.setText("Release");
         } else {
          pokemonCaught = false;
          catchButton.setText("Catch");
         }
    }
}
