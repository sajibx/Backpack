package com.example.demo

import com.fasterxml.jackson.databind.util.JSONPObject
import org.apache.catalina.User
import org.apache.catalina.UserDatabase
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.web.bind.annotation.*
import java.awt.Image
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.xml.crypto.Data
import kotlin.collections.ArrayList
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json
import java.io.Reader
import java.time.LocalDateTime


@Entity
class Users(@Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long? = null, var fullname: String? = null, var phonenumber: String? = null, var password: String? = null, var address: String? = null,  var userType: String? = null, var state: Int = 0, var token:String? = null)

@Entity
class Products(@Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long? = null, var product_url: String? = null, var product_price: String? = null, var product_status: String? = null, var productprice: String? = null, var product_buyer: String? = null, var buyer_name: String? = null, var buyer_number: Long? = null)



class LogingClass(val username: String? = null, val password: String? = null)

interface UsersRepository : JpaRepository<Users, Long> {

    fun getUsersByPhonenumberAndPassword(phonenumber: String?, password: String?): Users

    @Query("select  p from Users p where p.phonenumber  = :phone")
    fun getByPhone(@Param("phone") phone: String?): Users

    @Query("select  p from Users p where p.token  = :phone")
    fun getByToken(@Param("phone") phone: String?): Users
}


interface ProductRepository : JpaRepository<Products, Long> {

    @Query("select  p from Products  p where p.productprice  >?1")
    fun getAllByProductpriceLessThan(productprice: Int?): MutableList<Products>

    @Query("select ps from Products ps where ps.product_buyer = ?1 and ps.product_status = ?2")
    fun getByKey(key_data: String?, key_data1: String?): MutableList<Products>

    @Query("select pr from Products pr where pr.product_url = ?1 or pr.buyer_name = ?2")
    fun getByKey2(key_data: String?) : MutableList<Products>

    @Query("select ps from Products ps where  ps.product_status = ?1")
    fun getByKey3(key_data: String?): MutableList<Products>

    @Query("select ps from Products ps where ps.productprice = ?1 and ps.product_status = ?2")
    fun getByKey4(key_data: String?, key_data1: String?): MutableList<Products>

    @Query("select pq from Products pq where pq.buyer_name = ?1")
    fun getBySeller(productseller: Long): MutableList<Products>

    @Query("select tq from Products tq where tq.id = ?1")
    fun getByRate(id: Long?):MutableList<Products>

}


@RestController
@RequestMapping("/home")
class UserController(val usersRepository: UsersRepository, val productsRepository: ProductRepository) {






    data class ret(
            val name: String? = null,
            val game:String? = null
    )

    data class register(
            val name:String? = null,
            val phone:String? = null,
            val password:String? = null,
            val address:String? = null,
            val userType:String? = null
    )


    @PostMapping("/register")
    fun register(@RequestBody reg:register):ret
    {

        try
        {
            val num = usersRepository.getByPhone(reg.phone)
            if (num.fullname!!.isEmpty()) {
                val kt = ret("User registered")
                return kt
            }
            else
            {
                val kt = ret("exist")

                return kt
            }
        }
        catch (e: Exception)
        {
            var tk = LocalDateTime.now().toString()+reg.phone
            tk = tk.replace(",","")
            tk = tk.replace(".","")
            tk = tk.replace(":","")
            tk = tk.replace("-","")
            val k = Users(0,reg.name,reg.phone,reg.password,reg.address,reg.userType,0,tk)
            usersRepository.save(k)
            val kt = ret(tk,reg.userType)
            return kt
        }
    }



    data class login(
            var name:String? = null,
            var pass:String? = null
    )



    @PostMapping("/order")
    fun order(@RequestBody ord:login):ret
    {
        try {

            val num = usersRepository.getByToken(ord.pass)

            if (num.fullname!!.isEmpty())
            {
                val kt = ret("login again")
                return kt
            }
            else
            {

                    var s = usersRepository.findById(num.id!!)
                    if (s.isPresent) {
                        var data = s.get()

                        var pr = Products(0,ord.name,"","pending","",data.id.toString(),data.fullname,data.phonenumber!!.toLong())

                        productsRepository.save(pr)

                        var k = ret("saved")
                        return k
                    } else {
                        val kt = ret("login again")
                        return kt
                    }

            }
        } catch (e: Exception) {

            val kt = ret("login again")
            return kt
        }
    }


    @PostMapping("/login")
    fun login2(@RequestBody lgn:login):ret
    {


        try {

            val num = usersRepository.getByPhone(lgn.name)

            if (num.fullname!!.isEmpty())
            {
                val kt = ret("user dosent exist")
                return kt
            }
            else
            {
                if (num.phonenumber == lgn.name && num.password == lgn.pass)
                {

                    var s = usersRepository.findById(num.id!!)
                    if (s.isPresent) {
                        var data = s.get()

                        var tk = LocalDateTime.now().toString() + lgn.name
                        tk = tk.replace(",", "")
                        tk = tk.replace(".", "")
                        tk = tk.replace(":", "")
                        tk = tk.replace("-", "")

                        var token = tk
                        data.token = token
                        usersRepository.save(data)
                        var k = ret(token,data.userType)
                        return k
                    } else {
                        val kt = ret("user dosent exist")
                        return kt
                    }
                }
                else
                {
                    val kt = ret("credienials dosent match")
                    return kt
                }
            }
        } catch (e: Exception) {

            val kt = ret("user dosent exist")
            return kt
        }
    }



    @PostMapping("/pending")
    fun pending(@RequestBody ter:ret): MutableList<Products>
    {
        var user = usersRepository.getByToken(ter.name)

        try {
            if (user.userType == "customer")
            {
                return productsRepository.getByKey(user.id.toString(),"pending")
            }
            else
            {
                return productsRepository.getByKey3("pending")
            }
        }catch (e:Exception)
        {
            return Collections.emptyList()
        }
    }

    @PostMapping("/approved")
    fun approved(@RequestBody ter:ret): MutableList<Products> {
        var user = usersRepository.getByToken(ter.name)

        if(ter.game == "customer")
        {
            return productsRepository.getByKey(user.id.toString(),"approved")
        }
        else
        {
            ;
        }

        return productsRepository.getByKey4(user.id.toString(),"approved")
    }


    @PostMapping("/approv/{id}")
    fun approv(@PathVariable id:String, @RequestBody ter:ret) {

        var usr = usersRepository.getByToken(ter.name)

        if (usr.userType == "importer")
        {
            var prd = productsRepository.findById(id.toLong())
            var data = prd.get()

            data.productprice = usr.id.toString()
            data.product_status = "approved"
            data.product_price = ter.game

            productsRepository.save(data)
        }

    }

    @PostMapping("/profile")
    fun profile(@RequestBody ter:ret): Users {
        var usr = usersRepository.getByToken(ter.name)
        return usr
    }


    @GetMapping("/Allusers1")
    fun getAll1(): MutableList<Users> {

        var myr = usersRepository.findAll()

        return myr

    }

    @GetMapping("/Allusers2")
    fun getAll2(): Users? {

        var k = usersRepository.findAll()
        //var b:Users? = null

        k.forEach {

            return it

        }
        return null
    }


    class mypro(var sin_id: Long)

    @PostMapping("/ProductClick")
    fun ProductClick(@RequestBody sin_data: mypro): Products? {
        var myt = productsRepository.findById(sin_data.sin_id.toLong())
        var myq = Products()
        myq = myt.get()
        return myq
    }


    class mylog(var sin_number: String, var sin_pass: String)

    @PostMapping("/Login")
    fun Login(@RequestBody sin_data: mylog): Users? {

        var myr = usersRepository.findAll()
        //var myt = Users()

        for (item in myr) {
            if (sin_data.sin_number == item.phonenumber && sin_data.sin_pass == item.password && item.state == 1) {
                return item
            }
        }
        return null
    }





    class rate(var rate_id:Long,var rating_1:Int, var flag:Int)



//    class rate1(var rate_id:Long,var rating_1:Int)
//
//    @PostMapping("/Rate1")
//    fun myRate1(@RequestBody my_rate1:rate1):Boolean
//    {
//        var p = productsRepository.findById(my_rate1.rate_id)
//        var data = Products()
//        if (p.isPresent)
//        {
//            if (data.rating!=0)
//            {
//                if (data.rating!! ==100)
//                {
//                    data = p.get()
//                    var t = data.rating
//
//                    data.rating = t
//                    //data.product_amount = pro_up.up_amount
//                    productsRepository.save(data)
//                    return true
//                }
//                else if(data.rating == -100)
//                {
//                    data = p.get()
//                    var t = data.rating
//                    data.rating = t
//                    //data.product_amount = pro_up.up_amount
//                    productsRepository.save(data)
//                    return true
//                }
//            }
//            else
//            {
//                data = p.get()
//                var t = data.rating
//                var s = my_rate1.rating_1 + t!!
//                data.rating = s
//                //data.product_amount = pro_up.up_amount
//                productsRepository.save(data)
//                return true
//            }
//            //return "save"
//            return true
//        }
//        else
//            return false
//
//    }






    class my_pro(var my_product: Long)

    @PostMapping("/my_product")
    fun my_product(@RequestBody pro_my: my_pro): MutableList<Products>? {
//        var k = productsRepository.findAll()
//        var f = ArrayList<Products>()
//
//        for (i in k)
//        {
//            if (i.id == pro_my.my_product)
//            {
//                f.add(i)
//                return f
//            }
//        }
//        return f
        var t = productsRepository.getBySeller(pro_my.my_product)
        return t

    }




    @DeleteMapping("/delete_product")
    fun delete_product(@RequestBody product_id: Long): String {
        productsRepository.deleteById(product_id)
        return "hoise"
    }


    class test_data(var search_names: String)

    @PostMapping("/my_search")
    fun my_search(@RequestBody data_test: test_data): MutableList<Products> {
//        var k = productsRepository.findAll()
//
//        for (i in k)
//        {
//            if (i.key_data == data_test.names)
//            {
//                return i
//            }
//        }
//
//        return null

        var p = productsRepository.getByKey(data_test.search_names,"")
        return p

        //return productsRepository.findAll()
    }


    @PostMapping("/my_search2")
    fun my_search2(@RequestBody data_test: test_data): MutableList<Products> {
//        var k = productsRepository.findAll()
//
//        for (i in k)
//        {
//            if (i.key_data == data_test.names)
//            {
//                return i
//            }
//        }
//
//        return null

        var p = productsRepository.getByKey2(data_test.search_names)
        return p

        //return productsRepository.findAll()
    }


    class bal(var pro_id: Long)

    @PostMapping("/delete_2")
    fun del(@RequestBody mybal: bal): String {
        var p = productsRepository.findById(mybal.pro_id)
        var data = p.get()
        productsRepository.delete(data)
        return "hoise"
    }


//    @PostMapping("/deleteType2")
//    fun del3(@RequestBody mybal: bal): String {
//        var p = products_typeRepository.findById(mybal.pro_id)
//        var data = p.get()
//        products_typeRepository.delete(data)
//        return "hoise"
//    }


//    class mybal4(var nameType:Long)
//    @PostMapping("/deleteType")
//    fun deletetype(@RequestBody product_type:mybal4):String
//    {
//
//        var p = products_typeRepository.findById(product_type.nameType)
//        var data = p.get()
//        products_typeRepository.delete(data)
//        return "hoise"
//
//    }


    class bal2(var pro_id: Long)

//    @PostMapping("/delete_4")
//    fun del2(@RequestBody mybal: bal): String {
//        var p = product_extendRepository.findById(mybal.pro_id)
//        var data = p.get()
//        product_extendRepository.delete(data)
//        return "hoise"
//    }

    class bal4(var pro_id: Long)

//    @PostMapping("/delete_6")
//    fun del4(@RequestBody mybal: bal): String {
//        var t = product_extendRepository.getByThat(mybal.pro_id)
//        product_extendRepository.deleteAll(t)
//        return "hoise"
//    }


    @PostMapping("/LoginNew")
    fun LoginNew(@RequestBody sin_data: mylog): Users? {
        var users = usersRepository.getUsersByPhonenumberAndPassword(sin_data.sin_number, sin_data.sin_pass)

        return users
    }


    class mylog1(var sin_id: Long)

    @PostMapping("/Login1")
    fun Login1(@RequestBody sin_data: mylog1): Users? {

        var myr = usersRepository.findById(sin_data.sin_id.toLong())
        var myt = Users()
        myt = myr.get()
        return myt
    }


    @GetMapping("/Allusers")
    fun getAll(): MutableList<Users> {
        return usersRepository.findAll()
    }


    @GetMapping("/Allproducts")
    fun getAllProduct(): MutableList<Products> {

        var myr = productsRepository.findAll()

        return myr
    }


    data class Data1(val id: Long? = null, var productbuyer: Long? = null, var productdetail: String? = null, var productname: String? = null, var productprice: String? = null, var productseller: Long? = null, var producttype: String? = null, var seller_name: String? = null, var seller_number: Long? = null)


    @GetMapping("/Allproducts1")
    fun getAllProduct2(): MutableList<Products>? {

        var myr = productsRepository.findAll()
        //myr.asReversed()

        return myr.asReversed()
    }



    class search_data(var productnames: String? = null)

    @PostMapping("/SearchProduct")
    fun searchProduct(@RequestBody mydata: String, mydata1: search_data): Users {
        //var myP: Optional<Products> = productsRepository.findById(mydata1.productnames!!)


        val myP: Optional<Users> = usersRepository.findById(mydata.toLong())
        var data = Users()
        if (myP.isPresent) {
            data = myP.get()
            return data
        } else {
            var data = Users()
            return data
        }

    }


    class search_data2(var productnames: MutableList<Users>)

    @PostMapping("/SearchProduct2")
    fun searchProduct2(@RequestBody mydata: String): Users? {
        //var myP: Optional<Products> = productsRepository.findById(mydata1.productnames!!)

        var myq = usersRepository.findAll()
        myq.forEach {
            if (it.fullname == mydata) {
                return it
            }
        }
        return null
    }



    @PostMapping("/SearchProduct6")
    fun searchProduct6(@RequestBody mydata: String): Users? {
        //var myP: Optional<Products> = productsRepository.findById(mydata1.productnames!!)


        var myq = Products()
        var myr = usersRepository.findAll()

        for (items in myr) {
            if (items.fullname == mydata) {
                return items
            }

        }
        return null
    }


    class search_data1(var productid: Long? = null, var userid: Long? = null)

    @PostMapping("/UpdateProducts")
    fun updateProduct(@RequestBody mydata: search_data) {
        //var myP: Optional<Products> = productsRepository.findById(mydata.userid!!)
        //var data = Products()
        //if (myP.isPresent) {
        //       data = myP.get()
        //    data.productbuyer = mydata.userid!!
        //    return productsRepository.save(data)
        //}
        //return data

        //return productsRepository.equals(mydata.productnames==data.productname || mydata.productnames == data.keyword)
    }







    class crew(var prodid: Long? = null)

    @PostMapping("/removeProduct")
    fun removeProduct(@RequestBody dataa: crew): Products {
        var myP: Optional<Products> = productsRepository.findById(dataa.prodid!!)
        var data = Products()
        if (myP.isPresent) {
            data = myP.get()
            //data.productbuyer = 0
            //data.rating = null
            return productsRepository.save(data)
        }
        return data
    }


    class rat(var proid: Long? = null, var ratings: String? = null)

    @PostMapping("/rateProduct")
    fun productRate(@RequestBody info: rat): Products {
        var myP: Optional<Products> = productsRepository.findById(info.proid!!)
        var data = Products()
        if (myP.isPresent) {
            data = myP.get()
            //data.rating = info.ratings!!
            return productsRepository.save(data)
        }
        return data
    }


    @PostMapping("/saveuser")
    fun saveUsers(@RequestBody users: Users): String {
        print(users.fullname)
        usersRepository.save(users)
        return " Hoise "
    }



    @PostMapping("/sellproduct")
    fun saveUsers(@RequestBody products: Products): String {
        productsRepository.save(products)
        return " Hoise "
    }

    /*@PostMapping("/Lgin")
    fun lohin(@RequestBody products: LogingClass): Users {
        return usersRepository.findinLog(products)

    }*/

    @PostMapping("/updateuser")
    fun updateUsers(@RequestBody users: Users): String {
        print(users.fullname)
        usersRepository.save(users)
        return " Hoise "
    }

}