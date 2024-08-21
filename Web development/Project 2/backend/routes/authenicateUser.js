//This file will be used to authenicate users with the aid of controllers
const router = require("express").Router();
const Controllers = require("../controller/auth");// controllers
const auth = require("../middleware/authenicate");

router.get("/", (req, res) => {
    res.send("Hey, its authenicate route");
})

//register user
router.post("/register", Controllers.controllers.registerUser);


//Implement user login
router.post("/userlogin", Controllers.controllers.verifyUser);

//test jwt
router.get("/test",auth,(req,res)=>{
    res.send("Successfully checked token")
})

module.exports =router;