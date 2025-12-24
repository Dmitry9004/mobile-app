package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.PriorityQueue

class MainActivity : AppCompatActivity() {
    lateinit var items: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getAll()

        val button = findViewById<Button>(R.id.button_main)
        val button1 = findViewById<Button>(R.id.button_sort_main)
        button.setOnClickListener({clickListener()})
        button1.setOnClickListener({sortList("alp")})

        val button2 = findViewById<Button>(R.id.main_search_button)
        button2.setOnClickListener({search()})
    }

    fun search() {
        val main_input = findViewById<EditText>(R.id.main_input)
        val q = main_input.getText().toString()

        if (q.length > 0) {
            val list = items.names()
            val itemsList = mutableListOf<String>()

            for (i in 0 until list.length()) {
                val name = list.get(i).toString();
                if (name.startsWith(q)) {
                    val nameFirst = name.substring(0, 3)
                    val nameSecond = name.substring(3, 6)
                    var item = items.get(name).toString();
                    itemsList.add(nameFirst + ": " + item + " " + nameSecond)
                }
            }
            val listView = findViewById<ListView>(R.id.list_main)

            val ad = ArrayAdapter(this, R.layout.list_item, itemsList.toTypedArray())
            listView.adapter = ad
        }
    }

    fun clickListener() {
        getAll()
    }

    fun sortList(type: String = "default") {
        val list = findViewById<ListView>(R.id.list_main)
        var names = items.names()
        var reesultList = mutableListOf("")
        reesultList.clear()

        if (type === "default") {
            for (i in 0 until names.length()) {
                val name = names.get(i).toString();
                val nameFirst = name.substring(0, 3)
                val nameSecond = name.substring(3, 6)
                var item = items.get(name).toString();
                reesultList.add(nameFirst + ": " + item + " " + nameSecond)
            }
        } else {
            val comparator = Comparator {str1: String, str2: String -> str2.compareTo(str1)}
            val itemsSorted = PriorityQueue<String>(comparator)
            val names = items.names()

            for ( i in 0 until names.length()) {
                itemsSorted.add(names.get(i).toString())
            }

            while (!itemsSorted.isEmpty()) {
                val name = itemsSorted.poll();
                val nameFirst = name.substring(0, 3)
                val nameSecond = name.substring(3, 6)
                var item = items.get(name).toString();
                reesultList.add(nameFirst + ": " + item + " " + nameSecond)
            }
        }
        val i = reesultList

        val ad = ArrayAdapter(this, R.layout.list_item, reesultList.toTypedArray())
        list.adapter = ad
    }

    fun getAll() {
        val url = "https://currate.ru/api/?get=rates&pairs="
        val key = "679435be7d96dace712cc8e67284763a"
        val types = listOf("BCHEUR", "BCHGBP", "BCHJPY", "BCHRUB", "BCHUSD", "BCHXRP", "BTCBCH", "BTCEUR", "BTCGBP", "BTCJPY", "BTCRUB", "BTCUSD", "BTCXRP", "BTGUSD", "BYNRUB", "CADRUB", "CHFRUB", "CNYEUR", "CNYRUB", "CNYUSD", "ETHEUR", "ETHGBP", "ETHJPY", "ETHRUB", "ETHUSD", "EURAED", "EURAMD", "EURBGN", "EURBYN", "EURGBP", "EURJPY", "EURKZT", "EURRUB", "EURTRY", "EURUSD", "GBPAUD", "GBPBYN", "GBPJPY", "GBPRUB", "GELRUB", "GELUSD", "IDRUSD", "JPYAMD", "JPYAZN", "JPYRUB", "LKREUR", "LKRRUB", "LKRUSD", "MDLEUR", "MDLRUB", "MDLUSD", "MMKEUR", "MMKRUB", "MMKUSD", "RSDEUR", "RSDRUB", "RSDUSD", "RUBAED", "RUBAMD", "RUBAUD", "RUBBGN", "RUBKZT", "RUBMYR", "RUBNZD", "RUBSGD", "RUBTRY", "RUBUAH", "THBCNY", "THBEUR", "THBRUB", "USDAED", "USDAMD", "USDAUD", "USDBGN", "USDBYN", "USDCAD", "USDGBP", "USDILS", "USDJPY", "USDKGS", "USDKZT", "USDMYR", "USDRUB", "USDTHB", "USDUAH");

        val queue = Volley.newRequestQueue(this)

        var urlRes = url;
        for (i in 0 until types.size) {
            urlRes += "${types.get(i)},"
        }
        urlRes.substring(0, urlRes.length-1)
        urlRes += "&key=${key}"

        val req = JsonObjectRequest(Request.Method.GET, urlRes, null, { response ->
            try {
                val arrayTypes = response.getJSONObject("data")
                items = arrayTypes
                sortList()

            } catch (e: Exception) {
            }
        }, { err ->
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT)
        })

        queue.add(req)
    }

}