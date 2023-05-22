package fri.ris

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import java.io.File

class ItemDialog: DialogFragment() {
    private var listener: ItemDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.item_dialog, container, false)

        val tvItemName = rootView.findViewById<TextView>(R.id.tv_item_name)
        val etDesc = rootView.findViewById<EditText>(R.id.tv_item_desc)
        val tvPrice = rootView.findViewById<TextView>(R.id.tv_price)
        val imageView = rootView.findViewById<ImageView>(R.id.img_item)
        val btnDismiss = rootView.findViewById<Button>(R.id.btn_preklici)
        val btnVKosarico = rootView.findViewById<Button>(R.id.btn_v_kosarico)
        val kolicina = rootView.findViewById<TextView>(R.id.tv_kolicina)
        val btnPlus = rootView.findViewById<ImageButton>(R.id.img_plus)
        val btnMinus = rootView.findViewById<ImageButton>(R.id.img_minus)


        tvItemName.text = arguments?.getString("NAME")
        etDesc.setText(arguments?.getString("DESC"))
        arguments?.getDouble("PRICE")?.let { Log.e("test", it.toString()) }
        tvPrice.text = "${arguments?.getDouble("PRICE").toString()}€"
        val resourceName = arguments?.getString("IMAGE")?.substringAfterLast("/")
            ?.substringBeforeLast(".")
        val resourceId = resources.getIdentifier(resourceName, "drawable", requireContext().packageName)

        imageView.setImageResource(resourceId)


        btnDismiss.setOnClickListener {
            dismiss()
        }

        btnVKosarico.setOnClickListener {
            arguments?.getInt("ID")?.let { it1 -> listener?.onItemDataProvided(it1,kolicina.text.toString().toInt()) }
            dismiss()
        }

        btnMinus.setOnClickListener {
            kolicina.text = (kolicina.text.toString().toInt() -1).toString()
        }

        btnPlus.setOnClickListener {
            kolicina.text = (kolicina.text.toString().toInt() +1).toString()
        }





        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ItemDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement ItemDialogListener")
        }
    }

    interface ItemDialogListener {
        fun onItemDataProvided(item_id: Int, Stock: Int)
    }
}