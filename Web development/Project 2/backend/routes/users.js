const router = require("express").Router();
const User = require("../models/users");
const Post = require("../models/posts");

router.get("/",(req,res)=>{
    res.send("Hey, its user routes");
})

//Update user

//Delete user

//get user

//follower user

//unfollw a user

module.exports =router;