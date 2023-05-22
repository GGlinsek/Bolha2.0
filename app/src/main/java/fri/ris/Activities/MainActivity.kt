package fri.ris.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import fri.ris.DatabaseHandler
import fri.ris.ItemDialog
import fri.ris.ModelClasses.ItemModelClass
import fri.ris.R

class MainActivity : AppCompatActivity(), ItemDialog.ItemDialogListener {

    val databaseHandler: DatabaseHandler = DatabaseHandler(this)
    val ids: MutableList<Int> = mutableListOf()
    val kolicine: MutableList<Int> = mutableListOf()
    val oboje: MutableList<Pair<Int, Int>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnkosarica = findViewById<ImageButton>(R.id.btn_kosarica)

        addItemList()


        val isItemsEmpty = databaseHandler.isItemsTableEmpty()

        if (isItemsEmpty) {
            databaseHandler.addItemEntries()
        }


        btnkosarica.setOnClickListener {
            val intent  = Intent(this, KosaricaActivity::class.java)
            intent.putIntegerArrayListExtra("IDS", ArrayList(ids))
            intent.putIntegerArrayListExtra("KOLICINE", ArrayList(kolicine))
            startActivity(intent)
        }


    }

    fun addItemList() {
        val itemList = databaseHandler.allItems()


        for (item in itemList){
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val listItemView = inflater.inflate(R.layout.item_list, null)
            val itemName = listItemView.findViewById<TextView>(R.id.tv_list_item_name)
            val itemImage = listItemView.findViewById<ImageView>(R.id.img_list)
            val itemDesc = listItemView.findViewById<EditText>(R.id.et_list_desc)

            itemName.text = item.name
            itemDesc.setText(item.desc)

            val resourceName = item.image.substringAfterLast("/")
                .substringBeforeLast(".")
            val resourceId = resources.getIdentifier(resourceName, "drawable", this.packageName)

            itemImage.setImageResource(resourceId)

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            val layout = findViewById<LinearLayout>(R.id.layout_items)
            layout.addView(listItemView, layoutParams)

            listItemView.setOnClickListener{
                val dialog = ItemDialog()
                val b = Bundle()
                b.putString("NAME",item.name)
                b.putString("DESC",item.desc)
                b.putDouble("PRICE",item.price)
                b.putString("IMAGE",item.image)
                b.putInt("ID", item.id)
                dialog.arguments = b

                dialog.show(supportFragmentManager,"CustomDialog")
            }

        }



    }

    override fun onItemDataProvided(item_id: Int, Stock: Int) {
        kolicine.add(Stock)
        ids.add(item_id)
    }
}