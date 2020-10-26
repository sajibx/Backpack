package com.example.demo

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping


@Controller
class WebControllerForNoob() {


//    @RequestMapping("/home" , "/delelte")
//    fun home(model: Model): String {
//
//
//
//        val list = productRepository.findAll()
//
//        model.addAttribute("allProduct", list)
//
//        return "home"
//    }

    @RequestMapping("/home.html")
    fun home():String {
        return "home"
    }
    @RequestMapping("/demo")
    fun homedemo():String {
        return "demo"
    }

    @RequestMapping("/checkout.html")
    fun profile(): String {
        return "checkout.html"
    }

    @RequestMapping("/my_product.html")
    fun my_product(): String {
        return "my_product.html"
    }
/*
    @RequestMapping("product")
    fun product(model : Model,@RequestParam("id", required = true) productId: String): String {

        val findById = productRepository.findById(productId.toLong())


        findById.let {
            it.get().let {product->
                model.addAttribute("product" , product)

            }

        }
        return "product"
    }*/

    @RequestMapping("/product.html")
    fun product():String
    {
        return "product.html"
    }

    @RequestMapping("/product_home.html")
    fun product_home(): String {
        return "product_home.html"
    }

    @RequestMapping("/sell_demo.html")
    fun sell_product(): String {
        return "sell_demo.html"
    }

    @RequestMapping("/sold_product.html")
    fun sold_product(): String {
        return "sold_product.html"
    }

    @RequestMapping("/signup.html")
    fun signup(): String {
        return "signup.html"
    }
}