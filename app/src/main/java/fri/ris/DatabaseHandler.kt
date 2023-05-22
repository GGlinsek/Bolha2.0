package fri.ris

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import fri.ris.ModelClasses.ItemModelClass
import fri.ris.ModelClasses.UserModelClass

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private const val DATABASE_VERSION = 11
        private const val DATABASE_NAME = "Bolha2Database"
        private const val TABLE_ITEMS = "ITEMS"
        private const val TABLE_USERS = "USERS"
        private const val TABLE_PROMOCODES = "PROMOCODES"
        private const val TABLE_CREDIT_CARDS = "CREDIT_CARDS"

        private const val KEY_USER_ID = "USER_ID"
        private const val KEY_EMAIL = "EMAIL"
        private const val KEY_USERNAME = "USERNAME"
        private const val KEY_PASS = "PASS"
        private const val KEY_PROFILE_IMAGE = "PROFILE_IMAGE"
        private const val KEY_IS_ADMIN = "IS_ADMIN"

        private const val KEY_ITEM_ID = "ITEM_ID"
        private const val KEY_ITEM_NAME = "NAME"
        private const val KEY_ITEM_DESC = "DESC"
        private const val KEY_ITEM_IMAGE = "ITEM_IMAGE"
        private const val KEY_ITEM_PRICE = "PRICE"
        private const val KEY_ITEM_STOCK = "STOCK"

        private const val KEY_PROMO_ID = "PROMO_ID"
        private const val KEY_PROMO_TAG = "TAG"
        private const val KEY_IS_VALID = "IS_VALID"

        private const val KEY_CC_NUMBER = "NUMBER"
        private const val KEY_CC_CCV = "CCV"
        private const val KEY_CC_BALANCE = "BALANCE"





    }

    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)

    }



    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val selectQuery = "DROP TABLE IF EXISTS $TABLE_ITEMS"

        val selectQuery1 = "DROP TABLE IF EXISTS $TABLE_USERS"

        val selectQuery2 = "DROP TABLE IF EXISTS $TABLE_CREDIT_CARDS"
        val selectQuery3 = "DROP TABLE IF EXISTS $TABLE_PROMOCODES"


        db.execSQL(selectQuery)
        db.execSQL(selectQuery1)
        db.execSQL(selectQuery2)
        db.execSQL(selectQuery3)

        onCreate(db)
    }

    private fun createTables(db: SQLiteDatabase) {
        val CREATE_USERS_TABLE = ("CREATE TABLE " + TABLE_USERS + " (" + KEY_USER_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_EMAIL + " TEXT,"+ KEY_USERNAME +" TEXT,"
                + KEY_PASS +" TEXT,"+ KEY_IS_ADMIN + " BOOLEAN," + KEY_PROFILE_IMAGE +" TEXT)")

        val CREATE_ITEMS_TABLE = ("CREATE TABLE " + TABLE_ITEMS + " (" + KEY_ITEM_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ITEM_NAME + " TEXT," + KEY_ITEM_DESC
                + " TEXT," + KEY_ITEM_PRICE + " NUMERIC," + KEY_ITEM_STOCK + " INTEGER," + KEY_ITEM_IMAGE + " TEXT)")

        val CREATE_CREDIT_CARDS_TABLE = ("CREATE TABLE " + TABLE_CREDIT_CARDS + " (" + KEY_CC_NUMBER
                + " TEXT PRIMARY KEY," + KEY_CC_CCV + " INTEGER," + KEY_CC_BALANCE + " NUMERIC)")

        val CREATE_PROMOCODES_TABLE = ("CREATE TABLE " + TABLE_PROMOCODES + " (" + KEY_PROMO_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_PROMO_TAG + " TEXT," + KEY_IS_VALID + " BOOLEAN)")


        db.execSQL(CREATE_USERS_TABLE)
        db.execSQL(CREATE_ITEMS_TABLE)
        db.execSQL(CREATE_CREDIT_CARDS_TABLE)
        db.execSQL(CREATE_PROMOCODES_TABLE)
    }

    fun registerUser(user: UserModelClass) : Long {
        val db = this.writableDatabase
        val contentValues = ContentValues ()

        contentValues.put(KEY_EMAIL, user.email)
        contentValues.put(KEY_USERNAME, user.username)
        contentValues.put(KEY_PASS, user.pass)
        contentValues.put(KEY_PROFILE_IMAGE, user.image)
        contentValues.put(KEY_IS_ADMIN, user.is_admin)

        val success = db.insert(TABLE_USERS, null, contentValues)

        db.close()
        return success
    }



    fun loginUser(username: String, password: String) : UserModelClass {
        val db = this.readableDatabase
        val sql = "SELECT * FROM $TABLE_USERS WHERE $KEY_USERNAME = '$username'"

        var user = UserModelClass(0,"","","",false,"")
        val cursor = db.rawQuery(sql, null)

        if (cursor != null && cursor.moveToFirst()){
            do {
                val idIndex = cursor.getColumnIndex(KEY_USER_ID)
                val usernameIndex = cursor.getColumnIndex(KEY_USERNAME)
                val mailIndex = cursor.getColumnIndex(KEY_EMAIL)
                val passwordIndex = cursor.getColumnIndex(KEY_PASS)
                val imageIndex = cursor.getColumnIndex(KEY_PROFILE_IMAGE)
                val adminIndex = cursor.getColumnIndex(KEY_IS_ADMIN)

                if (username == cursor.getString(usernameIndex)){
                    if (password == cursor.getString(passwordIndex)){
                        val id = if (idIndex != -1) cursor.getInt(idIndex) else -1
                        val username = if (usernameIndex != -1) cursor.getString(usernameIndex) else ""
                        val image = if (imageIndex != -1) cursor.getString(imageIndex) else ""

                        user = UserModelClass(id = 1, email = "", username = username, pass = "", is_admin = false, image = image)
                    }
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return user
    }

    fun addItem (item: ItemModelClass) : Long {
        val db = this.writableDatabase
        val contentValues = ContentValues ()

        contentValues.put(KEY_ITEM_NAME, item.name)
        contentValues.put(KEY_ITEM_DESC, item.desc)
        contentValues.put(KEY_ITEM_PRICE, item.price)
        contentValues.put(KEY_ITEM_STOCK, item.stock)
        contentValues.put(KEY_ITEM_IMAGE, item.image)

        val success = db.insert(TABLE_ITEMS, null, contentValues)

        db.close()
        return success

    }

    fun isItemsTableEmpty(): Boolean {
        val db = this.readableDatabase
        val selectQuery = "SELECT COUNT(*) FROM $TABLE_ITEMS"
        val cursor = db.rawQuery(selectQuery, null)

        var count = 0
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor?.close()
        db.close()

        return count == 0
    }

    fun updateStock(item: ItemModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ITEM_STOCK, item.stock)

        return db.update(TABLE_ITEMS, contentValues, "$KEY_ITEM_ID = ${item.id}", null)
    }

    fun update (id: Int, stock: Int) {

        val db = this.writableDatabase

        val sql = "UPDATE $TABLE_ITEMS SET $KEY_ITEM_STOCK = $stock" +
                "WHERE $KEY_ITEM_ID = $id"

        db.execSQL(sql)
        db.close()
    }






    fun deleteRows() {
        //val selectQuery = "DELETE FROM $TABLE_CONTACTS"
        val db = this.writableDatabase


    }


    fun addItemEntries() {
        registerUser(UserModelClass(0,"admin@gmail.com","admin","admin",true,""))
        addItem(ItemModelClass(0, "Laptop", "Prenosni računalnik z visokimi zmogljivostmi", 999.99, "@drawable/laptop", 10))
        addItem(ItemModelClass(0, "Knjiga", "Najnovejši roman priznanega avtorja", 19.99, "@drawable/knjiga", 30))
        addItem(ItemModelClass(0, "Potovalna torba", "Prostorna torba za potovanja", 49.99, "@drawable/potovalna_torba", 5))
        addItem(ItemModelClass(0, "Pametna ura", "Elegantna pametna ura z različnimi funkcijami", 149.99, "@drawable/pametna_ura", 15))
        addItem(ItemModelClass(0, "Fotoaparat", "Visokokakovosten digitalni fotoaparat", 399.99, "@drawable/fotoaparat", 3))
        addItem(ItemModelClass(0, "Kopalna brisača", "Velika kopalna brisača za plažo", 24.99, "@drawable/kopalna_brisaca", 20))
        addItem(ItemModelClass(0, "Fitnes žoga", "Fitnes žoga za vadbo in krepitev mišic", 12.99, "@drawable/fitnes_zoga", 8))
        addItem(ItemModelClass(0, "Slušalke", "Brezžične slušalke s tehnologijo odpravljanja hrupa", 79.99, "@drawable/slusalke", 12))
        addItem(ItemModelClass(0, "Očala za sončenje", "Modna očala za sončenje s polariziranimi stekli", 39.99, "@drawable/ocala_sonce", 25))
        addItem(ItemModelClass(0, "Vrtalni stroj", "Močan vrtalni stroj za domače popravilo", 89.99, "@drawable/vrtalni_stroj", 6))
        addItem(ItemModelClass(0, "Vakuumski čistilec", "Robotski sesalnik za avtomatsko čiščenje", 299.99, "@drawable/vakuumski_cistilec", 8))
        addItem(ItemModelClass(0, "Vesla", "Kakovostna vesla za veslanje na mirnih vodah", 199.99, "@drawable/vesla", 10))
        addItem(ItemModelClass(0, "Pisarniški stol", "Ergonomski pisarniški stol za udobno delo", 149.99, "@drawable/pisarniski_stol", 5))
        addItem(ItemModelClass(0, "Glasbena tipkovnica", "Elektronska tipkovnica s številnimi zvočnimi efekti", 299.99, "@drawable/tipkovnica", 2))
        addItem(ItemModelClass(0, "Kolesarska čelada", "Varnostna čelada za kolesarjenje po mestu", 39.99, "@drawable/celada", 20))
        addItem(ItemModelClass(0, "Brezžični zvočnik", "Kompakten brezžični zvočnik za poslušanje glasbe", 59.99, "@drawable/zvocnik", 15))
        addItem(ItemModelClass(0, "Fitnes tracker", "Naprava za sledenje aktivnosti in merjenje srčnega utripa", 79.99, "@drawable/fitnes_tracker", 10))
        addItem(ItemModelClass(0, "Vedro", "Veliko vedro za prenašanje in shranjevanje vode", 9.99, "@drawable/vedro", 30))
        addItem(ItemModelClass(0, "Teniški lopar", "Profesionalni teniški lopar za napredne igralce", 89.99, "@drawable/teniski_lopar", 4))

    }



    fun fillCredit() {
        val db = this.writableDatabase

        val sql = "INSERT INTO $TABLE_CREDIT_CARDS ($KEY_CC_NUMBER,$KEY_CC_CCV,$KEY_CC_BALANCE) VALUES ('1234123412341234', '123', 1000000.0)"
        db.execSQL(sql)
    }


    fun viewItem(i: Int): ItemModelClass {

        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_ITEMS WHERE $KEY_ITEM_ID = $i"

        val cursor = db.rawQuery(selectQuery, null)
        var item = ItemModelClass(0,"","",0.0,"",0)

        if (cursor != null && cursor.moveToFirst()){
            do {
                val idIndex = cursor.getColumnIndex(KEY_ITEM_ID)
                val nameIndex = cursor.getColumnIndex(KEY_ITEM_NAME)
                val descIndex = cursor.getColumnIndex(KEY_ITEM_DESC)
                val priceIndex = cursor.getColumnIndex(KEY_ITEM_PRICE)
                val imageIndex = cursor.getColumnIndex(KEY_ITEM_IMAGE)
                val stockIndex = cursor.getColumnIndex(KEY_ITEM_STOCK)

                val id = if (idIndex != -1) cursor.getInt(idIndex) else -1
                val name = if (nameIndex != -1) cursor.getString(nameIndex) else ""
                val desc = if (descIndex != -1) cursor.getString(descIndex) else ""
                val price = if (priceIndex != -1) cursor.getDouble(priceIndex) else -1.0
                val image = if (imageIndex != -1) cursor.getString(imageIndex) else ""
                val stock = if (stockIndex != -1) cursor.getInt(stockIndex) else -1

                item = ItemModelClass(id = id, name = name, desc = desc, price = price, image = image, stock = stock)
            } while (cursor.moveToNext())
        }


        cursor?.close()
        db.close()
        return item
    }

    fun viewSelectedItems(list: List<Int>) : ArrayList<ItemModelClass> {
        val idList = list.joinToString(",")
        val itemList: ArrayList<ItemModelClass> = ArrayList()

        val db = this.readableDatabase

        val sql = "SELECT * FROM $TABLE_ITEMS WHERE $KEY_ITEM_ID IN ($idList)"

        val cursor = db.rawQuery(sql, null)

        if (cursor != null && cursor.moveToFirst()){
            do {
                val idIndex = cursor.getColumnIndex(KEY_ITEM_ID)
                val nameIndex = cursor.getColumnIndex(KEY_ITEM_NAME)
                val descIndex = cursor.getColumnIndex(KEY_ITEM_DESC)
                val priceIndex = cursor.getColumnIndex(KEY_ITEM_PRICE)
                val imageIndex = cursor.getColumnIndex(KEY_ITEM_IMAGE)
                val stockIndex = cursor.getColumnIndex(KEY_ITEM_STOCK)

                val id = if (idIndex != -1) cursor.getInt(idIndex) else -1
                val name = if (nameIndex != -1) cursor.getString(nameIndex) else ""
                val desc = if (descIndex != -1) cursor.getString(descIndex) else ""
                val price = if (priceIndex != -1) cursor.getDouble(priceIndex) else -1.0
                val image = if (imageIndex != -1) cursor.getString(imageIndex) else ""
                val stock = if (stockIndex != -1) cursor.getInt(stockIndex) else -1

                val item = ItemModelClass(id = id, name = name, desc = desc, price = price, image = image, stock = stock)
                itemList.add(item)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        db.close()

        return itemList
    }

    fun allItems(): ArrayList<ItemModelClass> {
        val itemList: ArrayList<ItemModelClass> = ArrayList()

        val sql = "SELECT * FROM $TABLE_ITEMS"

        val db = this.readableDatabase
        val cursor = db.rawQuery(sql, null)

        if (cursor != null && cursor.moveToFirst()){
            do {
                val idIndex = cursor.getColumnIndex(KEY_ITEM_ID)
                val nameIndex = cursor.getColumnIndex(KEY_ITEM_NAME)
                val descIndex = cursor.getColumnIndex(KEY_ITEM_DESC)
                val priceIndex = cursor.getColumnIndex(KEY_ITEM_PRICE)
                val imageIndex = cursor.getColumnIndex(KEY_ITEM_IMAGE)
                val stockIndex = cursor.getColumnIndex(KEY_ITEM_STOCK)

                val id = if (idIndex != -1) cursor.getInt(idIndex) else -1
                val name = if (nameIndex != -1) cursor.getString(nameIndex) else ""
                val desc = if (descIndex != -1) cursor.getString(descIndex) else ""
                val price = if (priceIndex != -1) cursor.getDouble(priceIndex) else -1.0
                val image = if (imageIndex != -1) cursor.getString(imageIndex) else ""
                val stock = if (stockIndex != -1) cursor.getInt(stockIndex) else -1

                val item = ItemModelClass(id = id, name = name, desc = desc, price = price, image = image, stock = stock)
                itemList.add(item)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        db.close()
        return itemList
    }

}

