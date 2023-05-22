package fri.ris.Activities

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import fri.ris.DatabaseHandler
import fri.ris.ItemDialog
import fri.ris.R
import java.text.DecimalFormat
import java.util.ArrayList

class KosaricaActivity: AppCompatActivity() {


    companion object {
        var skupaj: Double = 0.0
            set(value) {
                field = value
                // Call the callback function here
                onSkupajChanged()
            }

        private var skupajChangedCallback: (() -> Unit)? = null

        fun setSkupajChangedCallback(callback: () -> Unit) {
            skupajChangedCallback = callback
        }

        private fun onSkupajChanged() {
            skupajChangedCallback?.invoke()
        }
    }


    private val databaseHandler: DatabaseHandler = DatabaseHandler(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kosarica)
        val receivedIds = intent.getIntegerArrayListExtra("IDS")
        val receivedKolicine = intent.getIntegerArrayListExtra("KOLICINE")
        val tvSkupaj = findViewById<TextView>(R.id.tv_skupaj)
        skupaj = 0.0
        addItemList(receivedIds, receivedKolicine)


        setSkupajChangedCallback {
            tvSkupaj.text = "SKUPAJ: ${formatNumberWithDecimalPlaces(skupaj, 2)}€"
        }

        skupaj = skupaj
    }

    fun formatNumberWithDecimalPlaces(number: Double, decimalPlaces: Int): String {
        val decimalFormat = DecimalFormat("#." + "#".repeat(decimalPlaces))
        return decimalFormat.format(number)
    }

    fun addItemList(receivedIds: ArrayList<Int>?, kolicine: ArrayList<Int>?) {
        val itemList = receivedIds?.let { databaseHandler.viewSelectedItems(it) }


        if (itemList != null) {
            var i = 0
            for (item in itemList){
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val listItemView = inflater.inflate(R.layout.item_kosarica, null)
                val itemName = listItemView.findViewById<TextView>(R.id.tv_kosarica_name)
                val itemImage = listItemView.findViewById<ImageView>(R.id.img_kosarica)
                val kolicinaText = listItemView.findViewById<TextView>(R.id.tv_kosarica_kolicina)
                val btnPlus = listItemView.findViewById<ImageButton>(R.id.btn_kosarica_plus)
                val btnMinus = listItemView.findViewById<ImageButton>(R.id.btn_kosarica_minus)

                itemName.text = item.name
                kolicinaText.text = kolicine?.get(i)?.toString()
                skupaj += kolicinaText.text.toString().toInt() * item.price
                i++

                val resourceName = item.image.substringAfterLast("/")
                    .substringBeforeLast(".")
                val resourceId = resources.getIdentifier(resourceName, "drawable", this.packageName)

                itemImage.setImageResource(resourceId)

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                val layout = findViewById<LinearLayout>(R.id.layout_kosarica)
                layout.addView(listItemView, layoutParams)

                btnPlus.setOnClickListener {
                    kolicinaText.text = (kolicinaText.text.toString().toInt() +1).toString()
                    skupaj += item.price
                }

                btnMinus.setOnClickListener {
                    kolicinaText.text = (kolicinaText.text.toString().toInt() -1).toString()
                    skupaj -= item.price
                }

            }
        }



    }
}